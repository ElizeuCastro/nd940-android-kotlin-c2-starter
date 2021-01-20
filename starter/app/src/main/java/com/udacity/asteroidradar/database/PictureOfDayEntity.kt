package com.udacity.asteroidradar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.PictureOfDay

@Entity(tableName = "picture_of_the_day_table")
data class PictureOfDayEntity(
    @PrimaryKey
    val title: String,
    val url: String,
    @ColumnInfo(name = "media_type")
    val mediaType: String
)

fun PictureOfDayEntity.asDomainModel(): PictureOfDay =
    PictureOfDay(
        mediaType,
        title,
        url
    )