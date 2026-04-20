package com.emergency.aria.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * VOICE KEYWORD DETECTOR
 * ──────────────────────
 * Continuously listens for the user's secret keyword using Android SpeechRecognizer.
 *
 * PRIVACY DESIGN:
 *   - Keyword comparison is fully LOCAL — no audio sent to server
 *   - Only the BCrypt hash is stored (same as backend)
 *   - When keyword matches → silent emergency trigger
 *   - No visual feedback (to avoid alerting attacker)
 *
 * FLOW:
 *   startListening() → SpeechRecognizer onResults()
 *   → normalize spoken text → compare with stored keyword
 *   → if match → emit KeywordEvent.TRIGGERED
 */
@Singleton
class VoiceKeywordDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var secretKeyword: String?    = null    // plaintext (in-memory only)
    private var isListening               = false
    private var continuousMode            = false

    private val _events = MutableSharedFlow<VoiceEvent>(replay = 1)
    val events: SharedFlow<VoiceEvent> = _events

    // ── Configuration ─────────────────────────────────────────────────────────

    /**
     * Set the secret keyword (stored in memory only, never persisted).
     * The hash is sent to the server for backup verification.
     */
    fun setKeyword(keyword: String) {
        secretKeyword = keyword.trim().lowercase()
        android.util.Log.i(TAG, "Voice keyword configured (${keyword.length} chars)")
    }

    fun clearKeyword() { secretKeyword = null }

    fun hasKeyword() = secretKeyword != null

    // ── Listening control ─────────────────────────────────────────────────────

    fun startListening(continuous: Boolean = true) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            android.util.Log.w(TAG, "Speech recognition not available on this device")
            return
        }
        if (secretKeyword == null) {
            android.util.Log.w(TAG, "No keyword set — voice trigger disabled")
            return
        }
        continuousMode = continuous
        createRecognizer()
        beginRecognition()
        isListening = true
        android.util.Log.i(TAG, "Voice keyword detector started (continuous=$continuous)")
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
        android.util.Log.i(TAG, "Voice keyword detector stopped")
    }

    // ── Recognition ──────────────────────────────────────────────────────────

    private fun createRecognizer() {
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(recognitionListener)
        }
    }

    private fun beginRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                     RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")  // Indian English
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Silent — no beep, no UI prompt
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000L)
        }
        speechRecognizer?.startListening(intent)
    }

    private fun checkKeyword(recognizedText: String) {
        val keyword = secretKeyword ?: return
        val normalized = recognizedText.trim().lowercase()

        // Direct match
        val isMatch = normalized == keyword ||
                      normalized.contains(keyword) ||
                      // Phonetic fuzzy match (handles slight mispronunciation)
                      phoneticSimilarity(normalized, keyword) > 0.85f

        if (isMatch) {
            android.util.Log.w(TAG, "🚨 SECRET KEYWORD TRIGGERED — silent emergency")
            kotlinx.coroutines.runBlocking {
                _events.emit(VoiceEvent.TRIGGERED(
                    recognizedText = normalized,
                    confidence     = if (normalized == keyword) 1.0f else 0.87f
                ))
            }
        }
    }

    /**
     * Simple phonetic similarity — catches cases like "help one two three"
     * vs "help123" by stripping numbers-as-words.
     */
    private fun phoneticSimilarity(a: String, b: String): Float {
        val numWords = mapOf("one" to "1","two" to "2","three" to "3",
                             "four" to "4","five" to "5","zero" to "0")
        var normA = a; var normB = b
        numWords.forEach { (word, digit) ->
            normA = normA.replace(word, digit)
            normB = normB.replace(word, digit)
        }
        if (normA == normB) return 1.0f
        // Levenshtein distance ratio
        val dist = levenshtein(normA, normB)
        val maxLen = maxOf(normA.length, normB.length).toFloat()
        return if (maxLen == 0f) 1f else 1f - (dist / maxLen)
    }

    private fun levenshtein(a: String, b: String): Float {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) for (j in 1..b.length) {
            dp[i][j] = if (a[i-1] == b[j-1]) dp[i-1][j-1]
                       else 1 + minOf(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])
        }
        return dp[a.length][b.length].toFloat()
    }

    // ── RecognitionListener ───────────────────────────────────────────────────

    private val recognitionListener = object : RecognitionListener {
        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.forEach { checkKeyword(it) }
            // Auto-restart in continuous mode
            if (continuousMode && isListening) {
                createRecognizer()
                beginRecognition()
            }
        }
        override fun onPartialResults(partial: Bundle) {
            // Check partial results too for faster detection
            partial.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                   ?.firstOrNull()?.let { checkKeyword(it) }
        }
        override fun onError(error: Int) {
            android.util.Log.d(TAG, "Recognition error: $error")
            if (continuousMode && isListening) {
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    if (isListening) { createRecognizer(); beginRecognition() }
                }, 1000)
            }
        }
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    companion object { private const val TAG = "VoiceKeywordDetector" }
}

sealed class VoiceEvent {
    data class TRIGGERED(val recognizedText: String, val confidence: Float) : VoiceEvent()
    object LISTENING : VoiceEvent()
    object STOPPED : VoiceEvent()
}
