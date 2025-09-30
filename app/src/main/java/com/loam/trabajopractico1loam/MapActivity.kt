package com.loam.trabajopractico1loam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.loam.trabajopractico1loam.ui.mapa.MapFragment

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Ocultar la action bar para una experiencia de pantalla completa
        supportActionBar?.hide()

        // Solo agregar el fragment si no existe ya (para evitar duplicados en rotaciones de pantalla)
        if (savedInstanceState == null) {
            val fragment = MapFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.map_container, fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        // Permitir que el botón back cierre la actividad
        super.onBackPressed()
        finish()
    }
}