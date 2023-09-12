package likelion.project.dongnation.ui.chatting

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentChattingBinding
import likelion.project.dongnation.databinding.ItemChattingMessageCounterpartBinding
import likelion.project.dongnation.databinding.ItemChattingMessageOneselfBinding
import likelion.project.dongnation.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

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
                    val newTextView2 = makeMessageCounterpart(inflater, editTextChattingMessage.text.toString())
                    constraintLayoutChatting.apply {
                        scrollViewChatting.apply {
                            linearLayoutChatting.addView(newTextView1)
                            linearLayoutChatting.addView(newTextView2)
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
        }
        val message = itemChattingMessageOneselfBinding.root
        message.gravity = Gravity.END
        return message
    }

    // 상대방 채팅 생성
    private fun makeMessageCounterpart(inflater: LayoutInflater, inputMessage: String): LinearLayout {
        val itemChattingMessageCounterpartBinding = ItemChattingMessageCounterpartBinding.inflate(inflater)
        itemChattingMessageCounterpartBinding.run{
            textViewItemChattingMessage.text = inputMessage
        }
        val message = itemChattingMessageCounterpartBinding.root
        message.gravity = Gravity.START
        return message
    }
}