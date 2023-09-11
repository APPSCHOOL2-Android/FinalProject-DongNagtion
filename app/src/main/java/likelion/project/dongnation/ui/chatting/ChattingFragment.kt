package likelion.project.dongnation.ui.chatting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentChattingBinding
import likelion.project.dongnation.ui.main.MainActivity

class ChattingFragment : Fragment() {

    private lateinit var fragmentChattingBinding: FragmentChattingBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChattingBinding = FragmentChattingBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        setToolbar()

        return fragmentChattingBinding.root
    }

    private fun setToolbar(){
        fragmentChattingBinding.apply {
            toolbarChatting.apply{
                this.setNavigationIcon(R.drawable.ic_back_24dp)
                this.setNavigationOnClickListener {
                    mainActivity.removeFragment("ChattingFragment")
                }
                title = "유저 아이디"
            }
        }
    }
}