package com.udacity.asteroidradar.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.database.PictureOfDayEntity

@JsonClass(generateAdapter = true)
data class PictureOfDayProperty(
    @Json(name = "media_type") val mediaType: String,
    val title: String,
    val url: String
)

fun PictureOfDayProperty.asDatabaseModel(): PictureOfDayEntity =
    PictureOfDayEntity(
        title,
        url,
        mediaType
    )