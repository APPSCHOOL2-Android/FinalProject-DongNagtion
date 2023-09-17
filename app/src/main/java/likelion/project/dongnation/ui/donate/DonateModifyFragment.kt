package likelion.project.dongnation.ui.donate

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentDonateWriteBinding
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.ui.gallery.GalleryImage
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class DonateModifyFragment : Fragment() {

    private lateinit var fragmentDonateModifyBinding : FragmentDonateWriteBinding
    lateinit var mainActivity : MainActivity

    // 이미지 URL을 저장하는 목록
    private var newImagesUri = mutableListOf<Uri>()
    private var isUploading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDonateModifyBinding = FragmentDonateWriteBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        val oldDonate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("donate", Donations::class.java)!!
        } else {
            arguments?.getParcelable("donate")!!
        }

        setupImageUploadListener()

        fragmentDonateModifyBinding.run {
            mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE

            initModifyDonateInfo(oldDonate)
            setupUI(oldDonate)
        }

        return fragmentDonateModifyBinding.root
    }

    private fun initModifyDonateInfo(oldDonate : Donations){
        fragmentDonateModifyBinding.run {
            if (oldDonate.donationType == "도와주세요"){
                radioGroupDonateWriteType.check(R.id.radioButton_donate_write_type_help_me)
            } else {
                radioGroupDonateWriteType.check(R.id.radioButton_donate_write_type_help_you)
            }

            val categoryArray = resources.getStringArray(R.array.array_donate_category)
            val categoryIndex = categoryArray.indexOf(oldDonate.donationCategory)

            if (categoryIndex != -1) {
                spinnerDonateWriteCategory.run {
                    adapter = SpinnerAdapter(mainActivity, R.layout.item_spinner, categoryArray)
                    spinnerDonateWriteCategory.setSelection(categoryIndex)
                }
            }

            editTextDonateWriteTitle.setText(oldDonate.donationTitle)
            editTextDonateWriteSubTitle.setText(oldDonate.donationSubtitle)
            editTextDonateWriteContent.setText(oldDonate.donationContent)
        }
    }

    private fun setupUI(oldDonate : Donations) {
        // UI 초기화 코드
        fragmentDonateModifyBinding.run {
            mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE

            toolbarDonateWrite.run{
                title = "재능 기부 글 수정"
                setNavigationOnClickListener {
                    mainActivity.removeFragment("DonateModifyFragment")
                }
            }

            linearLayoutDonateWriteChoiceImg.setOnClickListener {
                mainActivity.replaceFragment("GalleryFragment", true, null)
            }

            buttonDonateWriteSave.run{
                text = "수정하기"
                setOnClickListener {
                    lifecycleScope.launch {
                        handleDonateSave(oldDonate)
                    }
                }
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

    private fun handleDonateSave(oldDonate : Donations) {
        val title = fragmentDonateModifyBinding.editTextDonateWriteTitle.text.toString()
        val subTitle = fragmentDonateModifyBinding.editTextDonateWriteSubTitle.text.toString()
        val content = fragmentDonateModifyBinding.editTextDonateWriteContent.text.toString()
        val selectCategory = fragmentDonateModifyBinding.spinnerDonateWriteCategory.selectedItemPosition

        val snackBarText = modifyDonateValidation(title, subTitle, content, selectCategory)

        if (snackBarText == "재능 기부 글이 저장되었습니다.") {
            val userId = LoginViewModel.loginUserInfo.userId
            val type = if (fragmentDonateModifyBinding.radioGroupDonateWriteType.checkedRadioButtonId == R.id.radioButton_donate_write_type_help_me) {
                "도와주세요"
            } else {
                "도와드릴게요"
            }

            val newDonate = Donations(
                donationIdx = oldDonate.donationIdx,
                donationTitle = title,
                donationSubtitle = subTitle,
                donationType = type,
                donationUser = userId,
                donationCategory = resources.getStringArray(R.array.array_donate_category)[selectCategory],
                donationContent = content,
                donationImg =  mutableListOf()
            )

            // deleteOldImage(oldDonate.donationImg)
            uploadImagesAndSaveDonate(newDonate)

        } else {
            Snackbar.make(requireView(), snackBarText, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun uploadImagesAndSaveDonate(donate: Donations) {
        // 이미지 업로드 및 기부 저장 중일 때는 다시 호출되지 않도록 체크
        if (isUploading) {
            return
        }
        isUploading = true

        val viewModel = ViewModelProvider(this)[DonateViewModel::class.java]

        val uploadTasks = newImagesUri.mapIndexed { index, uri ->
            viewModel.uploadImage(uri).addOnSuccessListener { imageUrl ->
                val imageUrlStr = imageUrl.toString()
                donate.donationImg.add(imageUrlStr)

                if (donate.donationImg.size == newImagesUri.size) {
                    lifecycleScope.launch {
                        viewModel.modifyDonate(donate.donationIdx, donate).addOnCompleteListener { saveTask ->
                            isUploading = false // 업로드 완료 후 플래그를 false로 설정
                            if (saveTask.isSuccessful) {
                                mainActivity.removeFragment("DonateModifyFragment")
                                mainActivity.removeFragment("DonateInfoFragment")
                            } else {
                                // 기부 저장 중 오류가 발생한 경우
                                Snackbar.make(requireView(), "수정 중 오류가 발생했습니다.", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                isUploading = false // 업로드 완료 후 플래그를 false로 설정
                Snackbar.make(requireView(), "이미지 업로드 중 오류가 발생했습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }

        Tasks.whenAll(uploadTasks)
        Snackbar.make(requireView(), "재능 기부 글이 수정되었습니다.", Snackbar.LENGTH_SHORT).show()
    }

    private fun deleteOldImage(oldList: MutableList<String>){
        val viewModel = ViewModelProvider(this)[DonateViewModel::class.java]
        for (img in oldList){
            viewModel.deleteImage(img)
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
                        newImagesUri.add(uri)
                        displayImage(index, uri)
                    }
                }
            }
        }
    }

    private fun displayImage(index: Int, uri: Uri) {
        val imageViewId = when (index) {
            0 -> R.id.imageView1_donate_write
            1 -> R.id.imageView2_donate_write
            2 -> R.id.imageView3_donate_write
            else -> 3
        }

        if (imageViewId != 0) {
            val imageView = fragmentDonateModifyBinding.root.findViewById<ImageView>(imageViewId)
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
            val imageView = fragmentDonateModifyBinding.root.findViewById<ImageView>(imageViewId)
            imageView.setImageDrawable(null)

            // 이미지 URL이 저장되어 있는 경우 삭제
            if (newImagesUri.size > index) {
                newImagesUri.removeAt(index)
            }
        }
    }

    // 데이터 유효성 검사
    fun modifyDonateValidation(title: String, subTitle: String, content: String, selectCategory: Int): String{
        val snackBarText = when {
            selectCategory == 0 -> "카테고리를 선택해주세요."
            title.isEmpty() -> "제목을 입력해주세요."
            subTitle.isEmpty() -> "대표 문구를 입력해주세요."
            content.isEmpty() -> "내용을 입력해주세요."
            newImagesUri.isEmpty() -> "사진은 최소 1장 이상 업로드 해주세요."
            else -> "재능 기부 글이 저장되었습니다."
        }

        return snackBarText
    }
}