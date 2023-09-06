package likelion.project.dongnation.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            recyclerviewHomeTalent.adapter = HomeAdapter()
        }

        return fragmentHomeBinding.root
    }
}