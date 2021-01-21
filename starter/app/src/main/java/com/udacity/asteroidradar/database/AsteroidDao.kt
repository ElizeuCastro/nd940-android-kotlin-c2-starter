package com.udacity.asteroidradar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.*

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: AsteroidEntity)

    @Query("select * from asteroid_table where close_approach_date between :from and :to order by close_approach_date asc")
    fun getAsteroids(from: Date, to: Date): List<AsteroidEntity>

    @Query("select * from asteroid_table order by close_approach_date asc")
    fun getAllAsteroids(): List<AsteroidEntity>

}