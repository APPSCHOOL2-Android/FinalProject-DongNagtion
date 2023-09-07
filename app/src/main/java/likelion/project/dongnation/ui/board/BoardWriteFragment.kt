package likelion.project.dongnation.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentBoardWriteBinding
import likelion.project.dongnation.ui.main.MainActivity


class BoardWriteFragment : Fragment() {

    lateinit var fragmentBoadWriteBinding: FragmentBoardWriteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoadWriteBinding = FragmentBoardWriteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentBoadWriteBinding.run {

            materialToolbarBoardWrite.run {

                inflateMenu(R.menu.menu_write)

                setNavigationOnClickListener {

                }

            }
        }

        return fragmentBoadWriteBinding.root
    }

}