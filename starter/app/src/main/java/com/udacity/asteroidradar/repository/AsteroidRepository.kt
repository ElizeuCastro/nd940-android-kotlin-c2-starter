package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.pictureOfDayDao.getPictureOfDay()) {
            it.asDomainModel()
        }

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            refreshAsteroids()
            refreshPictureOfDay()
        }
    }

    private suspend fun refreshAsteroids() {
        val result = AsteroidApi.feedService.getNearEarthObjects()
        database.asteroidDao.insertAll(*result.asDatabaseModel())
    }

    private suspend fun refreshPictureOfDay() {
        val result = AsteroidApi.pictureOfDayService.getPictureOfDay()
        database.pictureOfDayDao.insert(result.asDatabaseModel())
    }
}