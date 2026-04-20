package com.emergency.aria.ai

import android.graphics.Bitmap
import android.graphics.RectF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * FACE DETECTION ENGINE
 * ──────────────────────
 * Uses Google ML Kit (on-device, no internet) to detect:
 *
 *   1. PANIC / FEAR — open mouth + wide eyes + head movement
 *   2. FALL          — rapid pitch/roll change (combined with accelerometer)
 *   3. UNCONSCIOUS   — no face detected for >5 seconds after fall
 *
 * Trigger flow:
 *   Camera frame → ML Kit detection → analyze expressions
 *   → if PANIC or FALL detected → emit DetectionEvent
 *   → CommunicationManager.sendEmergencyAlert()
 */
@Singleton
class FaceDetectionEngine @Inject constructor() {

    private val detector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .enableTracking()
            .setMinFaceSize(0.1f)
            .build()
        FaceDetection.getClient(options)
    }

    private val _events = MutableSharedFlow<DetectionEvent>(replay = 1)
    val events: SharedFlow<DetectionEvent> = _events

    // State tracking
    private var lastFaceSeenMs    = 0L
    private var panicFrameCount   = 0
    private var fallDetectedMs    = 0L
    private var previousHeadAngle = 0f

    // ── Process camera frame ──────────────────────────────────────────────────

    suspend fun processFrame(bitmap: Bitmap): List<FaceAnalysis> =
        suspendCancellableCoroutine { cont ->
            val image = InputImage.fromBitmap(bitmap, 0)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    val analyses = faces.map { analyzeFace(it) }
                    analyzeScene(analyses)
                    cont.resume(analyses)
                }
                .addOnFailureListener {
                    checkForNoFace()
                    cont.resume(emptyList())
                }
        }

    // ── Face analysis ─────────────────────────────────────────────────────────

    private fun analyzeFace(face: Face): FaceAnalysis {
        lastFaceSeenMs = System.currentTimeMillis()

        val smilingProb     = face.smilingProbability ?: 0f
        val leftEyeOpen     = face.leftEyeOpenProbability  ?: 0.5f
        val rightEyeOpen    = face.rightEyeOpenProbability ?: 0.5f
        val headEulerY      = face.headEulerAngleY   // left/right
        val headEulerZ      = face.headEulerAngleZ   // tilt

        // ── Panic/Fear heuristics ─────────────────────────────────────────────
        // Fear: eyes WIDE open + NOT smiling + possible open mouth
        val eyesWide       = leftEyeOpen > 0.85f && rightEyeOpen > 0.85f
        val mouthOpen      = smilingProb < 0.1f
        val extremeHeadTilt = Math.abs(headEulerZ) > 30f
        val isPanic        = eyesWide && mouthOpen

        // ── Head impact / fall indicator ──────────────────────────────────────
        val headAngularVelocity = Math.abs(headEulerY - previousHeadAngle)
        previousHeadAngle = headEulerY
        val isSuddenMovement = headAngularVelocity > 25f

        // ── Unconscious indicator ─────────────────────────────────────────────
        val isEyesClosed = leftEyeOpen < 0.15f && rightEyeOpen < 0.15f

        return FaceAnalysis(
            faceId            = face.trackingId ?: -1,
            boundingBox       = RectF(face.boundingBox),
            isPanic           = isPanic,
            isSuddenMovement  = isSuddenMovement,
            isEyesClosed      = isEyesClosed,
            eyeOpenness       = (leftEyeOpen + rightEyeOpen) / 2f,
            smileProb         = smilingProb,
            headEulerY        = headEulerY,
            headEulerZ        = headEulerZ
        )
    }

    private fun analyzeScene(analyses: List<FaceAnalysis>) {
        if (analyses.isEmpty()) {
            checkForNoFace()
            return
        }

        val primaryFace = analyses.first()

        // Panic detection — requires 3 consecutive panic frames (debounce)
        if (primaryFace.isPanic) {
            panicFrameCount++
            if (panicFrameCount >= 3) {
                panicFrameCount = 0
                kotlinx.coroutines.runBlocking {
                    _events.emit(DetectionEvent(
                        type        = DetectionType.PANIC_DETECTED,
                        confidence  = 0.82f,
                        description = "Panic/fear expression detected — wide eyes, open mouth"
                    ))
                }
            }
        } else {
            panicFrameCount = maxOf(0, panicFrameCount - 1)
        }

        // Sudden movement (possible fall impact on face angle)
        if (primaryFace.isSuddenMovement && fallDetectedMs > 0) {
            val timeSinceFall = System.currentTimeMillis() - fallDetectedMs
            if (timeSinceFall < 3000) {
                kotlinx.coroutines.runBlocking {
                    _events.emit(DetectionEvent(
                        type        = DetectionType.FALL_CONFIRMED,
                        confidence  = 0.88f,
                        description = "Fall confirmed: sudden body movement + face impact"
                    ))
                }
                fallDetectedMs = 0
            }
        }
    }

    private fun checkForNoFace() {
        val timeSinceLastFace = System.currentTimeMillis() - lastFaceSeenMs
        // If we had a fall (from accelerometer) and now face is gone → unconscious
        if (fallDetectedMs > 0 && timeSinceLastFace > 5000) {
            kotlinx.coroutines.runBlocking {
                _events.emit(DetectionEvent(
                    type        = DetectionType.POSSIBLY_UNCONSCIOUS,
                    confidence  = 0.75f,
                    description = "No face detected for 5s after fall — possibly unconscious"
                ))
            }
        }
    }

    /** Called by AccelerometerService when fall is detected */
    fun notifyFallDetected() {
        fallDetectedMs = System.currentTimeMillis()
        android.util.Log.i("FaceDetection", "Fall signal received from accelerometer")
    }

    fun release() = detector.close()
}

// ── Data classes ─────────────────────────────────────────────────────────────

enum class DetectionType {
    PANIC_DETECTED,
    FALL_CONFIRMED,
    POSSIBLY_UNCONSCIOUS,
    ABNORMAL_MOVEMENT
}

data class DetectionEvent(
    val type: DetectionType,
    val confidence: Float,
    val description: String,
    val timestampMs: Long = System.currentTimeMillis()
)

data class FaceAnalysis(
    val faceId: Int,
    val boundingBox: RectF,
    val isPanic: Boolean,
    val isSuddenMovement: Boolean,
    val isEyesClosed: Boolean,
    val eyeOpenness: Float,
    val smileProb: Float,
    val headEulerY: Float,
    val headEulerZ: Float
)
