package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CachedPlotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlotDao {

    @Query("SELECT * FROM cached_plots ORDER BY createdAt DESC")
    fun getAllPlots(): Flow<List<CachedPlotEntity>>

    @Query("SELECT * FROM cached_plots WHERE id = :id LIMIT 1")
    suspend fun getPlotById(id: String): CachedPlotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlot(plot: CachedPlotEntity)

    @Query("DELETE FROM cached_plots WHERE id = :id")
    suspend fun deletePlotById(id: String)

    @Query("SELECT COUNT(*) FROM cached_plots")
    suspend fun getCount(): Int
}
