package com.vks.locationtest

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: LocationEntity)

    @Update
    suspend fun update(note: LocationEntity)

    @Delete
    suspend fun delete(note: LocationEntity)

    @Query("Select * from Location")
    fun getAllLocations(): LiveData<List<LocationEntity>>

    @Query("Select * from Location Where id = :id")
    suspend fun getLocation(id: Int): LocationEntity
}