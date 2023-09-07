package likelion.project.dongnation.ui.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.dongnation.databinding.FragmentReviewShowBinding
import likelion.project.dongnation.ui.donate.DonateAdapter

class ReviewShowFragment : Fragment() {

    lateinit var fragmentReviewShowBinding: FragmentReviewShowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentReviewShowBinding = FragmentReviewShowBinding.inflate(inflater)

        fragmentReviewShowBinding.run {
            recyclerViewReviewShowList.adapter = DonateAdapter()
        }

        return fragmentReviewShowBinding.root
    }
}