package likelion.project.dongnation.ui.review

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.dongnation.databinding.FragmentReviewShowBinding
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.ui.donate.DonateReviewAdapter
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

        val reviews = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("reviews", Review::class.java)!!
        } else {
            arguments?.getParcelableArrayList("reviews")!!
        }

        fragmentReviewShowBinding.run {

            toolbarReviewShow.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment("ReviewShowFragment")
                }
            }

            recyclerViewReviewShowList.run {
                adapter = DonateReviewAdapter(reviews, reviews.size)
                addItemDecoration(ItemSpacingDecoration(20))
            }
        }

        return fragmentReviewShowBinding.root
    }
}