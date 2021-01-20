package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PictureOfDayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pictureOfDay: PictureOfDayEntity)

    @Query("select * from picture_of_the_day_table ORDER BY 1 DESC LIMIT 1")
    fun getPictureOfDay(): LiveData<PictureOfDayEntity>
}