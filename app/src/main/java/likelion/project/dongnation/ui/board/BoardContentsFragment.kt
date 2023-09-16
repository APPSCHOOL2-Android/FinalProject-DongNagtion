package likelion.project.dongnation.ui.board

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentBoardContentsBinding
import likelion.project.dongnation.databinding.ItemBoardContentsCommentBinding
import likelion.project.dongnation.model.Tips
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class BoardContentsFragment : Fragment() {

    lateinit var fragmentBoardContentsBinding: FragmentBoardContentsBinding
    lateinit var mainActivity: MainActivity

    lateinit var board: Tips
    val userId = LoginViewModel.loginUserInfo.userId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardContentsBinding = FragmentBoardContentsBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        board = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("board", Tips::class.java)!!
        } else {
            arguments?.getParcelable("board")!!
        }

        fragmentBoardContentsBinding.run {
            // 바텀 네비게이션 안 보이게 하기
            mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE

            materialToolbarBoardContents.run {
                title = board.tipTitle
                setNavigationIcon(R.drawable.ic_back_24dp)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.BOARD_CONTENTS_FRAGMENT)
                }
            }

            textViewBoardContentsWriter.text = board.tipWriterName
            textViewBoardContentsDate.text = mainActivity.formatTimeDifference(board.tipDate.toDate())
            textViewBoardContentsContents.text = board.tipContent

            viewPagerBoardContents.run {
                if (getImageList().isNotEmpty()) {
                    viewPagerBoardContents.adapter = BoardContentsViewPagerAdapter(getImageList())
                    viewPagerBoardContents.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    springDotsIndicatorBoardContents.setViewPager2(viewPagerBoardContents)    // indicator 설정
                }else {
                    layoutBoardContentsImg.visibility = View.GONE
                }

            }

            recyclerViewBoardContents.run {
                adapter = BoardContentsAdapter()
                layoutManager = LinearLayoutManager(context)
            }

            if (userId == board.tipWriterId) {
                // 게시글 수정 이미지 클릭
                imageViewBoardContentsEdit.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("board", board)
                    mainActivity.replaceFragment(MainActivity.BOARD_MODIFY_FRAGMENT, true, bundle)
                }

            } else {
                imageViewBoardContentsEdit.visibility = View.GONE
                imageViewBoardContentsDelete.visibility = View.GONE
            }

        }

        return fragmentBoardContentsBinding.root
    }

    // 뷰 페이저에 들어갈 아이템
    private fun getImageList(): ArrayList<String> {

        val imageList = arrayListOf<String>()

        if (board.tipsImg.isNotEmpty()) {
            imageList.addAll(board.tipsImg)
        }

        return imageList
    }

    inner class BoardContentsAdapter : RecyclerView.Adapter<BoardContentsAdapter.BoardContentsHolder>() {

        inner class BoardContentsHolder(binding: ItemBoardContentsCommentBinding) : RecyclerView.ViewHolder(binding.root){
            val imageViewCommentItemProfile : ImageView
            val textViewCommentItemWriter : TextView
            val imageViewCommentItemEdit : ImageView
            val imageViewCommentItemDelete : ImageView
            val textViewCommentItemContents : TextView
            val textViewCommentItemDate : TextView

            init {
                imageViewCommentItemProfile = binding.imageViewCommentItemProfile
                textViewCommentItemWriter = binding.textViewCommentItemWriter
                imageViewCommentItemEdit = binding.imageViewCommentItemEdit
                imageViewCommentItemDelete = binding.imageViewCommentItemDelete
                textViewCommentItemContents = binding.textViewCommentItemContents
                textViewCommentItemDate = binding.textViewCommentItemDate
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardContentsHolder {
            val binding = ItemBoardContentsCommentBinding.inflate(layoutInflater)
            val boardContentsHolder = BoardContentsHolder(binding)

            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return boardContentsHolder
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: BoardContentsHolder, position: Int) {

        }

    }

}