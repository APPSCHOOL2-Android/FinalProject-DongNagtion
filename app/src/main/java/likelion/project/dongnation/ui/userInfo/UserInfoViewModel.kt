package likelion.project.dongnation.ui.userInfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import likelion.project.dongnation.repository.BoardRepository
import likelion.project.dongnation.repository.ChattingRoomRepository
import likelion.project.dongnation.repository.DonateRepository
import likelion.project.dongnation.repository.ReviewRepository
import likelion.project.dongnation.repository.UserRepository
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class UserInfoViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val donationRepository = DonateRepository()
    private val reviewRepository = ReviewRepository()
    private val boardRepository = BoardRepository()
    private val chattingRoomRepository = ChattingRoomRepository()

    val userProfileLiveData = MutableLiveData<String?>()
    val signOutStatus = MutableLiveData<Boolean>()

    fun getUserProfileInfo(userId: String) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            userProfileLiveData.postValue(userProfile)
        }
    }

    fun signOut(signOutType: Int){
        when(signOutType){
            LoginViewModel.LOGIN_KAKAO -> signOutKAKAO()
            LoginViewModel.LOGIN_NAVER -> signOutNAVER()
            LoginViewModel.LOGIN_GOOGLE -> signOutGOOGLE()
        }
    }

    private fun signOutKAKAO(){
        UserApiClient.instance.unlink { error ->
            if (error != null){
                Log.e("signOut", error.printStackTrace().toString())
            } else {
                Log.d("signOut", "카카오 연결 끊기 성공")
                deleteUserInfo()
            }
        }
    }

    private fun signOutNAVER(){
        NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
            override fun onSuccess() {
                deleteUserInfo()
                Log.d("signOut", "네이버 연결 끊기 성공")
            }
            override fun onFailure(httpStatus: Int, message: String) {
                Log.d("signOut", "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.d("signOut", "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }

    private fun signOutGOOGLE(){
        Firebase.auth.signOut()
        deleteUserInfo()
    }

    private fun deleteUserInfo()
    = viewModelScope.async {
        val user = LoginViewModel.loginUserInfo.copy()
        Log.d("signOut", "dslfk")

        val result = async {
            userRepository.deleteUser(user)
            donationRepository.deleteDonation(user)
            reviewRepository.deleteReview(user)
            boardRepository.deleteTip(user)
            chattingRoomRepository.deleteChattingRoom(user)
            true
        }
        signOutStatus.value = result.await()
    }
}