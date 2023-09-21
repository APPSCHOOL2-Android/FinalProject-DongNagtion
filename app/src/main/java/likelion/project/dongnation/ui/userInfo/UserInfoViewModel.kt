package likelion.project.dongnation.ui.userInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import likelion.project.dongnation.repository.UserRepository

class UserInfoViewModel : ViewModel() {

    val userRepository = UserRepository()

    val userProfileLiveData = MutableLiveData<String?>()

    fun getUserProfileInfo(userId: String) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            userProfileLiveData.postValue(userProfile)
        }
    }
}