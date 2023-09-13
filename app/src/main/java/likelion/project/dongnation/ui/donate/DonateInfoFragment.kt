package likelion.project.dongnation.ui.donate

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentDonateInfoBinding
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity
import likelion.project.dongnation.ui.review.ItemSpacingDecoration

class DonateInfoFragment : Fragment() {

    lateinit var fragmentDonateInfoBinding: FragmentDonateInfoBinding
    lateinit var mainActivity: MainActivity

    val imgList = ArrayList<String>()
    lateinit var donate: Donations
    private var rate = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDonateInfoBinding = FragmentDonateInfoBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        donate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("donation", Donations::class.java)!!
        } else {
            arguments?.getParcelable("donation")!!
        }
        rate = arguments?.getDouble("rate")!!

        fragmentDonateInfoBinding.run {
            imgList.add(R.drawable.ic_launcher_logo_foreground.toString())
            imgList.add(R.drawable.ic_launcher_logo_foreground.toString())
            imgList.add(R.drawable.ic_launcher_logo_foreground.toString())

            buttonDonateInfoBack.setOnClickListener {
                mainActivity.removeFragment("DonateInfoFragment")
            }

            viewpager2DonateInfoThumbnail.adapter = DonateInfoFragmentStateAdapter(mainActivity)
            setupTabLayoutMediator()

            initDonateInfo()
            initUserInfo()
            buttonSetting()

            recyclerViewDonateInfoReview.run {
                adapter = DonateReviewAdapter(donate.donationReview, minOf(donate.donationReview.size, 3))
                addItemDecoration(ItemSpacingDecoration(20))
                isNestedScrollingEnabled = false
            }

            textViewDonateInfoMore.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelableArrayList("reviews", ArrayList(donate.donationReview))
                mainActivity.replaceFragment("ReviewShowFragment", true, bundle)
            }

            buttonDonateInfoChat.setOnClickListener {
                mainActivity.replaceFragment("ChattingFragment", true, null)
            }

            buttonDonateInfoDonation.setOnClickListener {

            }
        }

        return fragmentDonateInfoBinding.root
    }

    inner class DonateInfoFragmentStateAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
        // 보여줄 페이지 수
        override fun getItemCount(): Int = minOf(imgList.size, 3)

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DonateInfoViewPagerFragment(imgList[position])
                1 -> DonateInfoViewPagerFragment(imgList[position])
                else -> DonateInfoViewPagerFragment(imgList[position])
            }
        }
    }

    private fun setupTabLayoutMediator(){
        TabLayoutMediator(
            fragmentDonateInfoBinding.tabLayoutDonateInfoDot,
            fragmentDonateInfoBinding.viewpager2DonateInfoThumbnail
        ) {tab, position -> }.attach()
    }

    private fun initDonateInfo(){
        fragmentDonateInfoBinding.run {
            textViewDonateInfoTitle.text = donate.donationTitle
            textViewDonateInfoSubTitle.text = donate.donationSubtitle
            textViewDonateInfoCategory.text = donate.donationCategory
            textViewDonateInfoReviewScore.text = rate.toString()
            textViewDonateInfoContent.text = donate.donationContent
            textViewDonateInfoReviewNumber.text = "(${donate.donationReview.size})"
        }
    }

    private fun initUserInfo(){
        val viewModel = ViewModelProvider(this)[DonateViewModel::class.java]

        viewModel.run {
            userLiveData.observe(viewLifecycleOwner){ user ->
                fragmentDonateInfoBinding.run {
                    textViewDonateInfoUserName.text = user.userName
                    textViewDonateInfoLacation.text = user.userAddress
                    textViewDonateInfoExperience.text = user.userExperience.toString()
                }
            }

            findUserInfo(donate.donationUser)
        }
    }

    private fun buttonSetting(){
        fragmentDonateInfoBinding.run {
            if (donate.donationType == "도와주세요"){
                buttonDonateInfoDonation.visibility = View.GONE
            }
            if (donate.donationUser == LoginViewModel.loginUserInfo.userId){
                buttonDonateInfoDonation.visibility = View.GONE
                buttonDonateInfoChat.visibility = View.GONE
                buttonDonateInfoDelete.visibility = View.VISIBLE
                buttonDonateInfoModify.visibility = View.VISIBLE

                buttonDonateInfoModify.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("donate", donate)
                    mainActivity.replaceFragment("DonateModifyFragment", true, bundle)
                }

                buttonDonateInfoDelete.setOnClickListener {

                }
            }
        }
    }
}