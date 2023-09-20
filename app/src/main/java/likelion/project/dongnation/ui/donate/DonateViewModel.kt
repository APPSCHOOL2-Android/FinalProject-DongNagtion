package likelion.project.dongnation.ui.donate

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.model.User
import likelion.project.dongnation.repository.DonateRepository
import likelion.project.dongnation.repository.ReviewRepository
import likelion.project.dongnation.repository.UserRepository
import javax.security.auth.callback.Callback

class DonateViewModel : ViewModel() {
    private val donateRepository = DonateRepository()
    private val userRepository = UserRepository()
    private val reviewRepository = ReviewRepository()

    val donateLiveData = MutableLiveData<Donations>()
    val userLiveData = MutableLiveData<User?>()
    val experienceLiveData = MutableLiveData<Int>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun addDonate(donate: Donations): Task<Void> {
        return donateRepository.addDonate(donate)
    }

    fun uploadImage(uri: Uri): Task<Uri> {
        return donateRepository.uploadImage(uri)
    }

    fun findDonateInfo(donationIdx: String) {
        viewModelScope.launch {
            val donate = donateRepository.getOneDonate(donationIdx)
            donateLiveData.postValue(donate)
        }
    }

    fun findUserInfo(userId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserForId(userId)
            userLiveData.postValue(user)
        }
    }

    fun addUserExperience(userId: String) {
        viewModelScope.launch {
            val experience = userRepository.addUserExperience(userId)
            experienceLiveData.postValue(experience)
        }
    }

    suspend fun modifyDonate(donationIdx: String, donate: Donations): Task<Void> {
        return donateRepository.modifyDonate(donationIdx, donate)
    }

    fun deleteImage(uri: String) {
        return donateRepository.deleteImage(uri)
    }

    fun getReviews(donationIdx: String) {
        viewModelScope.launch {
            reviewRepository.getReviews(donationIdx).collect {
                it.onSuccess { reviews ->
                    _uiState.update {
                        it.copy(
                            reviews = reviews
                        )
                    }
                }.onFailure {  throwable ->
                    _uiState.update { it.copy(throwable.message.toString()) }
                }
            }
        }
    }
}

data class UiState(
    val message: String = "",
    val reviews: List<Review> = emptyList()
)