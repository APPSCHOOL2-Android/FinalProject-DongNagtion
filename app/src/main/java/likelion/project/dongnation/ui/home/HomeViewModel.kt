package likelion.project.dongnation.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.repository.DonateRepository

class HomeViewModel : ViewModel() {
    private val donateRepository = DonateRepository()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadDonations() {
        viewModelScope.launch {
            donateRepository.getAllDonate().collect {
                it.onSuccess { donations ->
                    _uiState.update {
                        it.copy(isInitDanationList = true, donationList = donations)
                    }
                }.onFailure {

                }
            }
        }
    }

    fun searchDonate(word: String) {
        viewModelScope.launch {
            donateRepository.getAllDonate().collect {
                it.onSuccess { donations ->
                    val filteredDonations =
                        donations.filter { donation ->
                            donation.donationTitle.contains(word, ignoreCase = true) ||
                                    donation.donationSubtitle.contains(word, ignoreCase = true) ||
                                    donation.donationContent.contains(word, ignoreCase = true)
                        }
                    _uiState.update {
                        it.copy(isInitDanationList = true, donationList = filteredDonations)
                    }
                }.onFailure {

                }
            }
        }
    }
}

data class UiState(
    val isInitDanationList: Boolean = false,
    val donationList: List<Donations> = emptyList(),
)