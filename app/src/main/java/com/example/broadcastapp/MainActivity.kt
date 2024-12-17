package com.example.broadcastapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
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

        // Register the BroadcastReceiver dynamically
        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        registerReceiver(airplaneModeReceiver, filter)

        Log.d("MainActivity", "BroadcastReceiver registered")
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister the receiver to avoid memory leaks
        unregisterReceiver(airplaneModeReceiver)
        Log.d("MainActivity", "BroadcastReceiver unregistered")
    }
}
