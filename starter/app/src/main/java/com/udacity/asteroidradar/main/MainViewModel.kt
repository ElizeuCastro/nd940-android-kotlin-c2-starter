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

    private val asteroidFilterSelected = MutableLiveData(AsteroidFilter.WEEK)
    val pictureOfDay = asteroidRepository.pictureOfDay
    val asteroids = Transformations.switchMap(asteroidFilterSelected) { filter ->
        when (filter) {
            AsteroidFilter.WEEK -> asteroidRepository.getAsteroids(
                Date(),
                getDate(Constants.DEFAULT_END_DATE_DAYS)
            )
            AsteroidFilter.TODAY -> asteroidRepository.getAsteroids(Date(), Date())
            else -> asteroidRepository.getAllAsteroids()
        }
    }

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                asteroidRepository.refresh()
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
            }
        }
    }

    fun search(asteroidFilter: AsteroidFilter) {
        asteroidFilterSelected.value = asteroidFilter
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun openAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
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