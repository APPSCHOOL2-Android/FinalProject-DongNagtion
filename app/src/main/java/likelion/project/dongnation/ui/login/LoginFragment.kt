package likelion.project.dongnation.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import likelion.project.dongnation.databinding.FragmentLoginBinding
import likelion.project.dongnation.ui.main.MainActivity

class LoginFragment : Fragment() {
    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        replace()
        clickButton()

        return fragmentLoginBinding.root
    }

    private fun replace(){
        loginViewModel.loginState.observe(viewLifecycleOwner, Observer {
            when(it){
                LoginViewModel.LOGIN_KAKAO_SUCCESS -> {
                    mainActivity.replaceFragment("HomeFragment", false, null)
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
        }
    }
}