package likelion.project.dongnation.ui.review

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import likelion.project.dongnation.databinding.FragmentReviewShowBinding
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.ui.main.MainActivity

class ReviewShowFragment : Fragment() {

    private lateinit var binding: FragmentReviewShowBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var viewModel: ReviewViewModel
    private val reviewAdapter by lazy {
        ReviewAdapter()
    }
    private var donationIdx: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewShowBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        arguments?.let { donationIdx = it.getString("reviews") }

        binding.run {

            toolbarReviewShow.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment("ReviewShowFragment")
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        with(binding.recyclerViewReviewShowList) {
            adapter = reviewAdapter
            addItemDecoration(ItemSpacingDecoration(20))
        }
    }

    private fun observe() {
        donationIdx?.let { viewModel.getReviews(it) }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    reviewAdapter.submitList(it.reviews)
                }
            }
        }
    }
}