package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.network.ApiStatus
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _asteroids: MutableLiveData<List<Asteroid>> = asteroidRepository.asteroids as MutableLiveData<List<Asteroid>>
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids
    val pictureOfDay = asteroidRepository.pictureOfDay

    init {
        viewModelScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                asteroidRepository.refresh()
                _status.value = ApiStatus.DONE
            } catch(e: Exception) {
                _status.value = ApiStatus.ERROR
                _asteroids.value = ArrayList()
            }

        }
    }

    fun onAsteroidClicked(id: Long) {

    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}