package likelion.project.dongnation.ui.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import likelion.project.dongnation.databinding.FragmentReviewShowBinding
import likelion.project.dongnation.ui.main.MainActivity

class ReviewShowFragment : Fragment() {

    private lateinit var binding: FragmentReviewShowBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var viewModel: ReviewViewModel
    private val reviewAdapter by lazy {
        ReviewAdapter()
    }
    private lateinit var donationIdx: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewShowBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        arguments?.let { donationIdx = it.getString("reviews").toString() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        initRecyclerView()
        initView()
    }

    private fun initRecyclerView() {
        with(binding.recyclerViewReviewShowList) {
            adapter = reviewAdapter
            addItemDecoration(ItemSpacingDecoration(20))
        }
    }

    private fun observe() {
        viewModel.getReviews(donationIdx)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    reviewAdapter.submitList(it.reviews)
                }
            }
        }
    }

    private fun initView() {
        with(binding) {
            toolbarReviewShow.setNavigationOnClickListener {
                mainActivity.removeFragment("ReviewShowFragment")
            }

            floatingActionButtonReviewShowAdd.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("donationIdx", donationIdx)
                mainActivity.replaceFragment(MainActivity.REVIEW_WRITE_FRAGMENT, true, bundle)
            }
        }
    }
}