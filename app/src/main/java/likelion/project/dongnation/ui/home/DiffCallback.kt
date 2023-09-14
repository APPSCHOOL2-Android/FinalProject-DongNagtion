package likelion.project.dongnation.ui.home

import androidx.recyclerview.widget.DiffUtil
import likelion.project.dongnation.model.Donations

class DiffCallback(private val oldList: List<Donations>, private val newList: List<Donations>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return oldList[oldItemPosition].donationIdx == newList[newItemPosition].donationIdx
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
