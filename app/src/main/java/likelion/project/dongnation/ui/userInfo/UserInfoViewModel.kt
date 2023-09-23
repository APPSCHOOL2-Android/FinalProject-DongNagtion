package likelion.project.dongnation.ui.userInfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import likelion.project.dongnation.repository.UserRepository
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class UserInfoViewModel : ViewModel() {

    val userRepository = UserRepository()

    val userProfileLiveData = MutableLiveData<String?>()

    fun getUserProfileInfo(userId: String) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            userProfileLiveData.postValue(userProfile)
        }
    }

    fun signOut(signOutType: Int, mainActivity: MainActivity){
        when(signOutType){
            LoginViewModel.LOGIN_KAKAO -> signOutKAKAO()
        }
    }

    private fun signOutKAKAO(){
        UserApiClient.instance.unlink { error ->
            if (error != null){
                Log.e("signOut", error.printStackTrace().toString())
            } else {
                Log.d("signOut", "카카오 연결 끊기 성공")
            }
        }
    }
}