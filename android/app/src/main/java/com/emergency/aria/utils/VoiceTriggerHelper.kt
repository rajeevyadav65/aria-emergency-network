package com.emergency.aria.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.*

class VoiceTriggerHelper(private val context: Context, private val onTrigger: () -> Unit) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    fun startListening() {
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.forEach { text ->
                        Log.d("VOICE_TRIGGER", "Heard: $text")
                        if (text.contains("help", true) || text.contains("bachao", true) || text.contains("emergency", true)) {
                            onTrigger()
                        }
                    }
                    startListening()
                }

                override fun onError(error: Int) {
                    Log.e("VOICE_TRIGGER", "Error: $error")
                    startListening()
                }

                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            speechRecognizer?.startListening(recognizerIntent)
        } catch (e: Exception) {
            Log.e("VOICE_TRIGGER", "Critical failure: ${e.message}")
        }
    }

    fun stop() {
        speechRecognizer?.destroy()
    }
}