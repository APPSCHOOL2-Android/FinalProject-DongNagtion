package likelion.project.dongnation.ui.userInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import likelion.project.dongnation.databinding.FragmentUserInfoFollowingBinding
import likelion.project.dongnation.databinding.ItemFollowingListBinding
import likelion.project.dongnation.ui.main.MainActivity

class UserInfoFollowingFragment : Fragment() {

    lateinit var fragmentUserInfoFollowingBinding: FragmentUserInfoFollowingBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserInfoFollowingBinding = FragmentUserInfoFollowingBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentUserInfoFollowingBinding.run {
            recyclerViewFollowing.adapter = UserInfoFollowingAdapter()

            // 바텀 네비게이션 바 안보이게 설정
            // mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE
        }

        return fragmentUserInfoFollowingBinding.root
    }

    inner class UserInfoFollowingAdapter : RecyclerView.Adapter<UserInfoFollowingAdapter.UserInfoFollowingHolder>() {
        inner class UserInfoFollowingHolder(binding : ItemFollowingListBinding) : RecyclerView.ViewHolder(binding.root) {

            val textViewItemCategory : TextView
            val textViewItemUserId : TextView
            val textViewItemExperience : TextView
            val buttonItemDelete : Button

            init {
                textViewItemCategory = binding.textViewItemFollowingCategory
                textViewItemUserId = binding.textViewItemFollowingUserId
                textViewItemExperience = binding.textViewItemFollowingExperience
                buttonItemDelete = binding.buttonItemFollowingDelete

                // 삭제 버튼 클릭 시 이벤트
                buttonItemDelete.setOnClickListener {

                }
            }
        }

        override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): UserInfoFollowingHolder {
            val binding = ItemFollowingListBinding.inflate(LayoutInflater.from(parent.context))
            val userInfoBoardCommentHolder = UserInfoFollowingHolder(binding)

            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return userInfoBoardCommentHolder
        }

        override fun getItemCount(): Int {
            val itemCount = 0

            if (itemCount == 0) {
                fragmentUserInfoFollowingBinding.layoutFollowing.visibility = View.VISIBLE
            }

            return itemCount
        }

        override fun onBindViewHolder(holder: UserInfoFollowingHolder, position: Int) {

        }
    }
}