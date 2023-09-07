package likelion.project.dongnation.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import likelion.project.dongnation.databinding.FragmentOnboardingPageBinding

const val ARG_PARM1 = "param1"
const val ARG_PARM2 = "param2"
const val ARG_PARM3 = "param3"

class OnboardingPageFragment : Fragment() {

    lateinit var onboardingPageBinding: FragmentOnboardingPageBinding
    private var descript: Int? = null
    private var lottieResId: Int? = null
    private var page: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        onboardingPageBinding = FragmentOnboardingPageBinding.inflate(inflater)
        arguments?.let {
            descript = it.getInt(ARG_PARM1)
            lottieResId = it.getInt(ARG_PARM2)
            page = it.getInt(ARG_PARM3)
        }
        setOnboarding()
        return onboardingPageBinding.root
    }

    private fun setOnboarding() {
        onboardingPageBinding.run {
            when (page) {
                OnboardingPage.FIRST_PAGE.position -> showFirstPage()
                OnboardingPage.SECOND_PAGE.position -> showSecondPage()
                else -> showThirdPage()
            }
        }
    }

    private fun showFirstPage() {
        with(onboardingPageBinding) {
            lottieOnboarding.apply {
                lottieResId?.let { setAnimation(it) }
                playAnimation()
            }
            descript?.let {
                if (imageViewOnboardingDecription != null) {
                    Glide.with(requireContext())
                        .load(it)
                        .into(imageViewOnboardingDecription)
                }
            }
        }
    }

    private fun showSecondPage() {
        with(onboardingPageBinding) {
            lottieOnboarding.apply {
                lottieResId?.let { setAnimation(it) }
                playAnimation()
            }
            descript?.let {
                if (imageViewOnboardingDecription != null) {
                    Glide.with(requireContext())
                        .load(it)
                        .into(imageViewOnboardingDecription)
                }
            }
        }
    }

    private fun showThirdPage() {
        with(onboardingPageBinding) {
            lottieOnboarding.apply {
                lottieResId?.let { setAnimation(it) }
                playAnimation()
            }
            descript?.let {
                if (imageViewOnboardingDecription != null) {
                    Glide.with(requireContext())
                        .load(it)
                        .into(imageViewOnboardingDecription)
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(descripts: Int, lottieResId: Int, page: Int): Fragment {
            return OnboardingPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARM1, descripts)
                    putInt(ARG_PARM2, lottieResId)
                    putInt(ARG_PARM3, page)
                }
            }
        }
    }
}