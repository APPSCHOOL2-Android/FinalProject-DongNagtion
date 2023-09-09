package likelion.project.dongnation.ui.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.divider.MaterialDividerItemDecoration
import likelion.project.dongnation.databinding.FragmentReviewShowBinding
import likelion.project.dongnation.ui.donate.DonateAdapter
import likelion.project.dongnation.ui.main.MainActivity

class ReviewShowFragment : Fragment() {

    lateinit var fragmentReviewShowBinding: FragmentReviewShowBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentReviewShowBinding = FragmentReviewShowBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentReviewShowBinding.run {
            recyclerViewReviewShowList.run {
                adapter = DonateAdapter()
                addItemDecoration(ItemSpacingDecoration(20))
            }
        }

        return fragmentReviewShowBinding.root
    }
}