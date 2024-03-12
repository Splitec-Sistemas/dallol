/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.smartwatchapp.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.smartwatchapp.LightSensorService
import com.example.smartwatchapp.PermissionManager
import com.example.smartwatchapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sunHealth: TextView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sunHealth = findViewById(R.id.healNumber)

        permissionVerify()
        startLightSensor()
        button = findViewById(R.id.changeScreen)
        button.setOnClickListener {
            getLocation()
        }

    }

    //Inicia o serviço em segundo plano do sensor de luz
    private fun startLightSensor() {
        val serviceIntent = Intent(this, LightSensorService::class.java)
        this.startService(serviceIntent)
    }

    // Realiaza a verificação das permissões para o uso de notificaçãos e do gps
    private fun permissionVerify() {
        val permissionManager = PermissionManager(this)
        permissionManager.checkPermissions()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    var latitude = location.latitude
                    var longitude = location.longitude
                    var accuracy = location.accuracy
                    sunHealth.text =
                        "Latitude: $latitude\nLongitude: $longitude\nPrecisão: $accuracy"
                } else {
                    sunHealth.text = "Não foi possílvel coletar localização"
                    sunHealth.visibility = android.view.View.VISIBLE
                }
            }
    }
}


