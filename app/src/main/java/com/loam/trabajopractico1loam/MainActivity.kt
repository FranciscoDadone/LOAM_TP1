package com.loam.trabajopractico1loam

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.loam.trabajopractico1loam.ar.HelloArActivity
import com.loam.trabajopractico1loam.ar.ARMeasureActivity
import com.loam.trabajopractico1loam.services.DolarService
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    
    // Views del widget del dólar
    private lateinit var cotizacionDolar: TextView
    private lateinit var tvLinterna: TextView

    // Botones del menú
    private lateinit var btnPrecios: CardView
    private lateinit var btnSeccion3: CardView
    private lateinit var btnSeccion4: CardView
    private lateinit var btnMedidor: CardView
    private lateinit var chatBtn: CardView
    private lateinit var grabadorAudioBtn: CardView
    private lateinit var btnLlamar: CardView

    private lateinit var camaraFrontalBtn: CardView

    private val dolarService = DolarService()
    private val decimalFormat = DecimalFormat("#.##")

    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var isFlashOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupClickListeners()
        loadDolarInfo()

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList.first {
            cameraManager.getCameraCharacteristics(it)
                .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }

        btnSeccion3.setOnClickListener {
            if (isFlashOn) {
                apagarFlash()
                tvLinterna.text = "Linterna apagada"
            } else {
                encenderFlash()
                tvLinterna.text = "Linterna encendida"
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFlashOn) {
            apagarFlash()
        }
    }
    private fun encenderFlash() {
        cameraId?.let {
            cameraManager.setTorchMode(it, true)
            isFlashOn = true
        }
    }

    private fun apagarFlash() {
        cameraId?.let {
            cameraManager.setTorchMode(it, false)
            isFlashOn = false
        }
    }


    private fun initViews() {
        try {
            // Widget del dólar
            cotizacionDolar = findViewById(R.id.cotizacionDolar)
            tvLinterna = findViewById(R.id.modoLinternaTexto)
            
            // Botones del menú
            btnPrecios = findViewById(R.id.btnPrecios)
            btnSeccion3 = findViewById(R.id.btnSeccion3)
            btnSeccion4 = findViewById(R.id.btnSeccion4)
            camaraFrontalBtn = findViewById(R.id.camaraFrontalBtn)
            grabadorAudioBtn = findViewById(R.id.grabadorAudioBtn)
            chatBtn = findViewById(R.id.chatBtn)
            btnLlamar = findViewById(R.id.btnLlamar)
            btnMedidor = findViewById(R.id.btnMedidor)

            // Mostrar texto inicial
            cotizacionDolar.text = "Iniciando..."
        } catch (e: Exception) {
            e.printStackTrace()
            finish() // Cerrar la activity si no puede inicializar las vistas
        }
    }
    
    private fun loadDolarInfo() {
        lifecycleScope.launch {
            try {
                cotizacionDolar.text = "Cargando dólar..."
                val dolar = dolarService.getDolarOficial()
                cotizacionDolar.text = "Dólar: $${decimalFormat.format(dolar.compra)} - $${decimalFormat.format(dolar.venta)}"
            } catch (e: Exception) {
                cotizacionDolar.text = "Error al cargar dólar: ${e.message}"
                e.printStackTrace() // Para debug
            }
        }
    }


    
    private fun setupClickListeners() {
        btnPrecios.setOnClickListener {
             startActivity(Intent(this, PreciosActivity::class.java))
        }

        camaraFrontalBtn.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        grabadorAudioBtn.setOnClickListener {
            startActivity(Intent(this, GrabadorAudioActivity::class.java))
        }

        chatBtn.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        btnLlamar.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL);
            intent.data = Uri.parse("tel:2954465433")
            startActivity(intent)
        }

        btnMedidor.setOnClickListener {
            startActivity(Intent(this, ARMeasureActivity::class.java))
        }
    }
}
