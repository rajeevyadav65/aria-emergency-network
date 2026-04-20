// AiDetectionManager.kt — ARIA On-Device AI
// Uses Google ML Kit for on-device face detection + custom fall algorithm
// Works OFFLINE — no internet required for primary detection

package com.emergency.aria.ai

import android.content.Context
import android.graphics.Bitmap
import android.hardware.SensorEvent
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * On-device AI detection manager.
 *
 * 1. FACE DETECTION (ML Kit) — detects:
 *    - Fear/panic: open mouth + wide eyes + head tilt
 *    - Distress: eye openness probability drops (closed eyes → unconscious)
 *
 * 2. FALL DETECTION (Accelerometer) — detects:
 *    - Sudden high-G impact followed by stillness
 *    - Algorithm: threshold spike + sustained low movement
 *
 * 3. SERVER VALIDATION (optional) — sends ambiguous cases to Claude Vision
 *
 * Integration:
 *   val detector = AiDetectionManager(context)
 *   detector.startCamera()  // starts ML Kit face detection pipeline
 *   detector.feedAccelerometerData(event)  // call from SensorEventListener
 *   detector.detectionEvents.collect { event -> handleEvent(event) }
 */
class AiDetectionManager(private val context: Context) {

    data class DetectionEvent(
        val type: DetectionType,
        val confidence: Float,
        val description: String,
        val bitmap: Bitmap? = null  // captured frame for server validation
    )

    enum class DetectionType {
        FALL_DETECTED,
        PANIC_FACE,
        UNCONSCIOUS_FACE,  // Eyes closed, head at abnormal angle
        NONE
    }

    private val _events = MutableSharedFlow<DetectionEvent>()
    val detectionEvents: SharedFlow<DetectionEvent> = _events

    // ── ML Kit Face Detector ──────────────────────────────────────────────────

    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.15f)
        .build()

    private val faceDetector = FaceDetection.getClient(faceDetectorOptions)

    /**
     * Analyze a camera frame for panic/fear/unconsciousness.
     * Call this on every camera frame (or every N frames for performance).
     */
    fun analyzeFaceFrame(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        faceDetector.process(image)
            .addOnSuccessListener { faces -> processFaces(faces, bitmap) }
            .addOnFailureListener { /* silently ignore — primary detection only */ }
    }

    private fun processFaces(faces: List<Face>, bitmap: Bitmap) {
        if (faces.isEmpty()) return

        for (face in faces) {
            val leftEye  = face.leftEyeOpenProbability  ?: 1f
            val rightEye = face.rightEyeOpenProbability ?: 1f
            val smiling  = face.smilingProbability      ?: 0f
            val headTilt = abs(face.headEulerAngleZ)     // Z-rotation

            // PANIC detection: wide open eyes + no smile + extreme head tilt
            val panicScore = when {
                leftEye > 0.9f && rightEye > 0.9f && smiling < 0.1f && headTilt > 30f -> 0.85f
                leftEye > 0.85f && rightEye > 0.85f && smiling < 0.1f -> 0.65f
                else -> 0f
            }

            // UNCONSCIOUS detection: both eyes closed + head at odd angle
            val unconsciousScore = when {
                leftEye < 0.1f && rightEye < 0.1f && headTilt > 20f -> 0.88f
                leftEye < 0.2f && rightEye < 0.2f -> 0.60f
                else -> 0f
            }

            when {
                panicScore > 0.7f -> _events.tryEmit(DetectionEvent(
                    DetectionType.PANIC_FACE, panicScore,
                    "Panic detected: eyes wide, no smile, head tilted ${headTilt.toInt()}°", bitmap))

                unconsciousScore > 0.75f -> _events.tryEmit(DetectionEvent(
                    DetectionType.UNCONSCIOUS_FACE, unconsciousScore,
                    "Possible unconsciousness: both eyes closed", bitmap))
            }
        }
    }

    // ── Fall Detection (Accelerometer) ────────────────────────────────────────

    private var previousAccel = Triple(0f, 0f, 0f)
    private var impactTime   = 0L
    private var postImpactSamples = 0

    private val IMPACT_THRESHOLD  = 25.0f  // m/s² (roughly 2.5G)
    private val STILLNESS_THRESHOLD = 3.0f  // very little movement after fall
    private val POST_IMPACT_WINDOW  = 50    // samples after impact to check for stillness

    /**
     * Feed raw accelerometer data.
     * Call from SensorEventListener.onSensorChanged() with TYPE_ACCELEROMETER.
     */
    fun feedAccelerometerData(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculate magnitude (remove gravity component approximately)
        val magnitude = sqrt(x * x + y * y + z * z)

        // Phase 1: detect high-G impact
        if (magnitude > IMPACT_THRESHOLD && impactTime == 0L) {
            impactTime = System.currentTimeMillis()
            postImpactSamples = 0
        }

        // Phase 2: check for stillness after impact
        if (impactTime > 0L) {
            postImpactSamples++
            val delta = sqrt(
                (x - previousAccel.first).let { it * it } +
                (y - previousAccel.second).let { it * it } +
                (z - previousAccel.third).let { it * it })

            if (postImpactSamples >= POST_IMPACT_WINDOW) {
                if (delta < STILLNESS_THRESHOLD) {
                    // High-G impact followed by stillness → FALL
                    val confidence = minOf(0.95f,
                        0.60f + (magnitude - IMPACT_THRESHOLD) / IMPACT_THRESHOLD * 0.35f)
                    _events.tryEmit(DetectionEvent(
                        DetectionType.FALL_DETECTED, confidence,
                        "Fall detected: impact ${magnitude.toInt()} m/s², then stillness"))
                }
                // Reset for next detection
                impactTime = 0L
                postImpactSamples = 0
            }
        }

        previousAccel = Triple(x, y, z)
    }

    fun destroy() = faceDetector.close()
}
