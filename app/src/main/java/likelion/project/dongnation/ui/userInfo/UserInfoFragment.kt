package likelion.project.dongnation.ui.userInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentUserInfoBinding
import likelion.project.dongnation.ui.main.MainActivity

class UserInfoFragment : Fragment() {

    lateinit var fragmentUserInfoBinding: FragmentUserInfoBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserInfoBinding = FragmentUserInfoBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentUserInfoBinding.run {
            toolbarUserInfo.run {
                title = "내 정보"
            }
        }

        return fragmentUserInfoBinding.root
    }
}