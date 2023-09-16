package likelion.project.dongnation.ui.donate

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.model.User
import likelion.project.dongnation.repository.DonateRepository
import likelion.project.dongnation.repository.UserRepository
import javax.security.auth.callback.Callback

class DonateViewModel : ViewModel() {
    private val donateRepository = DonateRepository()
    private val userRepository = UserRepository()

    val donateLiveData = MutableLiveData<Donations>()
    val userLiveData = MutableLiveData<User?>()
    val experienceLiveData = MutableLiveData<Int>()

    fun addDonate(donate : Donations) : Task<Void> {
        return donateRepository.addDonate(donate)
    }

    fun uploadImage(uri: Uri): Task<Uri> {
        return donateRepository.uploadImage(uri)
    }

    fun findDonateInfo(donationIdx : String){
        viewModelScope.launch {
            val donate = donateRepository.getOneDonate(donationIdx)
            donateLiveData.postValue(donate)
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

    suspend fun modifyDonate(donationIdx : String, donate : Donations) : Task<Void>{
        return donateRepository.modifyDonate(donationIdx, donate)
    }

    fun deleteImage(uri : String) {
        return donateRepository.deleteImage(uri)
    }
}