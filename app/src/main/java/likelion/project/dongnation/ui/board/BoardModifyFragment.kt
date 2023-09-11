package likelion.project.dongnation.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentBoardModifyBinding
import likelion.project.dongnation.ui.main.MainActivity

class BoardModifyFragment : Fragment() {

    lateinit var fragmentBoardModifyBinding: FragmentBoardModifyBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardModifyBinding = FragmentBoardModifyBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentBoardModifyBinding.run {

        }

        return fragmentBoardModifyBinding.root
    }

}