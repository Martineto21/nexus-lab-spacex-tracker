package com.nexuslab.spacextracker.data.api

import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.data.model.Rocket
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpaceXApiService {
    
    @GET("launches")
    suspend fun getAllLaunches(): Response<List<Launch>>
    
    @GET("launches/latest")
    suspend fun getLatestLaunch(): Response<Launch>
    
    @GET("launches/next")
    suspend fun getNextLaunch(): Response<Launch>
    
    @GET("launches/{id}")
    suspend fun getLaunchById(@Path("id") id: String): Response<Launch>
    
    @GET("launches")
    suspend fun getLaunches(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<List<Launch>>
    
    @GET("rockets")
    suspend fun getAllRockets(): Response<List<Rocket>>
    
    @GET("rockets/{id}")
    suspend fun getRocketById(@Path("id") id: String): Response<Rocket>
    
    companion object {
        const val BASE_URL = "https://api.spacexdata.com/v4/"
    }
}