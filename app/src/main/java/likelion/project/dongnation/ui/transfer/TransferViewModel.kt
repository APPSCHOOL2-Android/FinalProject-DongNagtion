package likelion.project.dongnation.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.GeocodingUI
import likelion.project.dongnation.model.ReverseGeocodingUI
import likelion.project.dongnation.model.User
import likelion.project.dongnation.model.toGeocodingUI
import likelion.project.dongnation.model.toReverseGeocodingUI
import likelion.project.dongnation.repository.UserRepository

class TransferViewModel: ViewModel() {
    private val userRepository = UserRepository()
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateTransferCode(user: User) {
        viewModelScope.launch {
            userRepository.updateTransferCode(user).collect {
                it.onSuccess {
                    _uiState.update { it.copy(showMessage = "송금 코드 등록 성공") }
                }.onFailure {
                    _uiState.update { it.copy(showMessage = "송금 코드 등록 실패") }
                }
            }
        }
    }
}

data class UiState(
    val showMessage: String = ""
)