package likelion.project.dongnation.ui.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentHomeBinding
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: HomeViewModel

    private lateinit var adapter: HomeAdapter
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        binding.run {
            mainActivity.activityMainBinding.bottomNavigation.visibility = View.VISIBLE
            toolbarHome.run {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_map -> mainActivity.replaceFragment("MapFragment", true, null)
                    }
                    false
                }
            }

            adapter = HomeAdapter(mainActivity, emptyList()) // 초기에 빈 데이터로 어댑터를 설정
            recyclerviewHomeDonationAll.adapter = adapter

            val db = Firebase.firestore
            db.collection("users").whereEqualTo("userId", LoginViewModel.loginUserInfo.userId).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val dbAddress = document["userAddress"] as String
                        textViewHomeLocation.text = extractLocation(dbAddress)
                    }
                }

            buttonHomeSearch.setOnClickListener {
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
        searchResult()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerviewHomeDonationAll

        // RecyclerView.Adapter.StateRestorationPolicy 설정
        recyclerView?.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        viewModel.loadDonations()
        observe()
    }

    private fun observe() {
        if (viewModel.uiState.value.isInitDanationList) binding.shimmerFrameLayoutHome.visibility =
            View.GONE else binding.shimmerFrameLayoutHome.visibility = View.VISIBLE
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect {
                    delay(500)
                    if (it.isInitDanationList) {
                        binding.shimmerFrameLayoutHome.visibility = View.GONE
                        binding.recyclerviewHomeDonationAll.visibility =
                            View.VISIBLE
                        binding.recyclerviewHomeDonationAll.run {
                            adapter = HomeAdapter(mainActivity, it.donationList)
                        }
                    }
                }
            }
        }
    }

    private fun searchResult() {
        binding.editTextHomeSearch.addTextChangedListener {
            it?.let { text ->
                if (it.isEmpty()) {
                    viewModel.loadDonations()
                } else {
                    viewModel.searchDonate(text.toString())
                }
            }
        }
    }


    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun extractLocation(address: String): String {
        val addressParts = address.split(" ")
        val dongIndex = addressParts.indexOfFirst { it.endsWith("동") || it.endsWith("읍") || it.endsWith("면") }
        val dongOrEupOrMyeon = if (dongIndex >= 0) addressParts.subList(2, dongIndex + 1).joinToString(" ") else ""
        return "${addressParts[0]} ${addressParts[1]} $dongOrEupOrMyeon".trim()
    }
}