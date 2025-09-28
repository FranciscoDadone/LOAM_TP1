package com.loam.trabajopractico1loam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.loam.trabajopractico1loam.viewmodel.DolarViewModel
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    
    private val dolarViewModel: DolarViewModel by viewModels()
    
    // Views del widget del dólar
    private lateinit var layoutLoading: LinearLayout
    private lateinit var layoutError: LinearLayout
    private lateinit var layoutSuccess: LinearLayout
    private lateinit var tvCompra: TextView
    private lateinit var tvVenta: TextView
    
    // Botones del menú
    private lateinit var btnPrecios: CardView
    private lateinit var btnSeccion3: CardView
    private lateinit var btnSeccion4: CardView
    
    private val decimalFormat = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun initViews() {
        // Widget del dólar
        layoutLoading = findViewById(R.id.layoutLoading)
        layoutError = findViewById(R.id.layoutError)
        layoutSuccess = findViewById(R.id.layoutSuccess)
        tvCompra = findViewById(R.id.tvCompra)
        tvVenta = findViewById(R.id.tvVenta)
        
        // Botones del menú
        btnPrecios = findViewById(R.id.btnPrecios)
        btnSeccion3 = findViewById(R.id.btnSeccion3)
        btnSeccion4 = findViewById(R.id.btnSeccion4)
    }
    
    private fun setupClickListeners() {
        btnPrecios.setOnClickListener {
            startActivity(Intent(this, PreciosActivity::class.java))
        }
        
        btnSeccion3.setOnClickListener {
            // TODO: Implementar navegación a Sección 3
        }
        
        btnSeccion4.setOnClickListener {
            // TODO: Implementar navegación a Sección 4
        }
        
        layoutError.setOnClickListener {
            dolarViewModel.retry()
        }
    }
    
    private fun observeViewModel() {
        dolarViewModel.uiState.observe(this) { state ->
            updateDolarWidget(state)
        }
    }
    
    private fun updateDolarWidget(state: com.loam.trabajopractico1loam.viewmodel.DolarUiState) {
        when {
            state.isLoading -> {
                layoutLoading.visibility = View.VISIBLE
                layoutError.visibility = View.GONE
                layoutSuccess.visibility = View.GONE
            }
            
            state.errorMessage != null -> {
                layoutLoading.visibility = View.GONE
                layoutError.visibility = View.VISIBLE
                layoutSuccess.visibility = View.GONE
            }
            
            state.dolar != null -> {
                layoutLoading.visibility = View.GONE
                layoutError.visibility = View.GONE
                layoutSuccess.visibility = View.VISIBLE
                
                tvCompra.text = "$${decimalFormat.format(state.dolar.compra)}"
                tvVenta.text = "$${decimalFormat.format(state.dolar.venta)}"
            }
        }
    }
}
