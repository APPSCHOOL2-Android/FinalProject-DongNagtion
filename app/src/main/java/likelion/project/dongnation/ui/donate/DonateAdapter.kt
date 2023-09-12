package likelion.project.dongnation.ui.donate

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import likelion.project.dongnation.databinding.ItemReviewListBinding
import likelion.project.dongnation.model.Review

class DonateAdapter(val reviews: List<Review>) : RecyclerView.Adapter<DonateAdapter.DonateViewHolder>() {
    inner class DonateViewHolder(binding: ItemReviewListBinding) : RecyclerView.ViewHolder(binding.root){
        var itemimg : ImageView
        var itemWriter : TextView
        var itemContent : TextView
        var itemScore : TextView

        init {
            itemimg = binding.imageViewItemImg
            itemWriter = binding.textViewItemWriter
            itemContent = binding.textViewItemContent
            itemScore = binding.textViewItemScore
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonateViewHolder {
        val binding = ItemReviewListBinding.inflate(LayoutInflater.from(parent.context))
        val donateViewHolder = DonateViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return donateViewHolder
    }

    override fun getItemCount(): Int = minOf(reviews.size, 3)

    override fun onBindViewHolder(holder: DonateViewHolder, position: Int) {
        holder.itemWriter.text = reviews[position].reviewWriter
        holder.itemScore.text = reviews[position].reviewRate
        holder.itemContent.text = reviews[position].reviewContent
    }
}