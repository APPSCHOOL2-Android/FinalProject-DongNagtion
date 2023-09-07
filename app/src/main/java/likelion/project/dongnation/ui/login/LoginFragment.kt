package likelion.project.dongnation.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
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

        fragmentLoginBinding.run{
            buttonLoginKakao.run{
                setOnClickListener {
                    loginViewModel.login(LoginViewModel.LOGIN_ATTEMPT_KAKAO, mainActivity)
                }
            }
        }
        return fragmentLoginBinding.root
    }
}