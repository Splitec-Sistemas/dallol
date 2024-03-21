/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.smartwatchapp.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.smartwatchapp.LightSensorService
import com.example.smartwatchapp.PermissionManager
import com.example.smartwatchapp.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionVerify()
        startLightSensor()
    }

    //Inicia o serviço em segundo plano do sensor de luz
    private fun startLightSensor() {
        val serviceIntent = Intent(this, LightSensorService::class.java)
        startService(serviceIntent)
    }

    // Realiaza a verificação das permissões para o uso de notificações e do gps
    private fun permissionVerify() {
        val permissionManager = PermissionManager(this)
        permissionManager.checkPermissions()
    }
}


