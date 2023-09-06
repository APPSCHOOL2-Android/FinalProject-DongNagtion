package likelion.project.dongnation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import likelion.project.dongnation.databinding.ItemTalentlistBinding

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    inner class HomeViewHolder(binding: ItemTalentlistBinding) : RecyclerView.ViewHolder(binding.root){
        var itemThumbnail : ImageView
        var itemTitle : TextView
        var itemComment : TextView
        var itemReview : TextView
        var itemCategory : Chip

        init {
            itemThumbnail = binding.imageViewItemThumbnail
            itemTitle = binding.textViewItemTitle
            itemComment = binding.textViewItemComment
            itemReview = binding.textViewItemReview
            itemCategory = binding.chipItemCategory

            // 재능 클릭 시 이벤트
            binding.root.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemTalentlistBinding.inflate(LayoutInflater.from(parent.context))
        val homeViewHolder = HomeViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        return homeViewHolder
    }

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

    }
}