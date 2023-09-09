package likelion.project.dongnation.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentBoardContentsBinding
import likelion.project.dongnation.ui.main.MainActivity

class BoardContentsFragment : Fragment() {

    lateinit var fragmentBoardContentsBinding: FragmentBoardContentsBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardContentsBinding = FragmentBoardContentsBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentBoardContentsBinding.run {

            materialToolbarBoardContents.run {
                title = "팁 제목"
                setNavigationIcon(R.drawable.ic_back_24dp)
                setNavigationOnClickListener {

                }
            }

            viewPagerBoardContents.run {
                viewPagerBoardContents.adapter = BoardContentsViewPagerAdapter(getImageList())
                viewPagerBoardContents.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                springDotsIndicatorBoardContents.setViewPager2(viewPagerBoardContents)    // indicator 설정
            }

        }

        return fragmentBoardContentsBinding.root
    }

    // 뷰 페이저에 들어갈 아이템
    private fun getImageList(): ArrayList<String> {
        return arrayListOf<String>(R.drawable.ic_back_24dp.toString(), R.drawable.ic_launcher_logo_foreground.toString(), R.drawable.ic_launcher_logo_foreground.toString())
    }

}