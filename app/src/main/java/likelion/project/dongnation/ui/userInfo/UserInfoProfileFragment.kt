package likelion.project.dongnation.ui.userInfo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentUserInfoProfileBinding
import likelion.project.dongnation.ui.main.MainActivity

class UserInfoProfileFragment : Fragment() {

    lateinit var fragmentUserInfoProfileBinding: FragmentUserInfoProfileBinding
    lateinit var mainActivity: MainActivity

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserInfoProfileBinding = FragmentUserInfoProfileBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentUserInfoProfileBinding.run {

            materialToolbarUserInfoProfile.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.USER_INFO_PROFILE_FRAGMENT)
                }
            }

            // 프로필 이미지
            imageViewUserInfoProfile.run {
                setOnClickListener {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }

            // 등록하기 버튼
            buttonUserInfoRegister.run {
                setOnClickListener {

                }
            }

        }

        return fragmentUserInfoProfileBinding.root
    }

}