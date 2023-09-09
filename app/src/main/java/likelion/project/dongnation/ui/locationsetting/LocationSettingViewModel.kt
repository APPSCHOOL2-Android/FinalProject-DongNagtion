package likelion.project.dongnation.ui.locationsetting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.GeocodingUI
import likelion.project.dongnation.model.ReverseGeocodingUI
import likelion.project.dongnation.model.User
import likelion.project.dongnation.model.toGeocodingUI
import likelion.project.dongnation.model.toReverseGeocodingUI
import likelion.project.dongnation.repository.MapRepository
import likelion.project.dongnation.repository.UserRepository


class LocationSettingViewModel : ViewModel() {
    private val mapRepository = MapRepository()
    private val userRepository = UserRepository()
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState>
        get() = _uiState

    fun getGeocoding(query: String) {
        viewModelScope.launch {
            mapRepository.getGeocoding(query).collect { response ->
                response.onSuccess { geocoding ->
                    if (geocoding.status == "OK") {
                        _uiState.update {
                            it.copy(
                                geocodingUI = geocoding.toGeocodingUI(),
                                isGeocodingInitialized = true
                            )
                        }
                    }
                }.onFailure { throwable ->
                    throwable as Exception
                }
            }
        }
    }

    fun getReverseGeocoding(coords: String) {
        viewModelScope.launch {
            mapRepository.getReverseGeocoding(coords).collect { response ->
                response.onSuccess { reverseGeocoding ->
                    if (reverseGeocoding.status.code == 0) {
                        _uiState.update {
                            it.copy(
                                reverseGeocodingUI = reverseGeocoding.toReverseGeocodingUI(),
                                isReverseGeocodingInittialized = true,
                                geocodingUI = GeocodingUI(coords = coords),
                            )

                        }
                    }
                }.onFailure { throwable ->
                    throwable as Exception
                }
            }
        }
    }

    fun updateAddress(user: User) {
        viewModelScope.launch {
            userRepository.updateAddress(user).collect {
                it.onSuccess {
                    _uiState.update { it.copy(showMessage = "지역 설정 성공") }
                }.onFailure {
                    _uiState.update { it.copy(showMessage = "지역 설정 실패") }
                }
            }
        }
    }
}

data class UiState(
    val geocodingUI: GeocodingUI? = null,
    val reverseGeocodingUI: ReverseGeocodingUI? = null,
    val isGeocodingInitialized: Boolean = false,
    val isReverseGeocodingInittialized: Boolean = false,
    val showMessage: String = "",
)