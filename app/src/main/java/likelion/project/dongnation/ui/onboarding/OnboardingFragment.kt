package likelion.project.dongnation.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentOnboardingBinding
import likelion.project.dongnation.ui.main.MainActivity

class OnboardingFragment : Fragment() {

    lateinit var fragmentOnboardBinding: FragmentOnboardingBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: OnboardingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentOnboardBinding = FragmentOnboardingBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, OnboardingViewModelFactory(mainActivity))[OnboardingViewModel::class.java]
        initViewPager()
        initEvent()
        return fragmentOnboardBinding.root
    }

    private fun initEvent() {
        setViewPagePosition()
        setButtonTextToPageTransition()
    }

    private fun setButtonTextToPageTransition() {
        fragmentOnboardBinding.run {
            viewpagerOnboarding.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        OnboardingPage.THIRD_PAGE.position -> buttonOnboardingNext.text =
                            getString(R.string.start)
                        else -> buttonOnboardingNext.text = getString(R.string.next)
                    }
                }
            })
        }
    }

    private fun setViewPagePosition() {
        fragmentOnboardBinding.run {
            buttonOnboardingNext.setOnClickListener {
                viewpagerOnboarding.run {
                    if (currentItem == END_PAGE) {
                        viewModel.writeFirstVisitor()
                        mainActivity.replaceFragment(MainActivity.LOGIN_FRAGMENT, false, null)
                    } else {
                        currentItem += 1
                    }
                }
            }
        }
    }

    private fun initViewPager() {
        fragmentOnboardBinding.run {
            viewpagerOnboarding.adapter = ScreenSlidePagerAdapter(this@OnboardingFragment)
            viewpagerOnboarding.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewpagerOnboarding.isUserInputEnabled = false

            TabLayoutMediator(
                tabLayoutOnboardingDot,
                viewpagerOnboarding
            ) { tab, position -> }.attach()
        }
    }

    private inner class ScreenSlidePagerAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = PAGES_NUMBER
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                OnboardingPage.FIRST_PAGE.position -> {
                    OnboardingPageFragment.newInstance(
                        descripts = R.drawable.img_onboarding_decription1,
                        lottieResId = R.raw.raw_profile,
                        page = OnboardingPage.FIRST_PAGE.position
                    )
                }

                OnboardingPage.SECOND_PAGE.position -> {
                    OnboardingPageFragment.newInstance(
                        descripts = R.drawable.img_onboarding_decription2,
                        lottieResId = R.raw.raw_talent,
                        page = OnboardingPage.SECOND_PAGE.position
                    )
                }

                else -> {
                    OnboardingPageFragment.newInstance(
                        descripts = R.drawable.img_onboarding_decription3,
                        lottieResId = R.raw.raw_share,
                        page = OnboardingPage.THIRD_PAGE.position
                    )
                }
            }
        }
    }

    companion object {
        private const val PAGES_NUMBER = 3
        private const val END_PAGE = 2
    }
}

enum class OnboardingPage(val position: Int) {
    FIRST_PAGE(0),
    SECOND_PAGE(1),
    THIRD_PAGE(2),
}