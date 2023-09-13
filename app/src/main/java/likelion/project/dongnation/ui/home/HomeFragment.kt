package likelion.project.dongnation.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.ActivityMainBinding
import likelion.project.dongnation.databinding.FragmentHomeBinding
import likelion.project.dongnation.ui.main.MainActivity

class HomeFragment : Fragment() {

    lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        viewModel.run {
            donatesLiveData.observe(viewLifecycleOwner){ donates ->
                fragmentHomeBinding.recyclerviewHomeDonationAll.adapter = HomeAdapter(mainActivity, donates)
            }

            loadDonations()
        }

        fragmentHomeBinding.run {

            toolbarHome.run {
                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.item_map -> mainActivity.replaceFragment("MapFragment", true, null)
                    }
                    false
                }
            }

            buttonHomeSearch.setOnClickListener {
                val word = editTextHomeSearch.text.toString()
                Snackbar.make(fragmentHomeBinding.root, word, Snackbar.LENGTH_SHORT).show()
            }

            floatingActionButtonHome.setOnClickListener {
                mainActivity.replaceFragment("DonateWriteFragment", true, null)
            }
        }

        return fragmentHomeBinding.root
    }
}