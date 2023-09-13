package likelion.project.dongnation.ui.board

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentBoardWriteBinding
import likelion.project.dongnation.ui.gallery.GalleryImage
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity
import java.util.Date
import java.util.UUID


class BoardWriteFragment : Fragment() {

    lateinit var fragmentBoadWriteBinding: FragmentBoardWriteBinding
    lateinit var mainActivity: MainActivity

    val db = Firebase.firestore
    val userId = LoginViewModel.loginUserInfo.userId
    val userName = LoginViewModel.loginUserInfo.userName
    val storage = FirebaseStorage.getInstance()

    // 이미지 URL을 저장하는 목록
    val images = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoadWriteBinding = FragmentBoardWriteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 이미지를 Firebase Storage에 업로드하고 Firestore에 저장하는 부분
        mainActivity.supportFragmentManager.setFragmentResultListener("images", viewLifecycleOwner) { key, bundle ->
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelableArray("imageList", GalleryImage::class.java)
            } else {
                bundle.getParcelableArray("imageList")
            }

            // 이미지를 UI에 표시
            result?.let { images ->
                for (i in images.indices) {
                    val imageViewId = when (i) {
                        0 -> R.id.imageView1BoardWrite
                        1 -> R.id.imageView2BoardWrite
                        2 -> R.id.imageView3BoardWrite
                        else -> 0
                    }

                    if (imageViewId != 0) {
                        val imageView = fragmentBoadWriteBinding.root.findViewById<ImageView>(imageViewId)
                        val galleryImage = images[i] as? GalleryImage
                        galleryImage?.uri?.let { uri ->
                            Glide.with(requireContext())
                                .load(uri)
                                .into(imageView)
                        }
                    }
                }
            }

            images.clear() // 이미지 목록 초기화

            // 이미지를 Firebase Storage에 업로드하고 이미지 URL을 가져옴
            result?.forEachIndexed { index, item ->
                if (item is GalleryImage) {
                    val uri = item.uri
                    uploadImageToFirebaseStorage(
                        uri,
                        onSuccess = { imageUrl ->
                            images.add(imageUrl)
                        },
                        onFailure = { e ->
                            // 이미지 업로드 실패 시 처리
                            Log.e("BoardWriteFragment", "Error uploading image", e)
                        }
                    )
                }
            }

            // 이미지 삭제 이벤트 핸들러
            fragmentBoadWriteBinding.imageView1BoardWriteDelete.setOnClickListener {
                // 이미지뷰 초기화
                fragmentBoadWriteBinding.imageView1BoardWrite.setImageDrawable(null)

                // 이미지 URL 목록에서 제거
                if (images.size >= 1) {
                    images.removeAt(0)
                }
            }

            fragmentBoadWriteBinding.imageView2BoardWriteDelete.setOnClickListener {
                fragmentBoadWriteBinding.imageView2BoardWrite.setImageDrawable(null)
                if (images.size >= 2) {
                    images.removeAt(1)
                }
            }

            fragmentBoadWriteBinding.imageView3BoardWriteDelete.setOnClickListener {
                fragmentBoadWriteBinding.imageView3BoardWrite.setImageDrawable(null)
                if (images.size >= 3) {
                    images.removeAt(2)
                }
            }

        }

        fragmentBoadWriteBinding.run {
            // 바텀 네비게이션 안 보이게 하기
            mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE

            materialToolbarBoardWrite.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.BOARD_WRITE_FRAGMENT)
                }
            }

            // 이미지 선택 클릭
            imageButtonBoardWrite.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.GALLERY_FRAGMENT, true, null)
            }

            fragmentBoadWriteBinding.buttonBoardWrite.setOnClickListener {

                Log.d("BoardWriteFragment", "images : ${images}")

                // 이미지가 선택된 경우
                if (images.isNotEmpty()) {
                    saveDataToFirestore(images)
                } else {
                    val tipTitle = fragmentBoadWriteBinding.editTextBoardWriteTitle.text.toString()
                    val tipContent = fragmentBoadWriteBinding.editTextBoardWrite.text.toString()

                    saveDataNoImgToFirestore(emptyList(), tipTitle, tipContent)
                }

            }



        }

        return fragmentBoadWriteBinding.root
    }

    // 이미지를 Firebase Storage에 업로드하는 함수
    private fun uploadImageToFirebaseStorage(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("tipsImg/${UUID.randomUUID()}.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()
                        onSuccess(imageUrl)
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // Firestore에 이미지와 데이터를 저장하는 함수
    private fun saveDataToFirestore(images: List<String>) {
        val tipsCollection = db.collection("tips")

        val tipWriterId = userId
        val tipWriterName = userName
        val tipTitle = fragmentBoadWriteBinding.editTextBoardWriteTitle.text.toString()
        val tipContent = fragmentBoadWriteBinding.editTextBoardWrite.text.toString()

        // 현재 시간을 얻어와서 Timestamp로 변환
        val tipDate = Timestamp(Date())

        // 데이터를 매핑해서 Firestore 문서로 추가
        val newTip = hashMapOf(
            "tipWriterId" to tipWriterId,
            "tipWriterName" to tipWriterName,
            "tipTitle" to tipTitle,
            "tipContent" to tipContent,
            "tipDate" to tipDate,
            "tipsImg" to images
        )

        tipsCollection.add(newTip)
            .addOnSuccessListener { documentReference ->
                // 추가한 데이터의 문서 ID를 가져온다
                val newTipId = documentReference.id

                // 문서 ID를 사용하여 tipIdx 필드를 업데이트한다
                tipsCollection.document(newTipId)
                    .update("tipIdx", newTipId)
                    .addOnSuccessListener {
                        // 작성이 완료되면 프래그먼트를 제거
                        mainActivity.removeFragment(MainActivity.BOARD_WRITE_FRAGMENT)
                    }
                    .addOnFailureListener { e ->
                        Log.d("BoardWriteFragment", "Error updating tipIdx", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.d("BoardWriteFragment", "Error adding document", e)
            }
    }

    // 이미지 없이, Firestore에 데이터를 저장하는 함수
    private fun saveDataNoImgToFirestore(images: List<String>, tipTitle: String, tipContent: String) {
        val tipsCollection = db.collection("tips")

        val tipWriterId = userId
        val tipWriterName = userName

        val tipDate = Timestamp(Date())

        val newTip = hashMapOf(
            "tipWriterId" to tipWriterId,
            "tipWriterName" to tipWriterName,
            "tipTitle" to tipTitle,
            "tipContent" to tipContent,
            "tipDate" to tipDate,
            "tipsImg" to images
        )

        tipsCollection.add(newTip)
            .addOnSuccessListener { documentReference ->

                val newTipId = documentReference.id

                tipsCollection.document(newTipId)
                    .update("tipIdx", newTipId)
                    .addOnSuccessListener {
                        mainActivity.removeFragment(MainActivity.BOARD_WRITE_FRAGMENT)
                    }
                    .addOnFailureListener { e ->
                        Log.d("BoardWriteFragment", "Error updating tipIdx", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.d("BoardWriteFragment", "Error adding document", e)
            }
    }

}

