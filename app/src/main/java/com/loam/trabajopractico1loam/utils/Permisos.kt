package com.loam.trabajopractico1loam.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class Permisos {
    companion object {
        fun audio(context: Context): Boolean {
            val audioPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            // Para Android 10 y anteriores, también necesitamos permisos de almacenamiento
            val storagePermission = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
            } else {
                // En Android 11+ no necesitamos estos permisos si guardamos en almacenamiento interno
                true
            }

            return audioPermission && storagePermission
        }

        fun ar(context: Context): Boolean {
            val cameraPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            return cameraPermission
        }
    }
}