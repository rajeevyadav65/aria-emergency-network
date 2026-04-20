package com.emergency.aria.ai;

/**
 * FALL DETECTION SERVICE
 * ───────────────────────
 * Uses accelerometer + gyroscope to detect sudden falls.
 *
 * ALGORITHM (two-phase):
 *  Phase 1 — FREE FALL:     Total acceleration drops below 3 m/s² for >80ms
 *  Phase 2 — IMPACT:        Total acceleration spikes above 25 m/s² within 500ms
 *
 * This combination (free fall + impact) has ~92% precision in studies.
 * False positives (jumping, dropping phone) are filtered by Phase 2 timing.
 *
 * After fall detection:
 *  → FaceDetectionEngine.notifyFallDetected()
 *  → 10-second countdown shown to user ("Are you OK?")
 *  → if no response → auto-trigger emergency
 */
@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u0000 .2\u00020\u0001:\u0001.B\u0019\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u001c\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u00132\u0006\u0010\u001e\u001a\u00020\u0013H\u0002J\u0018\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u00132\u0006\u0010\"\u001a\u00020\u0013H\u0002J\u001a\u0010#\u001a\u00020 2\b\u0010$\u001a\u0004\u0018\u00010\u000b2\u0006\u0010%\u001a\u00020&H\u0016J\u0010\u0010\'\u001a\u00020 2\u0006\u0010(\u001a\u00020)H\u0016J\u0010\u0010*\u001a\u00020 2\u0006\u0010(\u001a\u00020)H\u0002J\u0010\u0010+\u001a\u00020 2\u0006\u0010(\u001a\u00020)H\u0002J\u0006\u0010,\u001a\u00020 J\u0006\u0010-\u001a\u00020 R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006/"}, d2 = {"Lcom/emergency/aria/ai/FallDetectionService;", "Landroid/hardware/SensorEventListener;", "context", "Landroid/content/Context;", "faceDetectionEngine", "Lcom/emergency/aria/ai/FaceDetectionEngine;", "(Landroid/content/Context;Lcom/emergency/aria/ai/FaceDetectionEngine;)V", "_fallEvents", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/emergency/aria/ai/FallEvent;", "accelerometer", "Landroid/hardware/Sensor;", "fallEvents", "Lkotlinx/coroutines/flow/SharedFlow;", "getFallEvents", "()Lkotlinx/coroutines/flow/SharedFlow;", "freeFallStartMs", "", "gyroX", "", "gyroY", "gyroZ", "gyroscope", "inFreeFall", "", "lastImpactMs", "sensorManager", "Landroid/hardware/SensorManager;", "calculateConfidence", "impact", "rotation", "confirmFall", "", "impactG", "rotationRate", "onAccuracyChanged", "sensor", "accuracy", "", "onSensorChanged", "event", "Landroid/hardware/SensorEvent;", "processAccelerometer", "processGyroscope", "start", "stop", "Companion", "app_debug"})
public final class FallDetectionService implements android.hardware.SensorEventListener {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    private final com.emergency.aria.ai.FaceDetectionEngine faceDetectionEngine = null;
    @org.jetbrains.annotations.NotNull
    private final android.hardware.SensorManager sensorManager = null;
    @org.jetbrains.annotations.Nullable
    private final android.hardware.Sensor accelerometer = null;
    @org.jetbrains.annotations.Nullable
    private final android.hardware.Sensor gyroscope = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.emergency.aria.ai.FallEvent> _fallEvents = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.ai.FallEvent> fallEvents = null;
    private long freeFallStartMs = 0L;
    private boolean inFreeFall = false;
    private long lastImpactMs = 0L;
    private float gyroX = 0.0F;
    private float gyroY = 0.0F;
    private float gyroZ = 0.0F;
    public static final float FREE_FALL_THRESHOLD = 3.0F;
    public static final float IMPACT_THRESHOLD = 25.0F;
    public static final long FREE_FALL_MIN_MS = 80L;
    public static final long IMPACT_WINDOW_MS = 500L;
    public static final float STATIONARY_THRESHOLD = 2.0F;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG = "FallDetection";
    @org.jetbrains.annotations.NotNull
    public static final com.emergency.aria.ai.FallDetectionService.Companion Companion = null;
    
    @javax.inject.Inject
    public FallDetectionService(@dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    com.emergency.aria.ai.FaceDetectionEngine faceDetectionEngine) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.ai.FallEvent> getFallEvents() {
        return null;
    }
    
    public final void start() {
    }
    
    public final void stop() {
    }
    
    @java.lang.Override
    public void onSensorChanged(@org.jetbrains.annotations.NotNull
    android.hardware.SensorEvent event) {
    }
    
    private final void processAccelerometer(android.hardware.SensorEvent event) {
    }
    
    private final void processGyroscope(android.hardware.SensorEvent event) {
    }
    
    private final void confirmFall(float impactG, float rotationRate) {
    }
    
    private final float calculateConfidence(float impact, float rotation) {
        return 0.0F;
    }
    
    @java.lang.Override
    public void onAccuracyChanged(@org.jetbrains.annotations.Nullable
    android.hardware.Sensor sensor, int accuracy) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/emergency/aria/ai/FallDetectionService$Companion;", "", "()V", "FREE_FALL_MIN_MS", "", "FREE_FALL_THRESHOLD", "", "IMPACT_THRESHOLD", "IMPACT_WINDOW_MS", "STATIONARY_THRESHOLD", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}