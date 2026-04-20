package com.emergency.aria.ai

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

/**
 * FALL DETECTION SERVICE
 * ───────────────────────
 * Uses accelerometer + gyroscope to detect sudden falls.
 *
 * ALGORITHM (two-phase):
 *   Phase 1 — FREE FALL:     Total acceleration drops below 3 m/s² for >80ms
 *   Phase 2 — IMPACT:        Total acceleration spikes above 25 m/s² within 500ms
 *
 * This combination (free fall + impact) has ~92% precision in studies.
 * False positives (jumping, dropping phone) are filtered by Phase 2 timing.
 *
 * After fall detection:
 *   → FaceDetectionEngine.notifyFallDetected()
 *   → 10-second countdown shown to user ("Are you OK?")
 *   → if no response → auto-trigger emergency
 */
@Singleton
class FallDetectionService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val faceDetectionEngine: FaceDetectionEngine
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope     = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val _fallEvents = MutableSharedFlow<FallEvent>(replay = 1)
    val fallEvents: SharedFlow<FallEvent> = _fallEvents

    // Detection state machine
    private var freeFallStartMs = 0L
    private var inFreeFall      = false
    private var lastImpactMs    = 0L

    // Gyro data (for orientation change detection)
    private var gyroX = 0f; private var gyroY = 0f; private var gyroZ = 0f

    // Thresholds
    companion object {
        const val FREE_FALL_THRESHOLD  = 3.0f   // m/s² (below = free fall)
        const val IMPACT_THRESHOLD     = 25.0f  // m/s² (above = impact)
        const val FREE_FALL_MIN_MS     = 80L    // minimum free fall duration
        const val IMPACT_WINDOW_MS     = 500L   // impact must occur within this window
        const val STATIONARY_THRESHOLD = 2.0f   // m/s² (person not moving after fall)
        const val TAG = "FallDetection"
    }

    fun start() {
        sensorManager.registerListener(this, accelerometer,
            SensorManager.SENSOR_DELAY_GAME)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        android.util.Log.i(TAG, "Fall detection started")
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        android.util.Log.i(TAG, "Fall detection stopped")
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> processAccelerometer(event)
            Sensor.TYPE_GYROSCOPE     -> processGyroscope(event)
        }
    }

    private fun processAccelerometer(event: SensorEvent) {
        val x = event.values[0]; val y = event.values[1]; val z = event.values[2]
        val totalAccel = sqrt(x*x + y*y + z*z)
        val now = System.currentTimeMillis()

        // Phase 1: Free fall detection
        if (totalAccel < FREE_FALL_THRESHOLD) {
            if (!inFreeFall) { freeFallStartMs = now; inFreeFall = true }
        } else {
            if (inFreeFall) {
                val freeFallDuration = now - freeFallStartMs
                inFreeFall = false

                if (freeFallDuration >= FREE_FALL_MIN_MS) {
                    // Valid free fall — now watch for impact
                    lastImpactMs = now
                    android.util.Log.d(TAG, "Free fall detected (${freeFallDuration}ms)")
                }
            }

            // Phase 2: Impact detection
            if (lastImpactMs > 0 && (now - lastImpactMs) < IMPACT_WINDOW_MS) {
                if (totalAccel > IMPACT_THRESHOLD) {
                    val gyroMagnitude = sqrt(gyroX*gyroX + gyroY*gyroY + gyroZ*gyroZ)
                    confirmFall(totalAccel, gyroMagnitude)
                    lastImpactMs = 0
                }
            }
        }
    }

    private fun processGyroscope(event: SensorEvent) {
        gyroX = event.values[0]; gyroY = event.values[1]; gyroZ = event.values[2]
    }

    private fun confirmFall(impactG: Float, rotationRate: Float) {
        val confidence = calculateConfidence(impactG, rotationRate)
        android.util.Log.w(TAG, "🚨 FALL DETECTED! impact=${impactG}m/s² conf=$confidence")

        faceDetectionEngine.notifyFallDetected()

        kotlinx.coroutines.runBlocking {
            _fallEvents.emit(FallEvent(
                impactAcceleration = impactG,
                rotationRate       = rotationRate,
                confidence         = confidence,
                timestamp          = System.currentTimeMillis()
            ))
        }
    }

    private fun calculateConfidence(impact: Float, rotation: Float): Float {
        // Higher impact + higher rotation → higher confidence it's a real fall
        val impactScore    = minOf((impact - IMPACT_THRESHOLD) / 20f, 1f)
        val rotationScore  = minOf(rotation / 5f, 1f)
        return 0.5f + (impactScore * 0.3f) + (rotationScore * 0.2f)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

data class FallEvent(
    val impactAcceleration: Float,
    val rotationRate:       Float,
    val confidence:         Float,
    val timestamp:          Long
)
