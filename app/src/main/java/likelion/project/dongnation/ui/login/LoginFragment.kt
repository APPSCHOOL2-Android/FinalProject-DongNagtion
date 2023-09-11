package likelion.project.dongnation.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.async
import likelion.project.dongnation.BuildConfig
import likelion.project.dongnation.databinding.FragmentLoginBinding
import likelion.project.dongnation.ui.main.MainActivity

class LoginFragment : Fragment() {
    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        replace()
        clickButton()
        callGOOGLEOneTapUI()

        return fragmentLoginBinding.root
    }

    private fun replace(){
        loginViewModel.loginState.observe(viewLifecycleOwner, Observer {
            when(it){
                LoginViewModel.LOGIN_KAKAO_SUCCESS -> {
                    mainActivity.replaceFragment("HomeFragment", false, null)
                    mainActivity.bottomNavigationBar()
                }
                LoginViewModel.LOGIN_NAVER_SUCCESS -> {
                    mainActivity.replaceFragment("HomeFragment", false, null)
                    mainActivity.bottomNavigationBar()
                }
                LoginViewModel.LOGIN_GOOGLE_SUCCESS -> {
                    mainActivity.replaceFragment("HomeFragment", false, null)
                    mainActivity.bottomNavigationBar()
                }
            }
        })
    }
    private fun clickButton(){
        fragmentLoginBinding.run{
            buttonLoginKakao.run{
                setOnClickListener {
                    loginViewModel.login(LoginViewModel.LOGIN_KAKAO, mainActivity)
                }
            }
            buttonLoginNaver.run{
                setOnClickListener {
                    loginViewModel.login(LoginViewModel.LOGIN_NAVER, mainActivity)
                }
            }
            buttonLoginGoogle.run{
                setOnClickListener {
                    loginViewModel.login(LoginViewModel.LOGIN_GOOGLE, mainActivity)
                }
            }
        }
    }

    private fun callGOOGLEOneTapUI(){
        loginViewModel.loginState.observe(viewLifecycleOwner) {
            when (it) {
                LoginViewModel.LOGIN_GOOGLE_ONE_TAP_REQUEST -> {
                    // 원탭 로그인 객체 설정
                    oneTapClient = Identity.getSignInClient(mainActivity)
                    signInRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(
                            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(BuildConfig.GOOGLE_OAUTH_WEB_CLIENT_ID)
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                        )
                        .setAutoSelectEnabled(true)
                        .build()

                    // 원탭 로그인
                    oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener(mainActivity) { result ->
                            try {
                                startIntentSenderForResult(
                                    result.pendingIntent.intentSender,
                                    LoginViewModel.LOGIN_GOOGLE_ONE_TAP_REQUEST,
                                    null, 0, 0, 0, null)
                            } catch (e: Exception) {
                                Log.e("login", "Couldn't start One Tap UI: ${e.localizedMessage}")
                            }
                        }
                        .addOnFailureListener {e ->
                            Log.d("login", e.localizedMessage)
                        }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            LoginViewModel.LOGIN_GOOGLE_ONE_TAP_REQUEST -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val password = credential.password
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d("login", "Got ID token.")
                            loginViewModel.signInCredentialToUserGOOGLE(credential)
                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d("login", "Got password.")
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                }
            }
        }
    }
}