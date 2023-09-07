package likelion.project.dongnation.ui.locationsetting

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class LocationSettingPagerAdapter(
    fragment: Fragment,
    var fragments: MutableList<Fragment>,
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    fun refreshFragment(index: Int, fragment: Fragment) {
        fragments[index] = fragment
        notifyItemChanged(index)
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}