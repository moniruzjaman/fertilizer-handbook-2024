package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.FrgGuidelineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FrgGuidelineDao {

    @Query("SELECT * FROM frg_guidelines ORDER BY cropName ASC")
    fun getAllGuidelines(): Flow<List<FrgGuidelineEntity>>

    @Query("SELECT * FROM frg_guidelines WHERE category = :category ORDER BY cropName ASC")
    fun getGuidelinesByCategory(category: String): Flow<List<FrgGuidelineEntity>>

    @Query("SELECT * FROM frg_guidelines WHERE cropId = :cropId LIMIT 1")
    suspend fun getGuidelineByCropId(cropId: String): FrgGuidelineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuidelines(guidelines: List<FrgGuidelineEntity>)

    @Query("SELECT COUNT(*) FROM frg_guidelines")
    suspend fun getCount(): Int
}
