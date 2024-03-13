package com.example.smartwatchapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartwatchapp.presentation.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class LightSensorService : Service(), SensorEventListener {

    private var check: Boolean = false
    private var checkLimit: Boolean = false
    private lateinit var sensorManager: SensorManager
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var lightSensor: Sensor? = null
    private var sunCheck: Int = 0
    private var darkCheck: Int = 0

    //Variaveis para mandar para o celulebas
    private var lightLevel: Float = 0.0f
    private lateinit var local: String

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!check) {
            if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                // aqui onde recebe o valor da luz do ambiente
                lightLevel = event.values[0]
                if (!checkLimit) {
                    if (lightLevel > 100) {
                        if (!check) {
                            sunCheck++
                            check = true
                            timerSunCheck.start()
                            val text = "sol $sunCheck"
                            showNotifyLog(text)
                        }
                    } else {
                        if (!check) {
                            darkCheck++
                            check = true
                            timerSunCheck.start()
                            val text = "dark $darkCheck"
                            showNotifyLog(text)
                        }
                    }
                } else if (checkLimit) {
                    if (lightLevel > 100) {
                        if (!check) {
                            sunCheck++
                            check = true
                            timerSunCheck.start()
                            val text = "sol $sunCheck"
                            showNotifyLog(text)
                        }
                    } else {
                        if (!check) {
                            darkCheck++
                            check = true
                            timerSunCheck.start()
                            val text = "dark $darkCheck"
                            showNotifyLog(text)
                        }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não é necessário para este exemplo
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    private fun showNotify(text: String) {
        val channelId = "sensor_light_channel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sensor de luz"
            val descriptionText = "Notificação do sensor de luz"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("luz elevada")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 1000, 1000, 1000))

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotifyLog(text: String) {
        val channelId = "log_notify"
        val notificationId = 3

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "log test"
            val descriptionText = "Notificação de texte"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("log test")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 1000, 1000, 1000))

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    // aqui onde o local recebe o valor pra mandar pro celulebas
    @SuppressLint("MissingPermission")
    fun getLocal() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        fusedLocation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    var latitude = location.latitude
                    var longitude = location.longitude
                    var accuracy = location.accuracy
                    local =
                        "Latitude: $latitude\nLongitude: $longitude\nPrecisão: $accuracy\n Luz: $lightLevel"
                    enviarDadosParaCelular(local)
                } else {
                    local = "deu ruim"
                    showNotify(local)
                }
            }
    }

    private var timerSunCheck = object : CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            check = false
            if (sunCheck > 4) {
                darkCheck = 0
                sunCheck = 0
                if (!checkLimit) {
                    checkLimit = true
                    getLocal()
                    timerSunLimit.start()
                }
            } else if (darkCheck > 4) {
                darkCheck = 0
                sunCheck = 0
                checkLimit = false
                showNotify("você não está mais no sol")
                timerSunLimit.cancel()
            }
        }
    }

    private var timerSunLimit = object : CountDownTimer(20000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            darkCheck = 0
            sunCheck = 0
            checkLimit = false
            showNotify("atingiu seu limite solar")
        }
    }

    // Função para enviar dados
    fun enviarDadosParaCelular(dado: String) {
        val request = PutDataMapRequest.create("/dados") // Path para identificar o tipo de dados
        val dataMap = request.dataMap
        dataMap.putString("chave_dado", dado)

        Wearable.getDataClient(this).putDataItem(request.asPutDataRequest())
    }
}