package com.loam.trabajopractico1loam

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.loam.trabajopractico1loam.services.DolarService
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    
    // Views del widget del dólar
    private lateinit var cotizacionDolar: TextView
    
    // Botones del menú
    private lateinit var btnPrecios: CardView
    private lateinit var btnSeccion3: CardView
    private lateinit var btnSeccion4: CardView

    private val dolarService = DolarService()
    private val decimalFormat = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupClickListeners()
        loadDolarInfo()
    }
    
    private fun initViews() {
        try {
            // Widget del dólar
            cotizacionDolar = findViewById(R.id.cotizacionDolar)
            
            // Botones del menú
            btnPrecios = findViewById(R.id.btnPrecios)
            btnSeccion3 = findViewById(R.id.btnSeccion3)
            btnSeccion4 = findViewById(R.id.btnSeccion4)
            
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
            // Temporalmente desactivado para evitar crash
            // startActivity(Intent(this, PreciosActivity::class.java))
        }
        
        btnSeccion3.setOnClickListener {
            // TODO: Implementar navegación a Sección 3
        }
        
        btnSeccion4.setOnClickListener {
            // TODO: Implementar navegación a Sección 4
        }
    }
}
