package com.vks.locationtest

import androidx.lifecycle.LiveData

class LocationRepository(private val locationDao: LocationDao) {

    val allLocations: LiveData<List<LocationEntity>> = locationDao.getAllLocations()

    suspend fun insert(locationEntity: LocationEntity) {
        locationDao.insert(locationEntity)
    }

    suspend fun delete(locationEntity: LocationEntity) {
        locationDao.delete(locationEntity)
    }

    suspend fun getLocation(id: Int) = locationDao.getLocation(id)

    suspend fun update(locationEntity: LocationEntity) {
        locationDao.update(locationEntity)
    }
}