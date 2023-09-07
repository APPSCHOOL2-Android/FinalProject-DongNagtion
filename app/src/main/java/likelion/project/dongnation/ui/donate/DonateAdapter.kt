package likelion.project.dongnation.ui.donate

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import likelion.project.dongnation.databinding.ItemReviewListBinding

class DonateAdapter : RecyclerView.Adapter<DonateAdapter.DonateViewHolder>() {
    inner class DonateViewHolder(binding: ItemReviewListBinding) : RecyclerView.ViewHolder(binding.root){
        var itemimg : ImageView
        var itemTitle : TextView
        var itemContent : TextView
        var itemScore : TextView

        init {
            itemimg = binding.imageViewItemImg
            itemTitle = binding.textViewItemTitle
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

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: DonateViewHolder, position: Int) {

    }
}