package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CalculationDao
import com.example.data.local.dao.FrgGuidelineDao
import com.example.data.local.dao.PlotDao
import com.example.data.local.entity.CachedCalculationEntity
import com.example.data.local.entity.CachedPlotEntity
import com.example.data.local.entity.FrgGuidelineEntity

@Database(
    entities = [
        CachedCalculationEntity::class,
        FrgGuidelineEntity::class,
        CachedPlotEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FrgAppDatabase : RoomDatabase() {

    abstract fun calculationDao(): CalculationDao
    abstract fun frgGuidelineDao(): FrgGuidelineDao
    abstract fun plotDao(): PlotDao

    companion object {
        @Volatile
        private var INSTANCE: FrgAppDatabase? = null

        fun getInstance(context: Context): FrgAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FrgAppDatabase::class.java,
                    "frg2024_offline_cache.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
