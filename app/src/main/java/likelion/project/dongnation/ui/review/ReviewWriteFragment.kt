package likelion.project.dongnation.ui.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentReviewWriteBinding
import likelion.project.dongnation.ui.main.MainActivity

class ReviewWriteFragment : Fragment() {

    lateinit var fragmentReviewWriteBinding: FragmentReviewWriteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentReviewWriteBinding = FragmentReviewWriteBinding.inflate(inflater)
        mainActivity = activity as MainActivity



        return fragmentReviewWriteBinding.root
    }
}