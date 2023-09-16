package likelion.project.dongnation.ui.donate

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.Snackbar
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentDonateWriteBinding
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.ui.gallery.GalleryImage
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class DonateWriteFragment : Fragment() {

    lateinit var fragmentDonateWriteBinding: FragmentDonateWriteBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: DonateViewModel

    // 이미지 Uri 저장 리스트
    private val imagesUri = mutableListOf<Uri>()
    private var isUploading = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDonateWriteBinding = FragmentDonateWriteBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[DonateViewModel::class.java]

        setupUI()
        setupImageUploadListener()

        return fragmentDonateWriteBinding.root
    }

    private fun setupUI() {
        // UI 초기화 코드
        fragmentDonateWriteBinding.run {
            mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
            val category = resources.getStringArray(R.array.array_donate_category)

            spinnerDonateWriteCategory.run {
                adapter = SpinnerAdapter(mainActivity, R.layout.item_spinner, category)
            }

            linearLayoutDonateWriteChoiceImg.setOnClickListener {
                mainActivity.replaceFragment("GalleryFragment", true, null)
            }

            buttonDonateWriteSave.setOnClickListener {
                handleDonateSave()

            }

            imageView1DonateWrite.setOnClickListener {
                clearImageView(0)
            }

            imageView2DonateWrite.setOnClickListener {
                clearImageView(1)
            }

            imageView3DonateWrite.setOnClickListener {
                clearImageView(2)
            }
        }
    }

    private fun setupImageUploadListener() {
        // 이미지 업로드 리스너 설정 코드
        mainActivity.supportFragmentManager.setFragmentResultListener("images", viewLifecycleOwner) { key, bundle ->
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelableArray("imageList", GalleryImage::class.java)
            } else {
                bundle.getParcelableArray("imageList")
            }

            // 이미지 URI를 임시로 저장
            result?.let { images ->
                images.forEachIndexed { index, item ->
                    if (item is GalleryImage) {
                        val uri = item.uri
                        imagesUri.add(uri)
                        displayImage(index, uri)
                    }
                }
            }
        }
    }

    private fun displayImage(index: Int, uri: Uri) {
        // 이미지를 UI에 표시하는 코드
        val imageViewId = when (index) {
            0 -> R.id.imageView1_donate_write
            1 -> R.id.imageView2_donate_write
            2 -> R.id.imageView3_donate_write
            else -> 3
        }

        if (imageViewId != 0) {
            val imageView = fragmentDonateWriteBinding.root.findViewById<ImageView>(imageViewId)
            Glide.with(requireContext())
                .load(uri)
                .into(imageView)
        }
    }

    private fun clearImageView(index: Int) {
        // 이미지 뷰 초기화 코드
        val imageViewId = when (index) {
            0 -> R.id.imageView1_donate_write
            1 -> R.id.imageView2_donate_write
            2 -> R.id.imageView3_donate_write
            else -> 3
        }

        if (imageViewId != 0) {
            val imageView = fragmentDonateWriteBinding.root.findViewById<ImageView>(imageViewId)
            imageView.setImageDrawable(null)

            // 이미지 URL이 저장되어 있는 경우 삭제
            if (imagesUri.size > index) {
                imagesUri.removeAt(index)
            }
        }
    }

    // 재능 기부 글 저장
    private fun handleDonateSave() {
        val title = fragmentDonateWriteBinding.editTextDonateWriteTitle.text.toString()
        val subTitle = fragmentDonateWriteBinding.editTextDonateWriteSubTitle.text.toString()
        val content = fragmentDonateWriteBinding.editTextDonateWriteContent.text.toString()
        val selectCategory = fragmentDonateWriteBinding.spinnerDonateWriteCategory.selectedItemPosition

        if (donateValidation(title, subTitle, content, selectCategory) == "재능 기부 글이 저장되었습니다.") {
            val userId = LoginViewModel.loginUserInfo.userId
            val type = if (fragmentDonateWriteBinding.radioGroupDonateWriteType.checkedRadioButtonId == R.id.radioButton_donate_write_type_help_me) {
                "도와주세요"
            } else {
                "도와드릴게요"
            }
            
            val donate = Donations(
                donationTitle = title,
                donationSubtitle = subTitle,
                donationType = type,
                donationUser = userId,
                donationCategory = resources.getStringArray(R.array.array_donate_category)[selectCategory],
                donationContent = content,
                donationImg =  mutableListOf()
            )

            uploadImagesAndSaveDonate(donate)
        }
    }

    private fun uploadImagesAndSaveDonate(donate: Donations) {
        // 이미지 업로드 및 기부 저장 중일 때는 다시 호출되지 않도록 체크
        if (isUploading) {
            return
        }
        isUploading = true

        val viewModel = ViewModelProvider(this)[DonateViewModel::class.java]

        val uploadTasks = imagesUri.mapIndexed { index, uri ->
            viewModel.uploadImage(uri).addOnSuccessListener { imageUrl ->
                val imageUrlStr = imageUrl.toString()
                donate.donationImg.add(imageUrlStr)

                // 모든 이미지 업로드가 완료된 경우 donate 객체를 Firestore에 저장
                if (donate.donationImg.size == imagesUri.size) {

                    viewModel.addDonate(donate).addOnCompleteListener { saveTask ->
                        isUploading = false // 업로드 완료 후 플래그를 false로 설정
                        if (saveTask.isSuccessful) {
                            val bundle = Bundle()
                            bundle.putString("donationIdx", "${donate.donationIdx}")
                            mainActivity.removeFragment("DonateWriteFragment")
                            mainActivity.replaceFragment("DonateInfoFragment", true, bundle)
                        } else {
                            // 기부 저장 중 오류가 발생한 경우
                            Snackbar.make(requireView(), "저장 중 오류가 발생했습니다.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                isUploading = false // 업로드 완료 후 플래그를 false로 설정
                Snackbar.make(requireView(), "이미지 업로드 중 오류가 발생했습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }

        Tasks.whenAll(uploadTasks)
        Snackbar.make(requireView(), "재능 기부 글이 저장되었습니다.", Snackbar.LENGTH_SHORT).show()
    }

    // 데이터 유효성 검사
    fun donateValidation(title: String, subTitle: String, content: String, selectCategory: Int): String{
        val snackBarText = when {
            selectCategory == 0 -> "카테고리를 선택해주세요."
            title.isEmpty() -> "제목을 입력해주세요."
            subTitle.isEmpty() -> "대표 문구를 입력해주세요."
            content.isEmpty() -> "내용을 입력해주세요."
            imagesUri.isEmpty() -> "사진은 최소 1장 이상 업로드 해주세요."
            else -> "재능 기부 글이 저장되었습니다."
        }

        return snackBarText
    }
}