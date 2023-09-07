package likelion.project.dongnation.ui.donate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentDonateInfoViewPagerBinding

class DonateInfoViewPagerFragment(val image : String) : Fragment() {

    lateinit var fragmentDonateInfoViewPagerBinding: FragmentDonateInfoViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDonateInfoViewPagerBinding = FragmentDonateInfoViewPagerBinding.inflate(inflater)

        return fragmentDonateInfoViewPagerBinding.root
    }
}