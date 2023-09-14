package likelion.project.dongnation.ui.board

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.ItemBoardContentsImageBinding

class BoardContentsViewPagerAdapter(private val imageList: ArrayList<String>) : RecyclerView.Adapter<BoardContentsViewPagerAdapter.BoardContentsViewPagerViewHolder>() {

    inner class BoardContentsViewPagerViewHolder(itemBinding: ItemBoardContentsImageBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        val imageViewBoardContentViewPager : ImageView

        init {
            imageViewBoardContentViewPager = itemBinding.imageViewBoardContentViewPager
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardContentsViewPagerViewHolder {
        val itemBinding = ItemBoardContentsImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return BoardContentsViewPagerViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: BoardContentsViewPagerViewHolder, position: Int) {
        // 이미지 설정하기
        val imageUrl = imageList[position]

        Glide.with(holder.imageViewBoardContentViewPager.context)
            .load(imageUrl)
            .into(holder.imageViewBoardContentViewPager)

    }

}