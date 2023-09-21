package likelion.project.dongnation.ui.donate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentDonateInfoBinding
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity
import likelion.project.dongnation.ui.review.ItemSpacingDecoration
import likelion.project.dongnation.ui.review.ReviewAdapter
import kotlin.math.round

class DonateInfoFragment : Fragment() {

    lateinit var fragmentDonateInfoBinding: FragmentDonateInfoBinding
    lateinit var mainActivity: MainActivity
    private val reviewAdapter by lazy {
        ReviewAdapter()
    }

    lateinit var viewModel: DonateViewModel

    val imgList = ArrayList<String>()
    private var donateIdx = ""
    private var transferCode = ""

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
                val bundle = Bundle()
                bundle.putString("chattingRoomUserIdCounterpart",
                    viewModel.userLiveData.value?.userId
                )
                mainActivity.replaceFragment("ChattingFragment", true, bundle)
            }

            buttonDonateInfoDonation.setOnClickListener {
                if(transferCode == "") {
                    Snackbar.make(requireView(), "해당 회원의 송금코드가 존재하지 않습니다.", Snackbar.LENGTH_SHORT).show()
                } else {
                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse(transferCode))
                    startActivity(intent)
                }
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
                        transferCode = user.userTransCode

                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            fragmentDonateInfoBinding.progressBarDonateInfo.visibility = View.GONE
                        }, 500) // 1초(1000 밀리초) 후에 실행
                    }
                }
            }

            lifecycleScope.launch {
                getReviews(donateIdx)
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiState.collect {
                        reviewAdapter.submitList(it.reviews)
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
                adapter = reviewAdapter
                addItemDecoration(ItemSpacingDecoration(20))
                isNestedScrollingEnabled = false
            }

            textViewDonateInfoMore.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("reviews", donateIdx)
                mainActivity.replaceFragment("ReviewShowFragment", true, bundle)
            }

            textViewDonateInfoTitle.text = donateInfo.donationTitle
            textViewDonateInfoSubTitle.text = donateInfo.donationSubtitle
            textViewDonateInfoCategory.text = donateInfo.donationCategory
            textViewDonateInfoContent.text = donateInfo.donationContent
            textViewDonateInfoReviewScore.text = "0.0"
            textViewDonateInfoReviewNumber.text = "(0)"

            getReviewInstance(donateInfo)

            if (donateInfo.donationImg.isNotEmpty()){
                for (image in donateInfo.donationImg){
                    imgList.add(image)
                }
            }

            viewpager2DonateInfoThumbnail.adapter = DonateInfoFragmentStateAdapter(mainActivity)
            setupTabLayoutMediator()

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
            } else {
                buttonDonateInfoChat.visibility = View.VISIBLE
                buttonDonateInfoDelete.visibility = View.GONE
                buttonDonateInfoModify.visibility = View.GONE

                if (donateInfo.donationType == "도와주세요") {
                    buttonDonateInfoDonation.visibility = View.GONE
                } else {
                    buttonDonateInfoDonation.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getReviewInstance(donateInfo: Donations) {
        var rate = 0.0
        var documentSize = 0
        Firebase.firestore.collection("Reviews").whereEqualTo("donationBoardId", donateInfo.donationIdx).get().addOnSuccessListener { result ->
            for(document in result) {
                val reviewRate = document["reviewRate"] as String
                rate += reviewRate.toDouble()
                documentSize++
            }
            if(documentSize != 0) {
                val averageRate = rate / documentSize.toDouble()
                fragmentDonateInfoBinding.textViewDonateInfoReviewScore.text = "${averageRate}"
                fragmentDonateInfoBinding.textViewDonateInfoReviewNumber.text = "(${documentSize})"
            }
        }
    }
}