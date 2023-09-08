package likelion.project.dongnation.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import likelion.project.dongnation.db.remote.NaverMapDataSource
import likelion.project.dongnation.model.Geocoding
import likelion.project.dongnation.model.ReverseGeocoding

class MapRepository {
    private val naverMapDataSource = NaverMapDataSource()

    suspend fun getGeocoding(query: String): Flow<Result<Geocoding>> {
        return naverMapDataSource.getGeocoding(query)
    }

    suspend fun getReverseGeocoding(coords: String): Flow<Result<ReverseGeocoding>> {
        return naverMapDataSource.getReverseGeocoding(coords)
    }
}