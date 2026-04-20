package com.emergency.aria.ai;

/**
 * On-device AI detection manager.
 *
 * 1. FACE DETECTION (ML Kit) — detects:
 *   - Fear/panic: open mouth + wide eyes + head tilt
 *   - Distress: eye openness probability drops (closed eyes → unconscious)
 *
 * 2. FALL DETECTION (Accelerometer) — detects:
 *   - Sudden high-G impact followed by stillness
 *   - Algorithm: threshold spike + sustained low movement
 *
 * 3. SERVER VALIDATION (optional) — sends ambiguous cases to Claude Vision
 *
 * Integration:
 *  val detector = AiDetectionManager(context)
 *  detector.startCamera()  // starts ML Kit face detection pipeline
 *  detector.feedAccelerometerData(event)  // call from SensorEventListener
 *  detector.detectionEvents.collect { event -> handleEvent(event) }
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0002&\'B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dJ\u0006\u0010\u001e\u001a\u00020\u001bJ\u000e\u0010\u001f\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020!J\u001e\u0010\"\u001a\u00020\u001b2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020%0$2\u0006\u0010\u001c\u001a\u00020\u001dH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R \u0010\u0018\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/emergency/aria/ai/AiDetectionManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "IMPACT_THRESHOLD", "", "POST_IMPACT_WINDOW", "", "STILLNESS_THRESHOLD", "_events", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/emergency/aria/ai/AiDetectionManager$DetectionEvent;", "detectionEvents", "Lkotlinx/coroutines/flow/SharedFlow;", "getDetectionEvents", "()Lkotlinx/coroutines/flow/SharedFlow;", "faceDetector", "Lcom/google/mlkit/vision/face/FaceDetector;", "faceDetectorOptions", "Lcom/google/mlkit/vision/face/FaceDetectorOptions;", "impactTime", "", "postImpactSamples", "previousAccel", "Lkotlin/Triple;", "analyzeFaceFrame", "", "bitmap", "Landroid/graphics/Bitmap;", "destroy", "feedAccelerometerData", "event", "Landroid/hardware/SensorEvent;", "processFaces", "faces", "", "Lcom/google/mlkit/vision/face/Face;", "DetectionEvent", "DetectionType", "app_debug"})
public final class AiDetectionManager {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.emergency.aria.ai.AiDetectionManager.DetectionEvent> _events = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.ai.AiDetectionManager.DetectionEvent> detectionEvents = null;
    @org.jetbrains.annotations.NotNull
    private final com.google.mlkit.vision.face.FaceDetectorOptions faceDetectorOptions = null;
    @org.jetbrains.annotations.NotNull
    private final com.google.mlkit.vision.face.FaceDetector faceDetector = null;
    @org.jetbrains.annotations.NotNull
    private kotlin.Triple<java.lang.Float, java.lang.Float, java.lang.Float> previousAccel;
    private long impactTime = 0L;
    private int postImpactSamples = 0;
    private final float IMPACT_THRESHOLD = 25.0F;
    private final float STILLNESS_THRESHOLD = 3.0F;
    private final int POST_IMPACT_WINDOW = 50;
    
    public AiDetectionManager(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.ai.AiDetectionManager.DetectionEvent> getDetectionEvents() {
        return null;
    }
    
    /**
     * Analyze a camera frame for panic/fear/unconsciousness.
     * Call this on every camera frame (or every N frames for performance).
     */
    public final void analyzeFaceFrame(@org.jetbrains.annotations.NotNull
    android.graphics.Bitmap bitmap) {
    }
    
    private final void processFaces(java.util.List<? extends com.google.mlkit.vision.face.Face> faces, android.graphics.Bitmap bitmap) {
    }
    
    /**
     * Feed raw accelerometer data.
     * Call from SensorEventListener.onSensorChanged() with TYPE_ACCELEROMETER.
     */
    public final void feedAccelerometerData(@org.jetbrains.annotations.NotNull
    android.hardware.SensorEvent event) {
    }
    
    public final void destroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010\u0016\u001a\u0004\u0018\u00010\tH\u00c6\u0003J3\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u00c6\u0001J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u0007H\u00d6\u0001R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001e"}, d2 = {"Lcom/emergency/aria/ai/AiDetectionManager$DetectionEvent;", "", "type", "Lcom/emergency/aria/ai/AiDetectionManager$DetectionType;", "confidence", "", "description", "", "bitmap", "Landroid/graphics/Bitmap;", "(Lcom/emergency/aria/ai/AiDetectionManager$DetectionType;FLjava/lang/String;Landroid/graphics/Bitmap;)V", "getBitmap", "()Landroid/graphics/Bitmap;", "getConfidence", "()F", "getDescription", "()Ljava/lang/String;", "getType", "()Lcom/emergency/aria/ai/AiDetectionManager$DetectionType;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class DetectionEvent {
        @org.jetbrains.annotations.NotNull
        private final com.emergency.aria.ai.AiDetectionManager.DetectionType type = null;
        private final float confidence = 0.0F;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String description = null;
        @org.jetbrains.annotations.Nullable
        private final android.graphics.Bitmap bitmap = null;
        
        public DetectionEvent(@org.jetbrains.annotations.NotNull
        com.emergency.aria.ai.AiDetectionManager.DetectionType type, float confidence, @org.jetbrains.annotations.NotNull
        java.lang.String description, @org.jetbrains.annotations.Nullable
        android.graphics.Bitmap bitmap) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.emergency.aria.ai.AiDetectionManager.DetectionType getType() {
            return null;
        }
        
        public final float getConfidence() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getDescription() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
        public final android.graphics.Bitmap getBitmap() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.emergency.aria.ai.AiDetectionManager.DetectionType component1() {
            return null;
        }
        
        public final float component2() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
        public final android.graphics.Bitmap component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.emergency.aria.ai.AiDetectionManager.DetectionEvent copy(@org.jetbrains.annotations.NotNull
        com.emergency.aria.ai.AiDetectionManager.DetectionType type, float confidence, @org.jetbrains.annotations.NotNull
        java.lang.String description, @org.jetbrains.annotations.Nullable
        android.graphics.Bitmap bitmap) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/emergency/aria/ai/AiDetectionManager$DetectionType;", "", "(Ljava/lang/String;I)V", "FALL_DETECTED", "PANIC_FACE", "UNCONSCIOUS_FACE", "NONE", "app_debug"})
    public static enum DetectionType {
        /*public static final*/ FALL_DETECTED /* = new FALL_DETECTED() */,
        /*public static final*/ PANIC_FACE /* = new PANIC_FACE() */,
        /*public static final*/ UNCONSCIOUS_FACE /* = new UNCONSCIOUS_FACE() */,
        /*public static final*/ NONE /* = new NONE() */;
        
        DetectionType() {
        }
        
        @org.jetbrains.annotations.NotNull
        public static kotlin.enums.EnumEntries<com.emergency.aria.ai.AiDetectionManager.DetectionType> getEntries() {
            return null;
        }
    }
}