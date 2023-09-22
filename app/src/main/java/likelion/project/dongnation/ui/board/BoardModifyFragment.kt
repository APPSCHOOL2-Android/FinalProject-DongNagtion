package likelion.project.dongnation.ui.board

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentBoardModifyBinding
import likelion.project.dongnation.model.Tips
import likelion.project.dongnation.ui.gallery.GalleryImage
import likelion.project.dongnation.ui.main.MainActivity
import java.util.UUID

class BoardModifyFragment : Fragment() {

    lateinit var fragmentBoardModifyBinding: FragmentBoardModifyBinding
    lateinit var mainActivity: MainActivity

    lateinit var board: Tips

    val db = Firebase.firestore
    val storage = FirebaseStorage.getInstance()

    // 이미지 URL을 저장하는 목록
    val images = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardModifyBinding = FragmentBoardModifyBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        board = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("board", Tips::class.java)!!
        } else {
            arguments?.getParcelable("board")!!
        }

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
                        0 -> R.id.imageView1BoardModify
                        1 -> R.id.imageView2BoardModify
                        2 -> R.id.imageView3BoardModify
                        else -> 0
                    }

                    if (imageViewId != 0) {
                        val imageView = fragmentBoardModifyBinding.root.findViewById<ImageView>(imageViewId)
                        val galleryImage = images[i] as? GalleryImage
                        galleryImage?.uri?.let { uri ->
                            Glide.with(requireContext())
                                .load(uri)
                                .into(imageView)
                        }
                    }
                }
            }

            // 이미지를 Firebase Storage에 업로드하고 이미지 URL을 가져옴
            result?.forEachIndexed { index, item ->
                if (item is GalleryImage) {
                    val uri = item.uri
                    uploadImageToFirebaseStorage(
                        uri,
                        onSuccess = { imageUrl ->
                            // 바뀐 이미지만 추가되고, 나머지는 그대로 유지
                            if (index < images.size) {
                                images[index] = imageUrl
                            } else {
                                images.add(imageUrl)
                            }
                        },
                        onFailure = { e ->
                            // 이미지 업로드 실패 시 처리
                            Log.e("BoardWriteFragment", "Error uploading image", e)
                        }
                    )
                }
            }

        }

        fragmentBoardModifyBinding.run {

            initViews()

            materialToolbarBoardModify.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.BOARD_MODIFY_FRAGMENT)
                }
            }

            imageButtonBoardModify.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.GALLERY_FRAGMENT, true, null)
            }

           imageView1BoardModifyDelete.setOnClickListener {
                fragmentBoardModifyBinding.imageView1BoardModify.setImageDrawable(null)
                if (images.size >= 1) {
                    images.removeAt(0)
                }
            }

            imageView2BoardModifyDelete.setOnClickListener {
                fragmentBoardModifyBinding.imageView2BoardModify.setImageDrawable(null)
                if (images.size >= 2) {
                    images.removeAt(1)
                }
            }

            imageView3BoardModifyDelete.setOnClickListener {
                fragmentBoardModifyBinding.imageView3BoardModify.setImageDrawable(null)
                if (images.size >= 3) {
                    images.removeAt(2)
                }
            }

            buttonBoardModify.run {
                setOnClickListener {
                    hideKeyboard()
                    val newTitle = editTextBoardModifyTitle.text.toString()
                    val newContent = editTextBoardModifyContents.text.toString()

                    if (newTitle.isBlank() && newContent.isBlank()) {
                        Snackbar.make(requireView(), "제목과 내용을 입력해주세요.", Snackbar.LENGTH_SHORT).show()
                    }
                    else if (newTitle.isBlank()) {
                        Snackbar.make(requireView(), "제목을 입력해주세요.", Snackbar.LENGTH_SHORT).show()
                    }
                    else if (newContent.isBlank()) {
                        Snackbar.make(requireView(), "내용을 입력해주세요.", Snackbar.LENGTH_SHORT).show()
                    }
                    else {
                        // 새로운 제목과 내용으로 업데이트할 데이터 맵
                        val updatedData = mapOf(
                            "tipTitle" to newTitle,
                            "tipContent" to newContent,
                            "tipsImg" to images
                        )

                        // Firestore에서 해당 문서 업데이트
                        val documentRef = db.collection("tips").document(board.tipIdx)
                        documentRef
                            .update(updatedData)
                            .addOnSuccessListener {

                                mainActivity.removeFragment(MainActivity.BOARD_CONTENTS_FRAGMENT)
                                mainActivity.removeFragment(MainActivity.BOARD_MODIFY_FRAGMENT)
                                Snackbar.make(requireView(), "게시글이 수정되었습니다.", Snackbar.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {

                            }
                    }

                }
            }

        }

        return fragmentBoardModifyBinding.root
    }

    fun initViews() {
        fragmentBoardModifyBinding.apply {
            editTextBoardModifyTitle.text = Editable.Factory.getInstance().newEditable(board.tipTitle)
            editTextBoardModifyContents.text = Editable.Factory.getInstance().newEditable(board.tipContent)

            val imageViews = listOf(
                fragmentBoardModifyBinding.imageView1BoardModify,
                fragmentBoardModifyBinding.imageView2BoardModify,
                fragmentBoardModifyBinding.imageView3BoardModify
            )

            images.clear()

            for (i in imageViews.indices) {
                if (i < board.tipsImg.size) {
                    Glide.with(requireContext())
                        .load(board.tipsImg[i])
                        .into(imageViews[i])

                    images.add(board.tipsImg[i])
                }
            }

        }
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

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}