package likelion.project.dongnation.ui.review

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import likelion.project.dongnation.databinding.FragmentReviewWriteBinding
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.ui.gallery.GalleryImage
import likelion.project.dongnation.ui.main.MainActivity

class ReviewWriteFragment : Fragment() {

    private lateinit var binding: FragmentReviewWriteBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var viewModel: ReviewViewModel
    private val imagesUri = mutableMapOf<Int, String>()
    private lateinit var donationIdx: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewWriteBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        arguments?.let { donationIdx = it.getString("donationIdx").toString() }
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupImageUploadListener()
        viewModel.getDonationsBoardInfo(donationIdx)
        observe()
        initView()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (it.message) {
                        ReviewViewModel.REVIERW_SUCCESS -> {
                            binding.progressbarReviewWrite.visibility = View.GONE
                            mainActivity.removeFragment(MainActivity.REVIEW_WRITE_FRAGMENT)
                            Snackbar.make(requireView(), "리뷰 작성 성공", Snackbar.LENGTH_SHORT).show()
                        }
                        "" -> {
                            binding.progressbarReviewWrite.visibility = View.GONE
                            binding.buttonReviewWriteSave.isEnabled = true
                        }
                        else -> {
                            binding.progressbarReviewWrite.visibility = View.GONE
                            binding.buttonReviewWriteSave.isEnabled = true
                            Snackbar.make(requireView(), "리뷰 작성 실패", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        viewModel.donations.observe(mainActivity) { donations ->
            binding.textViewReviewWriteDonationUser.text = donations.donationUser
            binding.textViewReviewWriteDonationCategory.text = donations.donationCategory
            binding.textViewReviewWriteDonationTitle.text = donations.donationTitle
            if (donations.donationImg.isNotEmpty()) {
                Glide.with(this)
                    .load(donations.donationImg[0])
                    .into(binding.imageViewReviewWriteDonationThumbnail)
            }
        }
    }

    private fun initView() {
        binding.toolbarReviewWrite.setNavigationOnClickListener {
            mainActivity.removeFragment(MainActivity.REVIEW_WRITE_FRAGMENT)
        }

        for (id in binding.groupReviewWrite.referencedIds) {
            view?.let {
                it.findViewById<View>(id).setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.GALLERY_FRAGMENT, true, null)
                }
            }
        }

        binding.buttonReviewWriteSave.setOnClickListener {
            binding.progressbarReviewWrite.visibility = View.VISIBLE
            binding.buttonReviewWriteSave.isEnabled = false
            viewModel.addReview(
                Review(
                    donationBoardId = viewModel.donations.value?.donationIdx ?: "",
                    reviewWriter = binding.textViewReviewWriteDonationUser.text.toString(),
                    reviewContent = binding.editTextReviewWriteDonationContent.text.toString(),
                    reviewRate = "${binding.ratingBarReviewWrite.rating}",
                    reviewImg = imagesUri.values.toList()
                )
            )
        }

        binding.imageViewReviewWriteImage1Delete.setOnClickListener {
            clearImageView(0)
        }

        binding.imageViewReviewWriteImage2Delete.setOnClickListener {
            clearImageView(1)
        }

        binding.imageViewReviewWriteImage3Delete.setOnClickListener {
            clearImageView(2)
        }
    }

    private fun setupImageUploadListener() {
        mainActivity.supportFragmentManager.setFragmentResultListener(
            "images",
            viewLifecycleOwner
        ) { key, bundle ->
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelableArray("imageList", GalleryImage::class.java)
            } else {
                bundle.getParcelableArray("imageList")
            }

            result?.let { images ->
                images.forEachIndexed { index, item ->
                    if (item is GalleryImage) {
                        val uri = item.uri
                        imagesUri[index] = uri.toString()
                        displayImage(index, uri)
                    }
                }
            }
        }
    }

    private fun displayImage(index: Int, uri: Uri) {
        val imageViewId = when (index) {
            0 -> binding.imageViewReviewWriteImage1
            1 -> binding.imageViewReviewWriteImage2
            2 -> binding.imageViewReviewWriteImage3
            else -> return
        }

        Glide.with(requireContext())
            .load(uri)
            .into(imageViewId)
    }

    private fun clearImageView(index: Int) {
        val imageViewId = when (index) {
            0 -> binding.imageViewReviewWriteImage1
            1 -> binding.imageViewReviewWriteImage2
            2 -> binding.imageViewReviewWriteImage3
            else -> return
        }

        imageViewId.setImageDrawable(null)
        imagesUri.remove(index)
    }
}