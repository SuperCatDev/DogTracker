package com.sc.dtracker.features.location.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.sc.dtracker.common.coroutines.throttleFist
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class SensorDataRepository(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    fun getAzimuthFlow(): Flow<Float> = callbackFlow {
        val sensorEventListener = object : SensorEventListener {
            private var gravity: FloatArray? = null
            private var geomagnetic: FloatArray? = null

            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> gravity = event.values
                    Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values
                }

                if (gravity != null && geomagnetic != null) {
                    val R = FloatArray(9)
                    val I = FloatArray(9)
                    if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(R, orientation)
                        var azimuthInDegrees = Math.toDegrees(orientation[0].toDouble()).toFloat()
                        if (azimuthInDegrees < 0) {
                            azimuthInDegrees += 360
                        }
                        trySend(azimuthInDegrees)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle changes in sensor accuracy here if needed
            }
        }

        sensorManager.registerListener(
            sensorEventListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
        sensorManager.registerListener(
            sensorEventListener,
            magnetometer,
            SensorManager.SENSOR_DELAY_UI
        )

        awaitClose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }
        .throttleFist(1000)
        .distinctUntilChanged()
}