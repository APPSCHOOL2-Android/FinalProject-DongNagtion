package likelion.project.dongnation.ui.chatting

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentChattingBinding
import likelion.project.dongnation.databinding.ItemChattingMessageCounterpartBinding
import likelion.project.dongnation.databinding.ItemChattingMessageOneselfBinding
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Date

class ChattingFragment : Fragment() {

    private lateinit var fragmentChattingBinding: FragmentChattingBinding
    private lateinit var chattingViewModel: ChattingViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChattingBinding = FragmentChattingBinding.inflate(inflater)
        chattingViewModel = ViewModelProvider(this)[ChattingViewModel::class.java]
        mainActivity = activity as MainActivity

        setToolbar()
        sendMessage(inflater)
        return fragmentChattingBinding.root
    }

    private fun setToolbar(){
        fragmentChattingBinding.apply {
            toolbarChatting.apply{
                setNavigationIcon(R.drawable.ic_back_24dp)
                setNavigationOnClickListener {
                    mainActivity.removeFragment("ChattingFragment")
                }
                title = "유저 아이디"
            }
        }
    }

    private fun sendMessage(inflater: LayoutInflater){
        fragmentChattingBinding.apply {
            textInputLayoutChattingMessage.apply {
                setEndIconOnClickListener {
                    val newTextView1 = makeMessageOneself(inflater, editTextChattingMessage.text.toString())
                    chattingViewModel.sendingState.observe(viewLifecycleOwner) {
                        when(it){
                            ChattingViewModel.SEND_MESSAGE_COMPLETE -> {
                                constraintLayoutChatting.apply {
                                    scrollViewChatting.apply {
                                        linearLayoutChatting.addView(newTextView1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 유저 채팅 생성
    private fun makeMessageOneself(inflater: LayoutInflater, inputMessage: String): LinearLayout {
        val itemChattingMessageOneselfBinding = ItemChattingMessageOneselfBinding.inflate(inflater)
        itemChattingMessageOneselfBinding.run {
            textViewItemChattingMessage.text = inputMessage
            textViewItemChattingDate.text = getDate()
        }
        chattingViewModel.sendMessage(LoginViewModel.loginUserInfo.userId, "user2Tmp", inputMessage, getDate())
        val message = itemChattingMessageOneselfBinding.root
        message.gravity = Gravity.END
        return message
    }

    // 상대방 채팅 생성
    private fun makeMessageCounterpart(inflater: LayoutInflater, inputMessage: String): LinearLayout {
        val itemChattingMessageCounterpartBinding = ItemChattingMessageCounterpartBinding.inflate(inflater)
        itemChattingMessageCounterpartBinding.run{
            textViewItemChattingMessage.text = inputMessage
            textViewItemChattingDate.text = getDate()
        }
        val message = itemChattingMessageCounterpartBinding.root
        message.gravity = Gravity.START
        return message
    }

    // 현재 날짜 생성 및 반환
    private fun getDate(): String {
        // 현재 날짜와 시간
        val currentDate = Date()
        // 형식 변경
        val chattingDataFormat = SimpleDateFormat("MM.dd")

        return chattingDataFormat.format(currentDate)
    }
}