package com.pcsalt.example.airqualityindex.util

import android.util.Log

object Logger {
    fun d(msg: String?) {
        Log.d("AQI", "msg: $msg")
    }
}