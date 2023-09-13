package likelion.project.dongnation.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.ActivityMainBinding
import likelion.project.dongnation.databinding.FragmentHomeBinding
import likelion.project.dongnation.repository.DonateRepository
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
                setNavigationOnClickListener {
                    mainActivity.replaceFragment("MapFragment", true, null)
                }
            }

            buttonHomeSearch.setOnClickListener {
                searchResult()
            }

            editTextHomeSearch.setOnEditorActionListener { v, actionId, event ->
                searchResult()
                true
            }

            floatingActionButtonHome.setOnClickListener {
                mainActivity.replaceFragment("DonateWriteFragment", true, null)
            }
        }

        return fragmentHomeBinding.root
    }
}