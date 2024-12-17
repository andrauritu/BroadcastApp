package com.example.broadcastapp

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val airplaneModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                val isAirplaneModeOn = intent.getBooleanExtra("state", false)
                if (isAirplaneModeOn) {
                    Log.d("AirplaneModeReceiver", "Airplane mode is ON")
                } else {
                    Log.d("AirplaneModeReceiver", "Airplane mode is OFF")
                }
            }
        }
    }

    private val hashReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("HashReceiver", "hashReceiver triggered")
            if (intent.action == "com.example.broadcastapp.HASH_BROADCAST") {
                val hashedMessage = intent.getStringExtra("hashedMessage")
                Log.d("HashReceiver", "Received hashed message: $hashedMessage")
                runOnUiThread {
                    Toast.makeText(context, "Hashed Message: $hashedMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val airplaneFilter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(airplaneModeReceiver, airplaneFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(airplaneModeReceiver, airplaneFilter)
        }

        val hashFilter = IntentFilter("com.example.broadcastapp.HASH_BROADCAST")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(hashReceiver, hashFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(hashReceiver, hashFilter)
        }

        val buttonExplicit = findViewById<Button>(R.id.send_broadcast_button)
        buttonExplicit.setOnClickListener {
            sendExplicitBroadcast()
        }

        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)
        val buttonSendHash = findViewById<Button>(R.id.buttonSendHash)

        buttonSendHash.setOnClickListener {
            val message = editTextMessage.text.toString()
            if (message.isNotEmpty()) {
                hashAndSendBroadcast(message)
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendExplicitBroadcast() {
        val intent = Intent("com.example.broadcastapp.EXPLICIT_BROADCAST")
        intent.setComponent(ComponentName(this, MyExplicitReceiver::class.java))
        intent.putExtra("message", "Hello from explicit broadcast!")
        sendBroadcast(intent)
        Log.d("MainActivity", "Explicit broadcast sent")
    }

    private fun hashAndSendBroadcast(message: String) {
        thread {
            val hashedMessage = hashMessage(message)
            Log.d("MainActivity", "Computed Hashed Message: $hashedMessage")

            runOnUiThread {
                val intent = Intent("com.example.broadcastapp.HASH_BROADCAST")
                intent.setPackage(packageName)  // Ensure the broadcast is explicit
                intent.putExtra("hashedMessage", hashedMessage)
                sendBroadcast(intent)
                Log.d("MainActivity", "Hash broadcast sent")
            }
        }
    }

    private fun hashMessage(message: String): String {
        val bytes = message.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(airplaneModeReceiver)
        unregisterReceiver(hashReceiver)
    }
}
