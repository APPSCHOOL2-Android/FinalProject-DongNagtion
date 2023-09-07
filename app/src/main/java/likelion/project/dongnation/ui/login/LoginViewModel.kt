package likelion.project.dongnation.ui.login

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.runBlocking
import likelion.project.dongnation.model.User
import likelion.project.dongnation.repository.UserRepository
import likelion.project.dongnation.ui.main.MainActivity
import org.json.JSONObject

class LoginViewModel : ViewModel() {

    private var userRepository = UserRepository()

    fun login(loginType: Int, mainActivity: MainActivity){
        when(loginType){
            LOGIN_ATTEMPT_KAKAO -> {
                loginKAKAO(mainActivity)
            }
        }
    }

    // 카카오 로그인
    private fun loginKAKAO(mainActivity: MainActivity){
        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
            } else if (token != null) {
                val user= tokenToUserKAKAO(token)
                runBlocking {
                    if (user != null) {
                        saveWithDuplicateChecking(user)
                    }
                }
            }
        }

        // 카카오 실행 가능 여부 검사
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(mainActivity)) {
            // 카카오 로그인
            UserApiClient.instance.loginWithKakaoTalk(mainActivity) { token, error ->
                if (error != null) {
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(
                        mainActivity,
                        callback = callback
                    )
                }
                // 카카오 로그인 성공
                else if (token != null) {
                    val user = tokenToUserKAKAO(token)
                    // 로그인
                    runBlocking {
                        if (user != null) {
                            saveWithDuplicateChecking(user)
                        }
                    }
                }
            }
        } else {
            // 카카오톡이 설치되어있지 않은 경우 SnackBar로 표시
        }
    }

    private suspend fun saveWithDuplicateChecking(user: User): Int{
        val userList = userRepository.getUser(user)
        return if(userList.size != 0){
            Log.d("login", "기존 유저 존재")
            Log.d("login", "${userList[0].userId}")
            LOGIN_SUCCESS_KAKAO
        } else {
            Log.d("login", "기존 유저 없음")
            userRepository.saveUser(user)
            LOGIN_SUCCESS_KAKAO
        }
    }

    private fun tokenToUserKAKAO(token: OAuthToken): User? {
        val idTokenList = token.idToken?.split(".")
        if (idTokenList != null) {
            // 사용자 규격 정보
            val idTokenPayload = Base64.decode(idTokenList[1], Base64.DEFAULT)
                .toString(Charsets.UTF_8)
            Log.i("login", idTokenPayload)
            val payloadJSONObject = JSONObject(idTokenPayload)
            val userName = payloadJSONObject["nickname"].toString()
            val userId = payloadJSONObject["sub"].toString()
            var customerEmail = try {
                payloadJSONObject["email"].toString()
            } catch (e: Exception) {
                "사용자 제공 미동의"
            }
            return User(LOGIN_ATTEMPT_KAKAO, userId, userName, customerEmail)
        } else {
            return null
        }
    }

    companion object {
        const val LOGIN_ATTEMPT_KAKAO = 0
        const val LOGIN_SUCCESS_KAKAO = 0
    }
}