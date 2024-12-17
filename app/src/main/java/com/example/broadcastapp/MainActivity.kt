package com.example.broadcastapp

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // BroadcastReceiver to listen for AIRPLANE_MODE changes
    private val airplaneModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                // Check the state of Airplane Mode
                val isAirplaneModeOn = intent.getBooleanExtra("state", false)
                if (isAirplaneModeOn) {
                    Log.d("AirplaneModeReceiver", "Airplane mode is ON")
                } else {
                    Log.d("AirplaneModeReceiver", "Airplane mode is OFF")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register the BroadcastReceiver dynamically for Airplane Mode
        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        registerReceiver(airplaneModeReceiver, filter)
        Log.d("MainActivity", "Airplane Mode BroadcastReceiver registered")

        // Button to send custom explicit broadcast
        val button = findViewById<Button>(R.id.send_broadcast_button)
        button.setOnClickListener {
            sendExplicitBroadcast()
        }
    }

    private fun sendExplicitBroadcast() {
        val intent = Intent("com.example.broadcastapp.EXPLICIT_BROADCAST")
        intent.setComponent(ComponentName(this, MyExplicitReceiver::class.java))
        intent.putExtra("message", "Hello from explicit broadcast!")
        sendBroadcast(intent)
        Log.d("MainActivity", "Explicit broadcast sent")
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the Airplane Mode receiver to avoid memory leaks
        unregisterReceiver(airplaneModeReceiver)
        Log.d("MainActivity", "Airplane Mode BroadcastReceiver unregistered")
    }
}
