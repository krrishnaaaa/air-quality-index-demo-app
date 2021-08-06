package com.pcsalt.example.airqualityindex.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tbl_aqi_data")
data class AQIData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @SerializedName("city")
    @ColumnInfo(name = "city_name")
    var cityName: String = "",
    @SerializedName("aqi")
    @ColumnInfo(name = "aqi")
    var aqi: Double = 0.0,
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
): Comparable<AQIData> {
    override fun hashCode(): Int {
        return cityName.hashCode() * 31
    }

    override fun equals(other: Any?): Boolean {
        if (other is AQIData) {
            return (other.cityName == this.cityName)
        }
        return false
    }

    override fun compareTo(other: AQIData): Int {
        return this.cityName.compareTo(other.cityName)
    }
}
