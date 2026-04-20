package com.emergency.aria.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt

class FallDetector(context: Context, private val onFallDetected: () -> Unit) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("FallDetector", "Accelerometer sensor started")
        }
    }

    fun stop() = sensorManager.unregisterListener(this)

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            // Resultant acceleration nikal rahe hain
            val acceleration = sqrt(x*x + y*y + z*z)

            // 🚨 AI Logic: Agar acceleration 2.0 m/s² se kam hai (Free fall state)
            if (acceleration < 2.0) {
                Log.e("FallDetector", "🚨 Fall Trigger Detected!")
                onFallDetected()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}