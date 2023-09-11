package likelion.project.dongnation.ui.donate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentDonateWriteBinding
import likelion.project.dongnation.ui.main.MainActivity

class DonateWriteFragment : Fragment() {

    lateinit var fragmentDonateWriteBinding: FragmentDonateWriteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDonateWriteBinding = FragmentDonateWriteBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentDonateWriteBinding.run {
            spinnerDonateWriteCategory.run {
                val category = resources.getStringArray(R.array.array_donate_category)

                adapter = SpinnerAdapter(mainActivity, R.layout.item_spinner, category)
            }
        }

        return fragmentDonateWriteBinding.root
    }
}