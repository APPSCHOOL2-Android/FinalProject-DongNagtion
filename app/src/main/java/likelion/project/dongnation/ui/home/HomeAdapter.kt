package likelion.project.dongnation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.dongnation.databinding.ItemDonationlistBinding
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.ui.donate.DonateInfoFragment
import likelion.project.dongnation.ui.main.MainActivity

class HomeAdapter(val mainActivity: MainActivity, var donates: List<Donations>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private var rate: Double = 0.0
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
        var rate = 0.0
        var documentSize = 0
        Firebase.firestore.collection("Reviews").whereEqualTo("donationBoardId", donates[position].donationIdx).get().addOnSuccessListener { result ->
            for(document in result) {
                val reviewRate = document["reviewRate"] as String
                rate += reviewRate.toDouble()
                documentSize++
            }
            if(documentSize != 0) {
                val averageRate = rate / documentSize.toDouble()
                holder.itemReview.text = "${(averageRate * 10.0).roundToInt() / 10.0} (${documentSize})"
            }
        }

        if (donates[position].donationImg.isNotEmpty()){
            Glide.with(holder.itemThumbnail)
                .load(donates[position].donationImg[0])
                .into(holder.itemThumbnail)

            holder.itemThumbnail.clipToOutline = true
        }
    }

    fun updateData(newItems: List<Donations>) {
        val diffCallback = DiffCallback(donates, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        donates = newItems.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    fun Double.roundToInt(): Int {
        return if (this >= 0) {
            (this + 0.5).toInt()
        } else {
            (this - 0.5).toInt()
        }
    }

}