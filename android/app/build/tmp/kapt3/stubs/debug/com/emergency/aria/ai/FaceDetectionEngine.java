package com.emergency.aria.ai;

/**
 * FACE DETECTION ENGINE
 * ──────────────────────
 * Uses Google ML Kit (on-device, no internet) to detect:
 *
 *  1. PANIC / FEAR — open mouth + wide eyes + head movement
 *  2. FALL          — rapid pitch/roll change (combined with accelerometer)
 *  3. UNCONSCIOUS   — no face detected for >5 seconds after fall
 *
 * Trigger flow:
 *  Camera frame → ML Kit detection → analyze expressions
 *  → if PANIC or FALL detected → emit DetectionEvent
 *  → CommunicationManager.sendEmergencyAlert()
 */
@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\u0016\u0010\u001b\u001a\u00020\u001c2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00180\u001eH\u0002J\b\u0010\u001f\u001a\u00020\u001cH\u0002J\u0006\u0010 \u001a\u00020\u001cJ\u001f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00180\u001e2\u0006\u0010\"\u001a\u00020#H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010$J\u0006\u0010%\u001a\u00020\u001cR\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0006\u001a\u00020\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\tR\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006&"}, d2 = {"Lcom/emergency/aria/ai/FaceDetectionEngine;", "", "()V", "_events", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/emergency/aria/ai/DetectionEvent;", "detector", "Lcom/google/mlkit/vision/face/FaceDetector;", "getDetector", "()Lcom/google/mlkit/vision/face/FaceDetector;", "detector$delegate", "Lkotlin/Lazy;", "events", "Lkotlinx/coroutines/flow/SharedFlow;", "getEvents", "()Lkotlinx/coroutines/flow/SharedFlow;", "fallDetectedMs", "", "lastFaceSeenMs", "panicFrameCount", "", "previousHeadAngle", "", "analyzeFace", "Lcom/emergency/aria/ai/FaceAnalysis;", "face", "Lcom/google/mlkit/vision/face/Face;", "analyzeScene", "", "analyses", "", "checkForNoFace", "notifyFallDetected", "processFrame", "bitmap", "Landroid/graphics/Bitmap;", "(Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "release", "app_debug"})
public final class FaceDetectionEngine {
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy detector$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.emergency.aria.ai.DetectionEvent> _events = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.ai.DetectionEvent> events = null;
    private long lastFaceSeenMs = 0L;
    private int panicFrameCount = 0;
    private long fallDetectedMs = 0L;
    private float previousHeadAngle = 0.0F;
    
    @javax.inject.Inject
    public FaceDetectionEngine() {
        super();
    }
    
    private final com.google.mlkit.vision.face.FaceDetector getDetector() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.ai.DetectionEvent> getEvents() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object processFrame(@org.jetbrains.annotations.NotNull
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.emergency.aria.ai.FaceAnalysis>> $completion) {
        return null;
    }
    
    private final com.emergency.aria.ai.FaceAnalysis analyzeFace(com.google.mlkit.vision.face.Face face) {
        return null;
    }
    
    private final void analyzeScene(java.util.List<com.emergency.aria.ai.FaceAnalysis> analyses) {
    }
    
    private final void checkForNoFace() {
    }
    
    /**
     * Called by AccelerometerService when fall is detected
     */
    public final void notifyFallDetected() {
    }
    
    public final void release() {
    }
}