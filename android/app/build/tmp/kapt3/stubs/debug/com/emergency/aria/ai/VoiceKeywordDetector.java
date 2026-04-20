package com.emergency.aria.ai;

/**
 * VOICE KEYWORD DETECTOR
 * ──────────────────────
 * Continuously listens for the user's secret keyword using Android SpeechRecognizer.
 *
 * PRIVACY DESIGN:
 *  - Keyword comparison is fully LOCAL — no audio sent to server
 *  - Only the BCrypt hash is stored (same as backend)
 *  - When keyword matches → silent emergency trigger
 *  - No visual feedback (to avoid alerting attacker)
 *
 * FLOW:
 *  startListening() → SpeechRecognizer onResults()
 *  → normalize spoken text → compare with stored keyword
 *  → if match → emit KeywordEvent.TRIGGERED
 */
@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\n\b\u0007\u0018\u0000 &2\u00020\u0001:\u0001&B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u0012H\u0002J\u0006\u0010\u0019\u001a\u00020\u0016J\b\u0010\u001a\u001a\u00020\u0016H\u0002J\u0006\u0010\u001b\u001a\u00020\tJ\u0018\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u0012H\u0002J\u0018\u0010 \u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u00122\u0006\u0010\u001f\u001a\u00020\u0012H\u0002J\u000e\u0010!\u001a\u00020\u00162\u0006\u0010\"\u001a\u00020\u0012J\u0010\u0010#\u001a\u00020\u00162\b\b\u0002\u0010$\u001a\u00020\tJ\u0006\u0010%\u001a\u00020\u0016R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/emergency/aria/ai/VoiceKeywordDetector;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_events", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/emergency/aria/ai/VoiceEvent;", "continuousMode", "", "events", "Lkotlinx/coroutines/flow/SharedFlow;", "getEvents", "()Lkotlinx/coroutines/flow/SharedFlow;", "isListening", "recognitionListener", "Landroid/speech/RecognitionListener;", "secretKeyword", "", "speechRecognizer", "Landroid/speech/SpeechRecognizer;", "beginRecognition", "", "checkKeyword", "recognizedText", "clearKeyword", "createRecognizer", "hasKeyword", "levenshtein", "", "a", "b", "phoneticSimilarity", "setKeyword", "keyword", "startListening", "continuous", "stopListening", "Companion", "app_debug"})
public final class VoiceKeywordDetector {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable
    private android.speech.SpeechRecognizer speechRecognizer;
    @org.jetbrains.annotations.Nullable
    private java.lang.String secretKeyword;
    private boolean isListening = false;
    private boolean continuousMode = false;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.emergency.aria.ai.VoiceEvent> _events = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.ai.VoiceEvent> events = null;
    @org.jetbrains.annotations.NotNull
    private final android.speech.RecognitionListener recognitionListener = null;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG = "VoiceKeywordDetector";
    @org.jetbrains.annotations.NotNull
    public static final com.emergency.aria.ai.VoiceKeywordDetector.Companion Companion = null;
    
    @javax.inject.Inject
    public VoiceKeywordDetector(@dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.ai.VoiceEvent> getEvents() {
        return null;
    }
    
    /**
     * Set the secret keyword (stored in memory only, never persisted).
     * The hash is sent to the server for backup verification.
     */
    public final void setKeyword(@org.jetbrains.annotations.NotNull
    java.lang.String keyword) {
    }
    
    public final void clearKeyword() {
    }
    
    public final boolean hasKeyword() {
        return false;
    }
    
    public final void startListening(boolean continuous) {
    }
    
    public final void stopListening() {
    }
    
    private final void createRecognizer() {
    }
    
    private final void beginRecognition() {
    }
    
    private final void checkKeyword(java.lang.String recognizedText) {
    }
    
    /**
     * Simple phonetic similarity — catches cases like "help one two three"
     * vs "help123" by stripping numbers-as-words.
     */
    private final float phoneticSimilarity(java.lang.String a, java.lang.String b) {
        return 0.0F;
    }
    
    private final float levenshtein(java.lang.String a, java.lang.String b) {
        return 0.0F;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/emergency/aria/ai/VoiceKeywordDetector$Companion;", "", "()V", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}