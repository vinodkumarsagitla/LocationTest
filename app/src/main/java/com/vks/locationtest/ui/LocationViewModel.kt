package com.vks.locationtest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.vks.locationtest.database.LocationDatabase
import com.vks.locationtest.database.LocationEntity
import com.vks.locationtest.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    val allLocations: LiveData<List<LocationEntity>>
    private val repository: LocationRepository

    init {
        val dao = LocationDatabase.getDatabase(application).getLocationDao()
        repository = LocationRepository(dao)
        allLocations = repository.allLocations
    }

    fun delete(locationEntity: LocationEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(locationEntity)
    }

    fun insertOrUpdate(locationEntity: LocationEntity, onInsert: () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            if (locationEntity.id > 0)
                repository.update(locationEntity)
            else
                repository.insert(locationEntity)
            onInsert.invoke()
        }

    fun getLocation(id: Int, onComplete: (LocationEntity) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            onComplete.invoke(repository.getLocation(id))
        }
}