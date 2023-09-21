package likelion.project.dongnation.ui.userInfo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentUserInfoProfileBinding
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity
import java.util.UUID

class UserInfoProfileFragment : Fragment() {

    lateinit var fragmentUserInfoProfileBinding: FragmentUserInfoProfileBinding
    lateinit var mainActivity: MainActivity

    val db = Firebase.firestore
    val userId = LoginViewModel.loginUserInfo.userId

    var selectedUri: Uri? = null
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedUri = uri
            fragmentUserInfoProfileBinding.imageViewUserInfoProfile.setImageURI(uri)
            fragmentUserInfoProfileBinding.imageViewUserInfoPlus.isVisible = false
            fragmentUserInfoProfileBinding.imageViewUserInfoDelete.isVisible = true
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
                    if (selectedUri == null) {
                        startPicker()
                    }

                }
            }

            // 프로필 삭제
            imageViewUserInfoDelete.setOnClickListener {
                imageViewUserInfoProfile.setImageURI(null)
                selectedUri = null
                imageViewUserInfoDelete.isVisible = false
                imageViewUserInfoPlus.isVisible = true
            }

            // 등록하기 버튼
            buttonUserInfoRegister.run {
                setOnClickListener {
                    setupInfoRegister()
                }
            }

        }

        return fragmentUserInfoProfileBinding.root
    }

    private fun startPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setupInfoRegister() {
        showProgress()
        if (selectedUri != null) {
            val photoUri = selectedUri ?: return
            uploadImage(
                uri = photoUri,
                successHandler = {
                    uploadArticle(userId, it)
                },
                errorHandler = {
                    Log.d("UserInfoProfileFragment", "Error uploading image")
                }
            )

        } else {
            hideProgress()
            Snackbar.make(requireView(), "이미지가 선택되지 않았습니다", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showProgress() {
        fragmentUserInfoProfileBinding.progressBarUserInfoProfile.isVisible = true
    }

    private fun hideProgress() {
        fragmentUserInfoProfileBinding.progressBarUserInfoProfile.isVisible = false
    }

    // 이미지를 Firebase Storage에 업로드
    private fun uploadImage(
        uri: Uri,
        successHandler: (String) -> Unit,
        errorHandler: (Throwable?) -> Unit
    ) {
        val fileName = "${UUID.randomUUID()}.png"
        FirebaseStorage.getInstance().reference.child("userProfile/").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    FirebaseStorage.getInstance().reference.child("userProfile/").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener {
                            successHandler(it.toString())
                        }
                        .addOnFailureListener {
                            errorHandler(it)
                        }
                }else {
                    errorHandler(task.exception)
                }

            }
    }

    private fun uploadArticle(userId : String, photoUrl : String) {
        db.collection("users")
            .document(userId)
            .update("userProfile", photoUrl)
            .addOnSuccessListener {
                mainActivity.removeFragment(MainActivity.USER_INFO_PROFILE_FRAGMENT)
            }
            .addOnFailureListener {
                Log.d("UserInfoProfileFragment", "${it.printStackTrace()}")
            }
    }

}