package com.example.smartwatchapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.smartwatchapp.presentation.MainActivity

class PermissionManager(private val context: Context) {

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            context as MainActivity, // Assuming MainActivity is your activity
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS
            ),
            1
        )
    }

    fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        )
            requestPermissions()
    }

}