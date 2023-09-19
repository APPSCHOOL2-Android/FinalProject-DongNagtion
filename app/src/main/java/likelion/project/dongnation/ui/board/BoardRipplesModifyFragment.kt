package likelion.project.dongnation.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentBoardRipplesModifyBinding
import likelion.project.dongnation.ui.main.MainActivity

class BoardRipplesModifyFragment : Fragment() {

    lateinit var fragmentBoardRipplesModifyBinding: FragmentBoardRipplesModifyBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardRipplesModifyBinding = FragmentBoardRipplesModifyBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentBoardRipplesModifyBinding.run {

            materialToolbarBoardRipplesModify.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.BOARD_RIPPLES_MODIFY_FRAGMENT)
                }
            }

        }

        return fragmentBoardRipplesModifyBinding.root
    }

}