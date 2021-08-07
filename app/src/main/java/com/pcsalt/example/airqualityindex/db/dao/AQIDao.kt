package com.pcsalt.example.airqualityindex.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pcsalt.example.airqualityindex.db.entity.AQIData

@Dao
interface AQIDao {

    @Insert
    suspend fun insertAll(data: List<AQIData>)

    @Query(" SELECT * FROM tbl_aqi_data WHERE (city_name||'_'||last_updated) IN (SELECT city_name||'_'||max(last_updated) FROM tbl_aqi_data GROUP BY city_name) ORDER BY city_name")
    fun getLatestData(): LiveData<List<AQIData>>

    @Query("SELECT * FROM tbl_aqi_data WHERE city_name = :cityName LIMIT 100")
    fun getLatestDataByCity(cityName: String): LiveData<List<AQIData>>
}