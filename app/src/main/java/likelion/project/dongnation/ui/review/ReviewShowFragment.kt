package likelion.project.dongnation.ui.review

import android.os.Build
import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewShowBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        val reviews = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList("reviews", Review::class.java)!!
        } else {
            arguments?.getParcelableArrayList("reviews")!!
        }

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
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    reviewAdapter.submitList(it.reviews)
                }
            }
        }
    }
}