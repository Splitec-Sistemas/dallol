/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.smartwatchapp.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.smartwatchapp.LightSensorService
import com.example.smartwatchapp.PermissionManager
import com.example.smartwatchapp.R

class MainActivity : ComponentActivity() {
    private lateinit var sunHealth: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //falta o código para sincronizar os dados com o celular e ai atualizar o sunHealth
        sunHealth = findViewById(R.id.healNumber)

        permissionVerify()
        startLightSensor()
    }

    //Inicia o serviço em segundo plano do sensor de luz
    private fun startLightSensor() {
        val serviceIntent = Intent(this, LightSensorService::class.java)
        this.startService(serviceIntent)
    }

    // Realiaza a verificação das permissões para o uso de notificações e do gps
    private fun permissionVerify() {
        val permissionManager = PermissionManager(this)
        permissionManager.checkPermissions()
    }
}


