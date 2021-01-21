package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.getDate
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.toText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.pictureOfDayDao.getPictureOfDay()) {
            it?.asDomainModel()
        }

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            refreshAsteroids(
                Date(),
                getDate(Constants.DEFAULT_END_DATE_DAYS)
            )
            refreshPictureOfDay()
        }
    }

    private suspend fun refreshAsteroids(from: Date, to: Date) {
        val result = AsteroidApi.feedService.getNearEarthObjects(from.toText(), to.toText())
        database.asteroidDao.insertAll(*result.asDatabaseModel())
    }

    private suspend fun refreshPictureOfDay() {
        val result = AsteroidApi.pictureOfDayService.getPictureOfDay()
        database.pictureOfDayDao.insert(result.asDatabaseModel())
    }

    suspend fun getAsteroids(from: Date, to: Date) = withContext(Dispatchers.IO) {
        database.asteroidDao.getAsteroids(from, to).asDomainModel()
    }

    suspend fun getAllAsteroids() = withContext(Dispatchers.IO) {
        database.asteroidDao.getAllAsteroids().asDomainModel()
    }
}