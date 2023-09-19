package likelion.project.dongnation.ui.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentHomeBinding
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class HomeFragment : Fragment() {

    lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: HomeViewModel

    private lateinit var adapter: HomeAdapter
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        fragmentHomeBinding.run {

            toolbarHome.run {
                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.item_map -> mainActivity.replaceFragment("MapFragment", true, null)
                    }
                    false
                }
            }

            adapter = HomeAdapter(mainActivity, emptyList()) // 초기에 빈 데이터로 어댑터를 설정
            recyclerviewHomeDonationAll.adapter = adapter

            val db = Firebase.firestore
            db.collection("users").whereEqualTo("userId", LoginViewModel.loginUserInfo.userId).get().addOnSuccessListener { result ->
                for(document in result) {
                    val dbAddress = document["userAddress"] as String
                    textViewHomeLocation.text = dbAddress.split("동").firstOrNull().plus("동")
                }
            }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = fragmentHomeBinding.recyclerviewHomeDonationAll

        // RecyclerView.Adapter.StateRestorationPolicy 설정
        recyclerView?.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        // 데이터 로드
        viewModel.run {
            donatesLiveData.observe(viewLifecycleOwner) { donates ->
                // 기존 어댑터를 업데이트
                adapter.updateData(donates)
            }

            loadDonations()
        }
    }

    private fun searchResult(){
        val word = fragmentHomeBinding.editTextHomeSearch.text.toString()

        if (word == ""){
            Snackbar.make(fragmentHomeBinding.root, "검색어를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
        } else {
            viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

            viewModel.run {
                searchLiveData.observe(viewLifecycleOwner){ search ->
                    fragmentHomeBinding.recyclerviewHomeDonationAll.run {
                        adapter = HomeAdapter(mainActivity, search)
                        adapter?.notifyDataSetChanged()
                    }
                }

                searchDonate(word)
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}