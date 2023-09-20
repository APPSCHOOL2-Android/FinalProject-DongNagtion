package likelion.project.dongnation.ui.review

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.repository.ReviewRepository

class ReviewViewModel : ViewModel() {
    private val reviewRepository = ReviewRepository()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun getReviews(donationIdx: String) {
        Log.d("TEST4", "${donationIdx}")
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

    companion object {
        const val REVIERW_SUCCESS = "리뷰 작성 성공"
    }
}

data class UiState(
    val reviews: List<Review> = emptyList(),
    val message: String = "",
)