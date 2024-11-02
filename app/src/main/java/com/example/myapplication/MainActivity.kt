package com.example.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager


    private lateinit var textView: TextView

    // Sensor references
    private var accelerometerSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null
    private var magneticSensor: Sensor? = null
    private var ambientTemperatureSensor: Sensor? = null
    private var proximitySensor: Sensor? = null
    private var lightSensor: Sensor? = null
    private var pressureSensor: Sensor? = null
    private var humiditySensor: Sensor? = null

    private var accelerometerUncalibrated: Sensor? = null
    private var magneticFieldUncalibrated: Sensor? = null
    private var gyroscopeUncalibrated: Sensor? = null

    private var gameRotationVectorSensor: Sensor? = null
    private var gravitySensor: Sensor? = null
    private var linearAccelerationSensor: Sensor? = null
    private var geoMagRotationVectorSensor: Sensor? = null
    private var rotationVectorSensor: Sensor? = null

    // Last known sensor values
    private var lastSensorData: MutableMap<Int, String> = mutableMapOf()

    // For orientation calculation
    private val rotationMatrix = FloatArray(9)
    private val orientationValues = FloatArray(3)
    private var lastAccelerometer: FloatArray? = null
    private var lastMagnetometer: FloatArray? = null

    private var updateJob: Job? = null

    private lateinit var deviceSensors: List<Sensor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the sensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        textView = findViewById(R.id.tv)

        // Get all sensors and display their information
        deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (sensor in deviceSensors) {
            val sensorInfo = """
                Name: ${sensor.name}
                Vendor: ${sensor.vendor}
                Version: ${sensor.version}
                Maximum Range: ${sensor.maximumRange}
                Resolution: ${sensor.resolution}
                Power: ${sensor.power} mA
                Min Delay: ${sensor.minDelay} μs
                Data: N/A
                -------------------------------

            """.trimIndent()
            textView.append(sensorInfo)
        }
        registerSensorListeners()
    }

    private fun registerSensorListeners() {
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        ambientTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

        accelerometerUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)
        magneticFieldUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)
        gyroscopeUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)
        gameRotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        geoMagRotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)


        // Register each sensor
        accelerometerSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        magneticSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        ambientTemperatureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        pressureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        humiditySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometerUncalibrated?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        magneticFieldUncalibrated?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscopeUncalibrated?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gameRotationVectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gravitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        linearAccelerationSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        geoMagRotationVectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        rotationVectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val sensorData = when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    lastAccelerometer = event.values.clone()
                    "Accelerometer(m/s²) - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}"
                }
                Sensor.TYPE_GYROSCOPE -> {
                    "Gyroscope(rad/s) - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}"
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    lastMagnetometer = event.values.clone()
                    "Magnetic Field(μT) - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}"

                }
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    "Temperature ${event.values[0]} °C"
                }
                Sensor.TYPE_PROXIMITY -> {
                    "Proximity ${event.values[0]}"
                }
                Sensor.TYPE_LIGHT -> {
                    "Light ${event.values[0]} lx"
                }
                Sensor.TYPE_PRESSURE -> {
                    "Pressure ${event.values[0]} hPa"
                }
                Sensor.TYPE_RELATIVE_HUMIDITY -> {
                    "Humidity ${event.values[0]} %"
                }
                Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> {
                    "Uncalibrated Magnetic Field(μT) - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}"
                }
                Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> {
                    "Uncalibrated Accelerometer(m/s²) - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}"
                }
                Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> {
                    "Uncalibrated Gyroscope(rad/s) - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}"
                }
                Sensor.TYPE_GAME_ROTATION_VECTOR -> {
                    "Game Rotation Vector - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}, Scalar: ${event.values[3]}"
                }
                Sensor.TYPE_GRAVITY -> {
                    "Gravity - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}"
                }
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    "Linear Acceleration - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}"
                }
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                    "GeoMag Rotation Vector - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}, Scalar: ${event.values[3]}"
                }
                Sensor.TYPE_ROTATION_VECTOR -> {
                    "Rotation Vector - X: ${event.values[0]}, Y: ${event.values[1]}, Z: ${event.values[2]}, Scalar: ${event.values[3]}"
                }
                else -> {
                    "Data not available"
                }
            }

            // Store the last known sensor data
            lastSensorData[event.sensor.type] = "Data: $sensorData"

            // Calculate orientation if both accelerometer and magnetometer data are available
            if (lastAccelerometer != null && lastMagnetometer != null) {
                SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
                SensorManager.getOrientation(rotationMatrix, orientationValues)
                val azimuth = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
                val pitch = Math.toDegrees(orientationValues[1].toDouble()).toFloat()
                val roll = Math.toDegrees(orientationValues[2].toDouble()).toFloat()
                lastSensorData[Sensor.TYPE_ORIENTATION] = "Data: Orientation - Azimuth: $azimuth, Pitch: $pitch, Roll: $roll"
            }

            // Update the TextView with the latest sensor data
            updateJob?.cancel() // Cancel any ongoing job
            updateJob = CoroutineScope(Dispatchers.Main).launch {
                textView.text = deviceSensors.joinToString("\n") { sensor ->
                    val data = lastSensorData[sensor.type] ?: "Data: N/A"
                    """
                        Name: ${sensor.name}
                        Vendor: ${sensor.vendor}
                        Version: ${sensor.version}
                        Maximum Range: ${sensor.maximumRange}
                        Resolution: ${sensor.resolution}
                        Power: ${sensor.power} mA
                        Min Delay: ${sensor.minDelay} μs
                        $data
                        -------------------------------
                    """.trimIndent()
                }
                delay(100)  // Throttle UI updates to every 100 milliseconds
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if necessary
    }

    override fun onResume() {
        super.onResume()
        registerSensorListeners()
    }

    override fun onPause() {
        super.onPause()
        // Unregister the sensor listeners when the activity is paused
        sensorManager.unregisterListener(this)
        updateJob?.cancel() // Cancel any ongoing job
    }
}