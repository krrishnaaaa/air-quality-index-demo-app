package com.pcsalt.example.airqualityindex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pcsalt.example.airqualityindex.display.data.DisplayAQIDataFragment
import com.pcsalt.example.airqualityindex.network.SocketManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SocketManager.initConnection()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, DisplayAQIDataFragment())
            .commit()
    }

    override fun onDestroy() {
        SocketManager.disconnect()
        super.onDestroy()
    }
}