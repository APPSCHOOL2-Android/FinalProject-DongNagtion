package likelion.project.dongnation.ui.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.repository.DonateRepository
import likelion.project.dongnation.repository.ReviewRepository

class ReviewViewModel : ViewModel() {
    private val reviewRepository = ReviewRepository()
    private val donationRepository = DonateRepository()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _donations = MutableLiveData<Donations>()
    val donations: LiveData<Donations> = _donations

    fun getReviews(donationIdx: String) {
        viewModelScope.launch {
            reviewRepository.getReviews(donationIdx).collect {
                it.onSuccess { reviews ->
                    _uiState.update {
                        it.copy(reviews = reviews)
                    }
                }.onFailure { throwable ->
                    _uiState.update { it.copy(message = throwable.message.toString()) }
                }
            }
        }
    }

    fun addReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.addReview(review).collect {
                it.onSuccess {
                    _uiState.update {
                        it.copy(
                            message = REVIERW_SUCCESS
                        )
                    }
                }.onFailure { throwable ->
                    _uiState.update { it.copy(message = throwable.message.toString()) }
                }
            }
        }
    }

    fun getDonationsBoardInfo(donationIdx: String) {
        viewModelScope.launch {
            _donations.postValue(donationRepository.getOneDonate(donationIdx))
        }
    }

    companion object {
        const val REVIERW_SUCCESS = "리뷰 작성 성공"
    }
}

data class UiState(
    val donations: Donations = Donations(),
    val reviews: List<Review> = emptyList(),
    val message: String = "",
)