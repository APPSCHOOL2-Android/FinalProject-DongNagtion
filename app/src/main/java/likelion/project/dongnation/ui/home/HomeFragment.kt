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
import likelion.project.dongnation.ui.login.LoginViewModel
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
            
            textViewHomeLocation.text = LoginViewModel.loginUserInfo.userAddress

            buttonHomeSearch.setOnClickListener {
                searchResult()
                hideKeyboard(it)
            }

            editTextHomeSearch.setOnEditorActionListener { v, actionId, event ->
                searchResult()
                hideKeyboard(v)
                true
            }

            floatingActionButtonHome.setOnClickListener {
                mainActivity.replaceFragment("DonateWriteFragment", true, null)
            }
        }

        return fragmentHomeBinding.root
    }

    fun searchResult(){
        val word = fragmentHomeBinding.editTextHomeSearch.text.toString()

        if (word == ""){
            Snackbar.make(fragmentHomeBinding.root, "검색어를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
        } else {
            viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

            viewModel.run {
                searchLiveData.observe(viewLifecycleOwner){ search ->
                    fragmentHomeBinding.recyclerviewHomeDonationAll.run {
                        adapter = HomeAdapter(mainActivity, search)
                        adapter!!.notifyDataSetChanged()
                    }
                }

                searchDonate(word)
            }
        }
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}