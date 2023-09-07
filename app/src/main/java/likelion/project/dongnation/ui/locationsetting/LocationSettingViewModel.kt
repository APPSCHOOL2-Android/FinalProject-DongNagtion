package likelion.project.dongnation.ui.locationsetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class LocationSettingViewModel : ViewModel() {

//    private val _uiState = MutableStateFlow(UiState())
//    val uiState: StateFlow<UiState>
//        get() = _uiState
//
//    private val _uiEffect: MutableSharedFlow<UiEffect> = MutableSharedFlow()
//    val uiEffect: SharedFlow<UiEffect>
//        get() = _uiEffect

//    fun getGeocoding(query: String) {
//        viewModelScope.launch {
//            getGeocodingUseCase(query).collect { response ->
//                response.onSuccess { geocoding ->
//                    if (geocoding.status == "OK") {
//                        _uiState.update {
//                            it.copy(
//                                geocodingUI = geocoding.toGeocodingUI(),
//                                isGeocodingInitialized = true
//                            )
//                        }
//                    }
//                }.onFailure { throwable ->
//                    throwable as Exception
//                }
//            }
//        }
//    }
//
//    fun getReverseGeocoding(coords: String) {
//        viewModelScope.launch {
//            getReverseGeocodingUseCase(coords).collect { response ->
//                response.onSuccess { reverseGeocoding ->
//                    if (reverseGeocoding.status.code == 0) {
//                        _uiState.update {
//                            it.copy(
//                                reverseGeocodingUI = reverseGeocoding.toReverseGeocodingUI(),
//                                isReverseGeocodingInittialized = true,
//                                geocodingUI = GeocodingUI(coords = coords),
//                            )
//
//                        }
//                    } else {
//                        onFailMarkerClick()
//                    }
//                }.onFailure { throwable ->
//                    throwable as Exception
//                }
//            }
//        }
//    }
}

//data class UiState(
//    val geocodingUI: GeocodingUI? = null,
//    val reverseGeocodingUI: ReverseGeocodingUI? = null,
//    val isGeocodingInitialized: Boolean = false,
//    val isReverseGeocodingInittialized: Boolean = false,
//)
//
//sealed interface UiEffect {
//    data class ShowToastMessage(val resId: Int): UiEffect
//}