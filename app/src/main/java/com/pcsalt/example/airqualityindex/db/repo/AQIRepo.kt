package com.pcsalt.example.airqualityindex.db.repo

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pcsalt.example.airqualityindex.db.dao.AQIDao
import com.pcsalt.example.airqualityindex.db.entity.AQIData
import com.pcsalt.example.airqualityindex.network.OnTextMessage
import com.pcsalt.example.airqualityindex.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class AQIRepo(private val aqiDao: AQIDao) {

    suspend fun insertAll(data: List<AQIData>) = aqiDao.insertAll(data)

    fun getLatestData() = aqiDao.getLatestData()

    fun registerForEvents() {
        Logger.d("registering for events")
        EventBus.getDefault().register(this)
    }

    fun unregisterForEvents() {
        Logger.d("unregistering for events")
        EventBus.getDefault().unregister(this)
    }

//    private var lastReceived: Long = 0L

    @Subscribe()
    fun onNetworkText(data: OnTextMessage) {
//        val now = System.currentTimeMillis()
//        if (now.isMoreThan30Sec(lastReceived)) {
//            lastReceived = now
            Logger.d("received: ${data.text}")
            try {
                val listType = object : TypeToken<List<AQIData?>?>() {}.type
                val dataList: List<AQIData> = GsonBuilder().create().fromJson(data.text, listType)
                CoroutineScope(Dispatchers.IO).launch {
                    aqiDao.insertAll(dataList)
                }
            } catch (e: Exception) {
                Logger.d("error: ${e.message}")
                e.printStackTrace()
            }
//        }
    }
}
