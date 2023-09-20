package likelion.project.dongnation.ui.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import likelion.project.dongnation.databinding.ItemReviewListBinding
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.ui.main.MainActivity

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            ItemReviewListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(private val binding: ItemReviewListBinding) : ViewHolder(binding.root) {
        fun bind(item: Review) {
            with (binding) {
                Glide.with(itemView)
                    .load(item.reviewImg[0])
                    .into(imageViewItemReviewImg)
                textViewItemReviewWriter.text = item.reviewWriter
                textViewItemScore.text = item.reviewRate
                textViewItemReviewContent.text = item.reviewContent
                textViewItemReviewDate.text = MainActivity().formatTimeDifference(item.reviewDate.toDate())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.reviewIdx == newItem.reviewIdx
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }
}