package likelion.project.dongnation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import likelion.project.dongnation.databinding.ItemDonationlistBinding
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.ui.main.MainActivity
import kotlin.math.round

class HomeAdapter(val mainActivity: MainActivity, val donates: MutableList<Donations>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    var rate: Double = 0.0
    inner class HomeViewHolder(binding: ItemDonationlistBinding) : RecyclerView.ViewHolder(binding.root){
        var itemThumbnail : ImageView
        var itemTitle : TextView
        var itemSubTitle : TextView
        var itemReview : TextView
        var itemCategory : TextView

        init {
            itemThumbnail = binding.imageViewItemThumbnail
            itemTitle = binding.textViewItemTitle
            itemSubTitle = binding.textViewItemSubTitle
            itemReview = binding.textViewItemReview
            itemCategory = binding.textViewItemCategory

            // 재능 클릭 시 이벤트
            binding.root.setOnClickListener {
                var bundle = Bundle()
                bundle.putString("donationIdx", donates[bindingAdapterPosition].donationIdx)
                mainActivity.replaceFragment(MainActivity.DONATE_INFO_FRAGMENT, true, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemDonationlistBinding.inflate(LayoutInflater.from(parent.context))
        val homeViewHolder = HomeViewHolder(binding)

        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return homeViewHolder
    }

    override fun getItemCount(): Int = donates.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.itemCategory.text = donates[position].donationCategory
        holder.itemTitle.text = donates[position].donationTitle
        holder.itemSubTitle.text = donates[position].donationSubtitle

        rate = getRateAverage(donates[position].donationReview)

        holder.itemReview.text = "$rate (${donates[position].donationReview.size})"
    }

    private fun getRateAverage(reviews : List<Review>) : Double{
        var total = 0.0

        if (reviews.isEmpty()){
            return total
        } else {
            for (review in reviews){
                total += review.reviewRate.toFloat()
            }
        }

        return round((total / reviews.size) * 10) / 10
    }
}