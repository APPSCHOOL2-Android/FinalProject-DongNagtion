package likelion.project.dongnation.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentHomeBinding
import likelion.project.dongnation.ui.main.MainActivity

class HomeFragment : Fragment() {

    lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentHomeBinding.run {
            buttonHomeSearch.setOnClickListener {
                val word = editTextHomeSearch.text.toString()
                Snackbar.make(fragmentHomeBinding.root, word, Snackbar.LENGTH_SHORT).show()
            }

            recyclerviewHomeDonationAll.adapter = HomeAdapter(mainActivity)
        }

        return fragmentHomeBinding.root
    }
}