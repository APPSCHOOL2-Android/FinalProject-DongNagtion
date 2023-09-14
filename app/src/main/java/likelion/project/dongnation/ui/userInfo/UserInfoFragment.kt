package likelion.project.dongnation.ui.userInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
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

            // 송금 코드
            layoutInfoRemit.setOnClickListener {
                mainActivity.replaceFragment("TransferFragment", true, null)
                mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
            }

            // 배송지 변경
            layoutInfoChangeAddress.setOnClickListener {
                mainActivity.replaceFragment("LocationSettingFragment",true, null)
                mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
            }

            // 이웃 팔로잉
            layoutInfoFollow.setOnClickListener {
                mainActivity.replaceFragment("UserInfoFollowingFragment", true, null)
            }

            // 게시판 작성 글
            layoutInfoMyBoard.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.USER_INFO_BOARD_FRAGMENT, true, null)
            }

            // 로그아웃
            layoutInfoLogout.setOnClickListener {
                mainActivity.replaceFragment("LoginFragment", false, null)
                mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
                Snackbar.make(requireView(), "로그아웃이 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
            }

            // 계정 탈퇴
            layoutInfoWithdrawal.setOnClickListener {
            }

        }

        return fragmentUserInfoBinding.root
    }
}