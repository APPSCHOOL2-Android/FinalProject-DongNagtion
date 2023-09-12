package likelion.project.dongnation.ui.donate

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentDonateInfoBinding
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.ui.main.MainActivity
import likelion.project.dongnation.ui.review.ItemSpacingDecoration

class DonateInfoFragment : Fragment() {

    lateinit var fragmentDonateInfoBinding: FragmentDonateInfoBinding
    lateinit var mainActivity: MainActivity

    val imgList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDonateInfoBinding = FragmentDonateInfoBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        val donate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("donation", Donations::class.java)!!
        } else {
            arguments?.getParcelable("donation")!!
        }
        val rate = arguments?.getDouble("rate")!!

        fragmentDonateInfoBinding.run {
            imgList.add(R.drawable.ic_launcher_logo_foreground.toString())
            imgList.add(R.drawable.ic_launcher_logo_foreground.toString())
            imgList.add(R.drawable.ic_launcher_logo_foreground.toString())

            buttonDonateInfoBack.setOnClickListener {
                mainActivity.removeFragment("DonateInfoFragment")
            }

            viewpager2DonateInfoThumbnail.adapter = DonateInfoFragmentStateAdapter(mainActivity)
            setupTabLayoutMediator()

            textViewDonateInfoTitle.text = donate.donationTitle
            textViewDonateInfoSubTitle.text = donate.donationSubtitle
            textViewDonateInfoCategory.text = donate.donationCategory
            textViewDonateInfoReviewScore.text = rate.toString()
            textViewDonateInfoContent.text = donate.donationContent
            textViewDonateInfoReviewNumber.text = donate.donationReview.size.toString()

            recyclerViewDonateInfoReview.run {
                adapter = DonateAdapter(donate.donationReview)
                addItemDecoration(ItemSpacingDecoration(20))
                isNestedScrollingEnabled = false
            }

            textViewDonateInfoMore.setOnClickListener {
                mainActivity.replaceFragment("ReviewShowFragment", true, null)
            }

            buttonDonateInfoChatOrModify.run{
                setOnClickListener {
                    mainActivity.replaceFragment("ChattingFragment", true, null)
                }
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

    fun setupTabLayoutMediator(){
        TabLayoutMediator(
            fragmentDonateInfoBinding.tabLayoutDonateInfoDot,
            fragmentDonateInfoBinding.viewpager2DonateInfoThumbnail
        ) {tab, position -> }.attach()
    }
}