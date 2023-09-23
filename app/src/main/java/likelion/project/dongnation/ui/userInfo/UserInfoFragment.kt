package likelion.project.dongnation.ui.userInfo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentUserInfoBinding
import likelion.project.dongnation.databinding.ItemUserInfoDrawelCheckBinding
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class UserInfoFragment : Fragment() {

    lateinit var fragmentUserInfoBinding: FragmentUserInfoBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: UserInfoViewModel
    val userId = LoginViewModel.loginUserInfo.userId
    var userReviewRate = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserInfoBinding = FragmentUserInfoBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]

        viewModel.run {
            viewModel.userProfileLiveData.observe(viewLifecycleOwner) { userProfileUrl ->

                if (!userProfileUrl.isNullOrBlank()) {
                    Glide.with(this@UserInfoFragment)
                        .load(userProfileUrl)
                        .circleCrop()
                        .into(fragmentUserInfoBinding.imageViewInfoProfile)

                } else {
                    fragmentUserInfoBinding.imageViewInfoProfile.setImageResource(R.drawable.ic_account_circle_48dp)
                }
            }
            viewModel.getUserProfileInfo(userId)
        }

        fragmentUserInfoBinding.run {
            // 바텀 네비게이션 보이게하기
            mainActivity.activityMainBinding.bottomNavigation.visibility = View.VISIBLE

            toolbarUserInfo.title = "내 정보"
            textViewInfoNickname.text = LoginViewModel.loginUserInfo.userName

            showUserInfo()

            // 프로필 등록
            layoutInfoProfile.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.USER_INFO_PROFILE_FRAGMENT, true, null)
                mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
            }

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
                mainActivity.selectBottomNavigationItem(R.id.item_bottom_donate)
                mainActivity.replaceFragment("LoginFragment", false, null)
                mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
                Snackbar.make(requireView(), "로그아웃이 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
            }

            // 계정 탈퇴
            layoutInfoWithdrawal.setOnClickListener {
                val binding = ItemUserInfoDrawelCheckBinding.inflate(LayoutInflater.from(context))
                val builder = MaterialAlertDialogBuilder(mainActivity)
                builder.setView(binding.root)
                val dialog = builder.create()

                binding.buttonDrawelCheckYes.setOnClickListener {
                    mainActivity.selectBottomNavigationItem(R.id.item_bottom_donate)
                    mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
                    mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT, false, null)
                    Snackbar.make(requireView(), "탈퇴 처리가 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
                    dialog.dismiss()

                    viewModel.signOut(LoginViewModel.loginUserInfo.userType, mainActivity)
                }

                binding.buttonDrawelCheckNo.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }

        }

        return fragmentUserInfoBinding.root
    }

    private fun showUserInfo() {
        Firebase.firestore.collection("users").whereEqualTo("userId", LoginViewModel.loginUserInfo.userId).get().addOnSuccessListener { result ->
            for(document in result) {
                val userTransCode = document["userTransCode"] as String
                val userAddress = document["userAddress"] as String
                val userExperience = document["userExperience"] as Long
                fragmentUserInfoBinding.textViewUserAddress.text = userAddress
                fragmentUserInfoBinding.buttonReviewExp.text = "$userExperience 회"
                if(userTransCode == "") {
                    fragmentUserInfoBinding.textViewUserTransCode.text = "[송금 코드가 아직 저장되어 있지 않습니다.]"
                } else {
                    fragmentUserInfoBinding.textViewUserTransCode.text = "[송금 코드가 저장되어 있습니다.]"
                }
            }
        }

        var userRate = 0.0
        var documentSize = 0

        Firebase.firestore.collection("Donations").whereEqualTo("donationUser", LoginViewModel.loginUserInfo.userId).get().addOnSuccessListener {  result ->
            for(document in result) {
                val donationIdx = document["donationIdx"] as String
                Log.d("테스트경원", donationIdx)
                Firebase.firestore.collection("Reviews").whereEqualTo("donationBoardId", donationIdx).get().addOnSuccessListener { result2 ->
                    for(document2 in result2){
                        val reviewRate = document2["reviewRate"] as String
                        userRate += reviewRate.toDouble()
                        documentSize++
                    }
                    if(documentSize != 0) {
                        userReviewRate = userRate / documentSize.toDouble()
                        fragmentUserInfoBinding.buttonInfoReviewStar.text = "${(userReviewRate * 10.0).roundToInt() / 10.0}"
                    }
                }
            }
        }
    }
    fun Double.roundToInt(): Int {
        return if (this >= 0) {
            (this + 0.5).toInt()
        } else {
            (this - 0.5).toInt()
        }
    }
}