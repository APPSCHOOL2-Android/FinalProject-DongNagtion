package likelion.project.dongnation.api

import likelion.project.dongnation.model.Geocoding
import likelion.project.dongnation.model.ReverseGeocoding
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverMapService {
    @GET("/map-geocode/v2/geocode")
    suspend fun getGeocoding(
        @Query("query") query: String,
    ): Response<Geocoding>

    @GET("/map-reversegeocode/v2/gc")
    suspend fun getReverseGeocoding(
        @Query("coords") coords: String,
        @Query("orders") orders: String = "addr",
        @Query("output") output: String = "json",
    ): Response<ReverseGeocoding>
}