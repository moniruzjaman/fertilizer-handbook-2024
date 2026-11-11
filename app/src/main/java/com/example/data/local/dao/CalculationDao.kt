package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CachedCalculationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {

    @Query("SELECT * FROM cached_calculations ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<CachedCalculationEntity>>

    @Query("SELECT * FROM cached_calculations ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentCalculations(limit: Int = 15): Flow<List<CachedCalculationEntity>>

    @Query("SELECT * FROM cached_calculations WHERE id = :id")
    suspend fun getCalculationById(id: String): CachedCalculationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: CachedCalculationEntity)

    @Query("DELETE FROM cached_calculations WHERE id = :id")
    suspend fun deleteCalculationById(id: String)

    @Query("DELETE FROM cached_calculations")
    suspend fun clearAllCalculations()

    @Query("SELECT COUNT(*) FROM cached_calculations")
    suspend fun getCount(): Int
}
