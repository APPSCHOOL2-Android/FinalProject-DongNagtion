package likelion.project.dongnation.ui.donate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.model.User
import likelion.project.dongnation.repository.DonateRepository
import likelion.project.dongnation.repository.UserRepository

class DonateViewModel : ViewModel() {
    private val donateRepository = DonateRepository()
    private val userRepository = UserRepository()

    val userLiveData = MutableLiveData<User>()
    val experienceLiveData = MutableLiveData<Int>()

    fun addDonate(donate : Donations){
        viewModelScope.launch(Dispatchers.IO) {
            donateRepository.addDonate(donate)
        }
    }

    fun findUserInfo(userId : String){
        viewModelScope.launch {
            val user = userRepository.getUserForId(userId)
            userLiveData.postValue(user)
        }
    }

    fun addUserExperience(userId: String){
        viewModelScope.launch {
            val experience = userRepository.addUserExperience(userId)
            experienceLiveData.postValue(experience)
        }
    }
}