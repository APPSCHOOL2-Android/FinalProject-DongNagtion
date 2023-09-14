package likelion.project.dongnation.ui.donate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import kotlin.math.round

class DonateInfoFragment : Fragment() {

    lateinit var fragmentDonateInfoBinding: FragmentDonateInfoBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: DonateViewModel

    val imgList = ArrayList<String>()
    private var donateIdx = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDonateInfoBinding = FragmentDonateInfoBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[DonateViewModel::class.java]

        donateIdx = arguments?.getString("donationIdx")!!

        initViews()
        viewModel.findDonateInfo(donateIdx)

        return fragmentDonateInfoBinding.root
    }

    private fun initViews() {
        fragmentDonateInfoBinding.apply {
            buttonDonateInfoBack.setOnClickListener {
                mainActivity.removeFragment("DonateInfoFragment")
            }

            buttonDonateInfoChat.setOnClickListener {
                mainActivity.replaceFragment("ChattingFragment", true, null)
            }

            buttonDonateInfoDonation.setOnClickListener {

            }
        }

        viewModel.run {
            donateLiveData.observe(viewLifecycleOwner) { donateInfo ->
                setDonationInfoViews(donateInfo)
                findUserInfo(donateInfo.donationUser)
            }

            userLiveData.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    fragmentDonateInfoBinding.run {
                        textViewDonateInfoUserName.text = user.userName
                        textViewDonateInfoLacation.text = user.userAddress
                        textViewDonateInfoExperience.text = user.userExperience.toString()
                    }
                }
            }
        }
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

    private fun setDonationInfoViews(donateInfo: Donations) {
        fragmentDonateInfoBinding.apply {
            buttonDonateInfoLike.setOnClickListener {
                viewModel.addUserExperience(donateInfo.donationUser)
                it.isClickable = false
                it.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.green100))
                viewModel.experienceLiveData.observe(viewLifecycleOwner) { experience ->
                    textViewDonateInfoExperience.text = experience.toString()
                }
            }

            recyclerViewDonateInfoReview.run {
                adapter = DonateReviewAdapter(donateInfo.donationReview, minOf(donateInfo.donationReview.size, 3))
                addItemDecoration(ItemSpacingDecoration(20))
                isNestedScrollingEnabled = false
            }

            textViewDonateInfoMore.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelableArrayList("reviews", ArrayList(donateInfo.donationReview))
                mainActivity.replaceFragment("ReviewShowFragment", true, bundle)
            }

            textViewDonateInfoTitle.text = donateInfo.donationTitle
            textViewDonateInfoSubTitle.text = donateInfo.donationSubtitle
            textViewDonateInfoCategory.text = donateInfo.donationCategory
            textViewDonateInfoContent.text = donateInfo.donationContent
            textViewDonateInfoReviewNumber.text = "(${donateInfo.donationReview.size})"

            if (donateInfo.donationImg.isNotEmpty()){
                for (image in donateInfo.donationImg){
                    imgList.add(image)
                }
            }

            viewpager2DonateInfoThumbnail.adapter = DonateInfoFragmentStateAdapter(mainActivity)
            setupTabLayoutMediator()

            if (donateInfo.donationType == "도와주세요") {
                buttonDonateInfoDonation.visibility = View.GONE
            }
            if (donateInfo.donationUser == LoginViewModel.loginUserInfo.userId) {
                buttonDonateInfoDonation.visibility = View.GONE
                buttonDonateInfoChat.visibility = View.GONE
                buttonDonateInfoDelete.visibility = View.VISIBLE
                buttonDonateInfoModify.visibility = View.VISIBLE

                buttonDonateInfoModify.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("donate", donateInfo)
                    mainActivity.replaceFragment("DonateModifyFragment", true, bundle)
                }

                buttonDonateInfoDelete.setOnClickListener {

                }
    fun getRateAverage(reviews : List<Review>) : Double{
        var total = 0.0

        if (reviews.isEmpty()){
            return total
        } else {
            for (review in reviews){
                total += review.reviewRate.toFloat()
            }
        }

        return round((total / reviews.size) * 10) / 10
    }
}