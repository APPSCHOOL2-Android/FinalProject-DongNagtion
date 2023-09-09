package likelion.project.dongnation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import likelion.project.dongnation.databinding.ItemDonationlistBinding
import likelion.project.dongnation.ui.main.MainActivity

class HomeAdapter(val mainActivity: MainActivity) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
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
                mainActivity.replaceFragment(MainActivity.DONATE_INFO_FRAGMENT, true, null)
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

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

    }
}