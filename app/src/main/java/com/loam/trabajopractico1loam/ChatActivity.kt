package com.loam.trabajopractico1loam

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class ChatActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "ChatActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContent {
                MaterialTheme {
                    Surface {
                        Chat(
                            onBackPressed = {
                                finish() // Esto cierra la ChatActivity y regresa al MainActivity
                            }
                        )
                    }
                }
            }
            Log.d(TAG, "ChatActivity iniciada correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar ChatActivity", e)
            Toast.makeText(this, "Error al abrir el chat", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}