package likelion.project.dongnation.ui.userInfo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentUserInfoBoardBinding
import likelion.project.dongnation.databinding.ItemUserInfoBoardWriteBinding
import likelion.project.dongnation.model.Tips
import likelion.project.dongnation.ui.board.BoardViewModel
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class UserInfoBoardFragment : Fragment() {

    lateinit var fragmentUserInfoBoardBinding: FragmentUserInfoBoardBinding
    lateinit var mainActivity: MainActivity

    val userId = LoginViewModel.loginUserInfo.userId
    lateinit var viewModel: BoardViewModel

    val myBoardDataList = mutableListOf<Tips>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserInfoBoardBinding = FragmentUserInfoBoardBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(this)[BoardViewModel::class.java]

        viewModel.run {
            boardLiveData.observe(viewLifecycleOwner) { tipsList ->
                myBoardDataList.clear()
                tipsList.forEach{myBoardDataList.add(it)}

                if (myBoardDataList.isNotEmpty()) {
                    fragmentUserInfoBoardBinding.layoutWrite.visibility = View.GONE
                }else {
                    fragmentUserInfoBoardBinding.layoutWrite.visibility = View.VISIBLE
                }

                fragmentUserInfoBoardBinding.recyclerViewUserInfoBoardWrite.adapter?.notifyDataSetChanged()
            }

            loadMyBoard(userId)
        }

        fragmentUserInfoBoardBinding.run {

            mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE

            materialToolbarUserInfoBoard.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.USER_INFO_BOARD_FRAGMENT)
                }
            }

            // 작성글 클릭
            textViewUserInfoBoardWrite.setOnClickListener {
                viewUserInfoBoardWriteLine.setBackgroundColor(Color.BLACK)
                viewUserInfoBoardCommentLine.setBackgroundColor(Color.WHITE)

                layoutComment.visibility = View.GONE

                recyclerViewUserInfoBoardWrite.run {
                    adapter = UserInfoBoardWriteAdapter()
                    layoutManager = LinearLayoutManager(context)
                }

                recyclerViewUserInfoBoardWrite.visibility = View.VISIBLE
                recyclerViewUserInfoBoardComment.visibility = View.GONE

            }

            // 댓글 클릭
            textViewUserInfoBoardComment.setOnClickListener {
                viewUserInfoBoardWriteLine.setBackgroundColor(Color.WHITE)
                viewUserInfoBoardCommentLine.setBackgroundColor(Color.BLACK)

                layoutWrite.visibility = View.GONE

                recyclerViewUserInfoBoardComment.run {
                    adapter = UserInfoBoardCommentAdapter()
                    layoutManager = LinearLayoutManager(context)
                }

                recyclerViewUserInfoBoardWrite.visibility = View.GONE
                recyclerViewUserInfoBoardComment.visibility = View.VISIBLE

            }

            // 처음에는 작성 글 리사이클러뷰만 보이게하기
            recyclerViewUserInfoBoardWrite.run {
                adapter = UserInfoBoardWriteAdapter()
                layoutManager = LinearLayoutManager(context)
            }

        }

        return fragmentUserInfoBoardBinding.root
    }

    // 작성 글 Adapter
    inner class UserInfoBoardWriteAdapter : RecyclerView.Adapter<UserInfoBoardWriteAdapter.UserInfoBoardWriteHolder>() {

        inner class UserInfoBoardWriteHolder(binding : ItemUserInfoBoardWriteBinding) : RecyclerView.ViewHolder(binding.root) {

            val textViewItemBoardWriteTitle : TextView
            val textViewItemBoardWriteContents : TextView
            val textViewItemBoardWriteDate : TextView

            init {
                textViewItemBoardWriteTitle = binding.textViewItemBoardWriteTitle
                textViewItemBoardWriteContents = binding.textViewItemBoardWriteContents
                textViewItemBoardWriteDate = binding.textViewItemBoardWriteDate

                binding.root.setOnClickListener {
                    val position = bindingAdapterPosition
                    val selectedDonation = myBoardDataList[position]

                    var bundle = Bundle()
                    bundle.putParcelable("board", selectedDonation)
                    mainActivity.replaceFragment(MainActivity.BOARD_CONTENTS_FRAGMENT, true, bundle)
                }

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoBoardWriteHolder {
            val binding = ItemUserInfoBoardWriteBinding.inflate(LayoutInflater.from(parent.context))
            val userInfoBoardWriteHolder = UserInfoBoardWriteHolder(binding)

            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return userInfoBoardWriteHolder
        }

        override fun getItemCount(): Int {

            return myBoardDataList.size
        }

        override fun onBindViewHolder(holder: UserInfoBoardWriteHolder, position: Int) {

            val tipTimestamp = myBoardDataList[position].tipDate
            val formattedDate = mainActivity.formatTimeDifference(tipTimestamp.toDate())

            holder.textViewItemBoardWriteTitle.text = myBoardDataList[position].tipTitle
            holder.textViewItemBoardWriteContents.text = myBoardDataList[position].tipContent
            holder.textViewItemBoardWriteDate.text = formattedDate
        }
    }

    // 댓글 Adapter
    inner class UserInfoBoardCommentAdapter : RecyclerView.Adapter<UserInfoBoardCommentAdapter.UserInfoBoardCommentHolder>() {
        inner class UserInfoBoardCommentHolder(binding : ItemUserInfoBoardWriteBinding) : RecyclerView.ViewHolder(binding.root) {

            val textViewItemBoardWriteTitle : TextView
            val textViewItemBoardWriteContents : TextView
            val textViewItemBoardWriteDate : TextView

            init {
                textViewItemBoardWriteTitle = binding.textViewItemBoardWriteTitle
                textViewItemBoardWriteContents = binding.textViewItemBoardWriteContents
                textViewItemBoardWriteDate = binding.textViewItemBoardWriteDate
            }

        }

        override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): UserInfoBoardCommentHolder {
            val binding = ItemUserInfoBoardWriteBinding.inflate(LayoutInflater.from(parent.context))
            val userInfoBoardCommentHolder = UserInfoBoardCommentHolder(binding)

            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return userInfoBoardCommentHolder
        }

        override fun getItemCount(): Int {
            val itemCount = 0

            if (itemCount == 0) {
                fragmentUserInfoBoardBinding.layoutComment.visibility = View.VISIBLE
            } else {
                fragmentUserInfoBoardBinding.layoutComment.visibility = View.GONE
            }

            return itemCount
        }

        override fun onBindViewHolder(holder: UserInfoBoardCommentHolder, position: Int) {
            holder.textViewItemBoardWriteTitle.text = "댓글 제목입니다"
        }
    }

}