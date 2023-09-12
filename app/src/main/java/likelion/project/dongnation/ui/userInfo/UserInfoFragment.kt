package likelion.project.dongnation.ui.userInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            toolbarUserInfo.title = "내 정보"

            // 이웃 팔로잉
            layoutInfoFollow.setOnClickListener {
                mainActivity.replaceFragment("UserInfoFollowingFragment", true, null)
            }

            // 게시판 작성 글
            layoutInfoMyBoard.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.USER_INFO_BOARD_FRAGMENT, true, null)
            }

        }

        return fragmentUserInfoBinding.root
    }
}