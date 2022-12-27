package com.example.traininglifecycleawarecomp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var button : Button
    lateinit var networkMonitor : NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        networkMonitor = NetworkMonitor(this)
        lifecycle.addObserver(networkMonitor)

        button = findViewById<Button>(R.id.button)
        button.setOnClickListener(View.OnClickListener {
            var intent = Intent(this@MainActivity, SecondActivity::class.java)
            startActivity(intent)
        })
   }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}