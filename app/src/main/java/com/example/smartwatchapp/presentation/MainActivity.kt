/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.smartwatchapp.presentation

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.Nullable
import com.example.smartwatchapp.LightSensorService
import com.example.smartwatchapp.PermissionManager
import com.example.smartwatchapp.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable


class MainActivity : ComponentActivity(),
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    MessageApi.MessageListener {

    private lateinit var sunHealth: TextView

    companion object {
        var client: GoogleApiClient? = null
        var node: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionVerify()
        startLightSensor()

        sunHealth = findViewById(R.id.healNumber)
        client = GoogleApiClient.Builder(this).addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()

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

    override fun onConnected(@Nullable bundle: Bundle?) {
        Wearable.MessageApi.addListener(client!!, this)
        InitNodesTask().execute(client)
    }

    private class InitNodesTask : AsyncTask<GoogleApiClient?, Void?, String?>() {
        override fun doInBackground(vararg params: GoogleApiClient?): String? {
            val connectedNodes: List<Node> =
                Wearable.NodeApi.getConnectedNodes(client!!).await().nodes
            for (connectedNode in connectedNodes) {
                if (connectedNode.isNearby) {
                    return connectedNode.id
                }
            }
            return null
        }

        override fun onPostExecute(resultNode: String?) {
            super.onPostExecute(resultNode)
            node = resultNode
        }

    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/update_sunHealth") {
            sunHealth.text = messageEvent.data.toString()
        }
    }

    private fun disconnectGoogleApiClient() {
        if (client != null && client!!.isConnected) {
            Wearable.MessageApi.removeListener(client!!, this)
            client!!.disconnect()
        }
        node = null
    }

    override fun onStart() {
        super.onStart()
        client!!.connect()
    }

    override fun onStop() {
        disconnectGoogleApiClient()
        super.onStop()
    }

    override fun onConnectionSuspended(i: Int) {
        disconnectGoogleApiClient()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        disconnectGoogleApiClient()
    }


}


