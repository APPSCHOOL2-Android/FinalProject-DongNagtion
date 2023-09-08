package likelion.project.dongnation.db.remote

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import likelion.project.dongnation.api.NaverMapClient
import likelion.project.dongnation.api.NaverMapService
import likelion.project.dongnation.model.Geocoding
import likelion.project.dongnation.model.ReverseGeocoding

class NaverMapDataSource {

    private val apiService = NaverMapClient.apiService

    suspend fun getGeocoding(query: String): Flow<Result<Geocoding>> {
        return flow {
            runCatching {
                apiService.getGeocoding(query)
            }.onSuccess {
                emit(Result.success(it.body()!!))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    suspend fun getReverseGeocoding(coords: String): Flow<Result<ReverseGeocoding>> {
        return flow {
            runCatching {
                apiService.getReverseGeocoding(coords)
            }.onSuccess {
                emit(Result.success(it.body()!!))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }
}