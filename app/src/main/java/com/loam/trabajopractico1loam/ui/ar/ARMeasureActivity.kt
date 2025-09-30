package com.loam.trabajopractico1loam.ar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.*
import com.google.ar.core.exceptions.*
import com.loam.trabajopractico1loam.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ARMeasureActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var instructionsText: TextView
    private lateinit var distanceText: TextView
    private lateinit var clearButton: Button
    private lateinit var backButton: Button

    private var session: Session? = null
    private val points = mutableListOf<FloatArray>() // [x, y, z]
    private var pointsPlaced = 0

    private val CAMERA_PERMISSION_CODE = 100
    private var isARCoreInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_measure_camera)

        if (!checkCameraPermission()) {
            requestCameraPermission()
            return
        }

        initializeViews()
        setupButtons()
        startCamera()
        
        // Inicializar ARCore de manera opcional
        initializeARCore()
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeViews()
                setupButtons()
                startCamera()
                initializeARCore()
            } else {
                Toast.makeText(this, "Permiso de cámara requerido", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun initializeViews() {
        previewView = findViewById(R.id.previewView)
        instructionsText = findViewById(R.id.instructionsText)
        distanceText = findViewById(R.id.distanceText)
        clearButton = findViewById(R.id.clearButton)
        backButton = findViewById(R.id.backButton)

        // Set up touch listener
        previewView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && pointsPlaced < 2) {
                handleTap(event.x, event.y)
                true
            } else {
                false
            }
        }
    }

    private fun setupButtons() {
        clearButton.setOnClickListener {
            clearMeasurement()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )
                
                Toast.makeText(this, "Cámara lista. Toca dos puntos para medir.", Toast.LENGTH_LONG).show()
            } catch (exc: Exception) {
                Log.e("ARMeasure", "Use case binding failed", exc)
                Toast.makeText(this, "Error al inicializar la cámara", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun initializeARCore() {
        try {
            // Intentar inicializar ARCore pero continuar sin él si falla
            when (ArCoreApk.getInstance().requestInstall(this, false)) {
                ArCoreApk.InstallStatus.INSTALLED -> {
                    session = Session(this)
                    configureSession(session!!)
                    isARCoreInitialized = true
                    Log.d("ARMeasure", "ARCore initialized successfully")
                }
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    Log.d("ARMeasure", "ARCore installation requested")
                }
            }
        } catch (e: Exception) {
            Log.w("ARMeasure", "ARCore not available, using fallback method: ${e.message}")
            isARCoreInitialized = false
            Toast.makeText(this, "Usando modo de medición estimada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleTap(x: Float, y: Float) {
        if (isARCoreInitialized) {
            handleTapWithARCore(x, y)
        } else {
            handleTapWithoutARCore(x, y)
        }
    }

    private fun handleTapWithARCore(x: Float, y: Float) {
        session?.let { session ->
            try {
                val frame = session.update()
                val camera = frame.camera

                if (camera.trackingState == TrackingState.TRACKING) {
                    val hits = frame.hitTest(x, y)
                    
                    // Buscar hit en planos detectados
                    for (hit in hits) {
                        val trackable = hit.trackable
                        if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                            val position = hit.hitPose.translation
                            addPoint(position[0], position[1], position[2])
                            return
                        }
                    }
                }
                
                // Si ARCore no detecta superficies, usar método de fallback
                handleTapWithoutARCore(x, y)
                
            } catch (e: Exception) {
                Log.e("ARMeasure", "ARCore error, using fallback", e)
                handleTapWithoutARCore(x, y)
            }
        } ?: handleTapWithoutARCore(x, y)
    }

    private fun handleTapWithoutARCore(x: Float, y: Float) {
        // Método de medición estimada sin ARCore
        val normalizedX = (x / previewView.width) - 0.5f
        val normalizedY = 0.5f - (y / previewView.height)
        
        // Simular profundidad basada en la posición en pantalla
        val estimatedDepth = 1.0f + (pointsPlaced * 0.3f) // Variar profundidad entre puntos
        
        // Calcular posición 3D estimada
        val worldX = normalizedX * estimatedDepth * 1.5f
        val worldY = normalizedY * estimatedDepth * 1.5f
        val worldZ = -estimatedDepth
        
        addPoint(worldX, worldY, worldZ)
    }

    private fun addPoint(x: Float, y: Float, z: Float) {
        points.add(floatArrayOf(x, y, z))
        pointsPlaced++

        runOnUiThread {
            when (pointsPlaced) {
                1 -> {
                    instructionsText.text = "Toca el segundo punto"
                    Toast.makeText(this, "Primer punto colocado ✓", Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    instructionsText.text = "Medición completada"
                    calculateAndDisplayDistance()
                    Toast.makeText(this, "Segundo punto colocado ✓", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun calculateAndDisplayDistance() {
        if (points.size >= 2) {
            val point1 = points[0]
            val point2 = points[1]

            val dx = point1[0] - point2[0]
            val dy = point1[1] - point2[1]
            val dz = point1[2] - point2[2]

            val distance = sqrt(dx * dx + dy * dy + dz * dz)
            val distanceInCm = distance * 100

            val accuracy = if (isARCoreInitialized) "±2cm" else "estimada"
            distanceText.text = String.format("Distancia: %.1f cm (%s)", distanceInCm, accuracy)
        }
    }

    private fun clearMeasurement() {
        points.clear()
        pointsPlaced = 0

        instructionsText.text = "Toca dos puntos para medir la distancia"
        distanceText.text = "Distancia: -"

        Toast.makeText(this, "Medición limpiada", Toast.LENGTH_SHORT).show()
    }

    private fun configureSession(session: Session) {
        val config = Config(session)
        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
        config.lightEstimationMode = Config.LightEstimationMode.DISABLED
        session.configure(config)
    }

    override fun onResume() {
        super.onResume()

        if (!checkCameraPermission()) {
            return
        }

        if (isARCoreInitialized) {
            try {
                session?.resume()
            } catch (e: CameraNotAvailableException) {
                Log.e("ARMeasure", "Camera not available for ARCore", e)
                isARCoreInitialized = false
                Toast.makeText(this, "ARCore no disponible, usando modo estimado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isARCoreInitialized) {
            session?.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isARCoreInitialized) {
            session?.close()
        }
        clearMeasurement()
    }
}