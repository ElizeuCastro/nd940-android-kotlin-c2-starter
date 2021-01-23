package com.udacity.asteroidradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

enum class AsteroidFilter {
    WEEK,
    TODAY,
    SAVED
}

enum class ApiStatus { LOADING, ERROR, DONE }

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .readTimeout(60, TimeUnit.SECONDS)
    .connectTimeout(60, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        var request = chain.request()
        val url = request.url().newBuilder().addQueryParameter("api_key", BuildConfig.NASA_API_KEY)
            .build()
        request = request.newBuilder().url(url).build()
        chain.proceed(request.newBuilder().url(url).build())
    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .client(okHttpClient)
    .build()

interface AsteroidApiService {

    @GET("neo/rest/v1/feed")
    suspend fun getNearEarthObjects(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): NearEarthObjects

}

interface PictureOfDayService {

    @GET("planetary/apod")
    suspend fun getPictureOfDay(): PictureOfDayProperty

}


object AsteroidApi {
    val feedService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
    val pictureOfDayService: PictureOfDayService by lazy {
        retrofit.create(PictureOfDayService::class.java)
    }
}