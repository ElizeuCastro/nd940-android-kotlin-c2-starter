package com.udacity.asteroidradar.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.database.AsteroidEntity

@JsonClass(generateAdapter = true)
data class NearEarthObjects(
        @Json(name = "element_count") val elementCount: Int,
        @Json(name = "near_earth_objects") val asteroids: Map<String, List<NearEarth>>
)

@JsonClass(generateAdapter = true)
data class NearEarth(
        @Json(name = "id") val id: String,
        @Json(name = "name") val name: String,
        @Json(name = "absolute_magnitude_h") val absoluteMagnitude: Double,
        @Json(name = "estimated_diameter") val estimatedDiameter: EstimatedDiameter,
        @Json(name = "is_potentially_hazardous_asteroid") val isPotentiallyHazardousAsteroid: Boolean,
        @Json(name = "close_approach_data") val closeApproachData: List<CloseApproachData>
)

@JsonClass(generateAdapter = true)
data class EstimatedDiameter(
        @Json(name = "kilometers") val kilometers: EstimatedDiameterKilometer
)

@JsonClass(generateAdapter = true)
data class EstimatedDiameterKilometer(
        @Json(name = "estimated_diameter_min") val minimumDiameter: Double,
        @Json(name = "estimated_diameter_max") val maximumDiameter: Double
)

@JsonClass(generateAdapter = true)
data class CloseApproachData(
        @Json(name = "close_approach_date") val closeApproachDate: String,
        @Json(name = "relative_velocity") val relativeVelocity: RelativeVelocity,
        @Json(name = "miss_distance") val missDistance: MissDistance
)

@JsonClass(generateAdapter = true)
data class RelativeVelocity(
        @Json(name = "kilometers_per_second") val kilometersPerSecond: Double
)

@JsonClass(generateAdapter = true)
data class MissDistance(
        @Json(name = "astronomical") val astronomical: Double
)

fun NearEarthObjects.asDatabaseModel(): Array<AsteroidEntity> =
        asteroids.entries.map { it.value }.flatten().map { asteroid ->
                AsteroidEntity(
                        remoteId = asteroid.id,
                        name = asteroid.name,
                        closeApproachDate = asteroid.closeApproachData.first().run {
                                closeApproachDate
                        }.toString(),
                        absoluteMagnitude = asteroid.absoluteMagnitude,
                        estimatedDiameter = asteroid.estimatedDiameter.kilometers.maximumDiameter,
                        relativeVelocity = asteroid.closeApproachData.first().run {
                                relativeVelocity.kilometersPerSecond
                        }.toDouble(),
                        distanceFromEarth = asteroid.closeApproachData.first().run {
                                missDistance.astronomical
                        }.toDouble(),
                        isPotentiallyHazardous = asteroid.isPotentiallyHazardousAsteroid
                )
        }.toTypedArray()
