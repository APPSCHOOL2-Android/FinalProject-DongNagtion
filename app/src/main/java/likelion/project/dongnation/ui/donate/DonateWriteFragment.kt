package likelion.project.dongnation.ui.donate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentDonateWriteBinding
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class DonateWriteFragment : Fragment() {

    lateinit var fragmentDonateWriteBinding: FragmentDonateWriteBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: DonateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDonateWriteBinding = FragmentDonateWriteBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(this)[DonateViewModel::class.java]


        fragmentDonateWriteBinding.run {
            mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
            val category = resources.getStringArray(R.array.array_donate_category)

            toolbarDonateWrite.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment("DonateWriteFragment")
                }
            }

            spinnerDonateWriteCategory.run {
                adapter = SpinnerAdapter(mainActivity, R.layout.item_spinner, category)
            }

            linearLayoutDonateWriteChoiceImg.setOnClickListener {
                mainActivity.replaceFragment("GalleryFragment", true, null)
            }

            buttonDonateWriteSave.setOnClickListener {
                if(donateValidation()){
                    val title = editTextDonateWriteTitle.text.toString()
                    val subTitle = editTextDonateWriteSubTitle.text.toString()
                    val content = editTextDonateWriteContent.text.toString()
                    val category = category[spinnerDonateWriteCategory.selectedItemPosition]
                    val userId = LoginViewModel.loginUserInfo.userId
                    val type = if(radioGroupDonateWriteType.checkedRadioButtonId == R.id.radioButton_donate_write_type_help_me){
                        "도와주세요"
                    } else {
                        "도와드릴게요"
                    }

                    val donate = Donations(title, subTitle, type, userId, category, content)
                    viewModel.addDonate(donate)

                    mainActivity.removeFragment("DonateWriteFragment")
                }
            }
        }

        return fragmentDonateWriteBinding.root
    }

    // 데이터 유효성 검사
    fun donateValidation() : Boolean{
        fragmentDonateWriteBinding.run {
            val categoryPosition = spinnerDonateWriteCategory.selectedItemPosition
            val titleText = editTextDonateWriteTitle.text.toString()
            val subTitleText = editTextDonateWriteSubTitle.text.toString()
            val contentText = editTextDonateWriteContent.text.toString()

            val snackBarText = when {
                categoryPosition == 0 -> "카테고리를 선택해주세요."
                titleText.isEmpty() -> "제목을 입력해주세요."
                subTitleText.isEmpty() -> "대표 문구를 입력해주세요."
                contentText.isEmpty() -> "내용을 입력해주세요."
                else -> "재능 기부 글이 저장되었습니다."
            }

            val snackBar = Snackbar.make(requireView(), snackBarText, Snackbar.LENGTH_SHORT)
            snackBar.animationMode = Snackbar.ANIMATION_MODE_SLIDE

            snackBar.show()

            if (snackBarText == "재능 기부 글이 저장되었습니다."){
                return true
            }

            return false
        }
    }
}