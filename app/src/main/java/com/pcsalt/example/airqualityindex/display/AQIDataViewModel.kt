package com.pcsalt.example.airqualityindex.display

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pcsalt.example.airqualityindex.db.AQIDb
import com.pcsalt.example.airqualityindex.db.entity.AQIData
import com.pcsalt.example.airqualityindex.db.repo.AQIRepo
import kotlinx.coroutines.launch

class AQIDataViewModel(application: Application) : AndroidViewModel(application) {
    private var aqiRepo: AQIRepo

    init {
        val aqiDb = AQIDb.getAppDatabase(application)
        aqiRepo = AQIRepo(aqiDb.aqiDao())
        aqiRepo.registerForEvents()
    }

    fun insert(data: List<AQIData>) = viewModelScope.launch {
        aqiRepo.insertAll(data)
    }

    fun getLatestData() = aqiRepo.getLatestData()

    fun getLatestDataByCity(cityName: String) = aqiRepo.getLatestDataByCity(cityName)

    override fun onCleared() {
        aqiRepo.unregisterForEvents()
        super.onCleared()
    }
}