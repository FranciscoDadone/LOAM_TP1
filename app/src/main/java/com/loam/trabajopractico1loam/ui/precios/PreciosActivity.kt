package com.loam.trabajopractico1loam.ui.precios

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.loam.trabajopractico1loam.R
import com.loam.trabajopractico1loam.viewmodel.PreciosUiState
import com.loam.trabajopractico1loam.viewmodel.PreciosViewModel

class PreciosActivity : AppCompatActivity() {
    
    private val preciosViewModel: PreciosViewModel by viewModels()
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabBack: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutError: LinearLayout
    private lateinit var tvError: TextView
    private lateinit var preciosAdapter: PreciosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_precios)
        
        initViews()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fabBack = findViewById(R.id.fabBack)
        progressBar = findViewById(R.id.progressBar)
        layoutError = findViewById(R.id.layoutError)
        tvError = findViewById(R.id.tvError)
    }
    
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        preciosAdapter = PreciosAdapter(emptyList())
        recyclerView.adapter = preciosAdapter
    }
    
    private fun setupClickListeners() {
        fabBack.setOnClickListener {
            finish()
        }
        
        layoutError.setOnClickListener {
            preciosViewModel.retry()
        }
    }
    
    private fun observeViewModel() {
        preciosViewModel.uiState.observe(this) { state ->
            updateUI(state)
        }
    }
    
    private fun updateUI(state: PreciosUiState) {
        when {
            state.isLoading -> {
                progressBar.visibility = View.VISIBLE
                layoutError.visibility = View.GONE
                recyclerView.visibility = View.GONE
            }
            
            state.errorMessage != null -> {
                progressBar.visibility = View.GONE
                layoutError.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                tvError.text = state.errorMessage
            }
            
            else -> {
                progressBar.visibility = View.GONE
                layoutError.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                preciosAdapter.updatePrecios(state.precios)
            }
        }
    }
}