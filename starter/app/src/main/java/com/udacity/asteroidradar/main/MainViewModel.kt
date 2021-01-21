package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.getDate
import com.udacity.asteroidradar.network.ApiStatus
import com.udacity.asteroidradar.network.AsteroidFilter
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : ViewModel() {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    val pictureOfDay = asteroidRepository.pictureOfDay

    private var asteroidFilterSelected = AsteroidFilter.WEEK

    init {

        search(asteroidFilterSelected)

        viewModelScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                asteroidRepository.refresh()
                search(asteroidFilterSelected)
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                _asteroids.value = ArrayList()
            }
        }
    }

    fun onAsteroidClicked(id: Long) {

    }

    fun search(asteroidFilter: AsteroidFilter) {
        asteroidFilterSelected = asteroidFilter
        viewModelScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                _asteroids.value = when (asteroidFilterSelected) {
                    AsteroidFilter.WEEK -> {
                        asteroidRepository.getAsteroids(
                            Date(),
                            getDate(Constants.DEFAULT_END_DATE_DAYS)
                        )
                    }
                    AsteroidFilter.TODAY -> {
                        asteroidRepository.getAsteroids(Date(), Date())
                    }
                    AsteroidFilter.SAVED -> {
                        asteroidRepository.getAllAsteroids()
                    }
                }
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                _asteroids.value = ArrayList()
            }
        }
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