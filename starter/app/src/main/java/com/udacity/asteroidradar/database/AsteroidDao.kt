package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
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
    fun getAsteroids(from: Date, to: Date): LiveData<List<AsteroidEntity>>

    @Query("select * from asteroid_table order by close_approach_date asc")
    fun getAllAsteroids(): LiveData<List<AsteroidEntity>>

    @Query("delete from asteroid_table where id in(:ids)")
    fun deleteByIds(ids: List<Long>)

}