package com.loam.trabajopractico1loam

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.loam.trabajopractico1loam.ui.mapa.MapFragment

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        supportActionBar?.hide()

        if (savedInstanceState == null) {
            val fragment = MapFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.map_container, fragment)
                .commit()
        }
    }

    @SuppressLint("GestureBackNavigation")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}