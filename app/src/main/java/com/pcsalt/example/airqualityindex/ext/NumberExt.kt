package com.pcsalt.example.airqualityindex.ext

import android.text.format.DateUtils
import com.pcsalt.example.airqualityindex.R
import com.pcsalt.example.airqualityindex.util.Logger
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

const val BAND_1 = 1
const val BAND_2 = 2
const val BAND_3 = 3
const val BAND_4 = 4
const val BAND_5 = 5
const val BAND_6 = 6

fun Double.toTwoDecimal(): String {
    return BigDecimal(this)
        .setScale(2, RoundingMode.HALF_UP)
        .toString()
}

fun Double.getAQIBand(): Int {
    return when {
        this > 0 && this < 50 -> BAND_1
        this > 51 && this < 100 -> BAND_2
        this > 101 && this < 200 -> BAND_3
        this > 201 && this < 300 -> BAND_4
        this > 301 && this < 400 -> BAND_5
        else -> BAND_6
    }
}

fun Double.getAQIColor(): Int {
    return when (this.getAQIBand()) {
        BAND_1 -> R.color.aqi_0_50
        BAND_2 -> R.color.aqi_51_100
        BAND_3 -> R.color.aqi_101_200
        BAND_4 -> R.color.aqi_201_300
        BAND_5 -> R.color.aqi_301_400
        else -> R.color.aqi_401_500
    }
}

fun Long.toComparativeTime(): String {
    val current = System.currentTimeMillis()
    val diff = current - this
    val seconds = diff / 1000

    return if (seconds < 0) {
        return this.toDateTime()
    } else if (seconds < 15) {
        "A few seconds ago"
    } else if (seconds < 60) {
        "$seconds seconds"
    } else {
        val minutes = seconds / 60
        if (minutes < 2) {
            "1 minute ago"
        } else if (minutes < 60) {
            "$minutes minutes ago"
        } else {
            val hour = minutes / 60
            if (hour < 2) {
                "1 hour ago"
            } else {
                val isToday = DateUtils.isToday(this)
                if (isToday) {
                    this.toTime()
                } else {
                    this.toDateTime()
                }
            }
        }
    }
}

fun Long.toTime(): String {
    val dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    return dateFormat.format(Date(this))
}

fun Long.toDateTime(): String {
    val dateFormat = SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.getDefault())
    return dateFormat.format(Date(this))
}

fun Long.isMoreThan30Sec(last: Long): Boolean {
    val diff = (this - last) * 1.0f
    Logger.d("diff: $diff")
    return (diff / (5 * 1000L)) > 1
}