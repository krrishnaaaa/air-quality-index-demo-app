package com.pcsalt.example.airqualityindex.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pcsalt.example.airqualityindex.db.dao.AQIDao
import com.pcsalt.example.airqualityindex.db.entity.AQIData
import com.pcsalt.example.airqualityindex.util.Logger

@Database(
    entities = [AQIData::class],
    version = 1
)
abstract class AQIDb : RoomDatabase() {
    abstract fun aqiDao(): AQIDao

    companion object {
        @Volatile
        private var INSTANCE: AQIDb? = null

        @JvmStatic
        fun getAppDatabase(context: Context): AQIDb {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                Logger.d("Returning cached instance")
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AQIDb::class.java,
                    "expense_mgr.db"
                )
                    .build()
                INSTANCE = instance
                Logger.d("Returning new instance")
                return instance
            }
        }

        fun destroyDataBase() {
            INSTANCE = null
            Logger.d("Database instance destroyed")
        }
    }
}
