package likelion.project.dongnation.ui.chatting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentChattingBinding
import likelion.project.dongnation.databinding.ItemChattingMessageCounterpartRowBinding
import likelion.project.dongnation.databinding.ItemChattingMessageOneselfRowBinding
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Date

class ChattingFragment : Fragment() {

    private lateinit var fragmentChattingBinding: FragmentChattingBinding
    private lateinit var chattingViewModel: ChattingViewModel
    private lateinit var mainActivity: MainActivity

    private lateinit var chattingRoomUserIdCounterpart: String
    private lateinit var chattingRoomUserNameCounterpart: String
    private lateinit var chattingRoomUserProfileCounterpart: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChattingBinding = FragmentChattingBinding.inflate(inflater)
        chattingViewModel = ViewModelProvider(this)[ChattingViewModel::class.java]
        mainActivity = activity as MainActivity

        chattingRoomUserIdCounterpart =
            arguments?.getString("chattingRoomUserIdCounterpart", "").toString()
        chattingRoomUserNameCounterpart =
            arguments?.getString("chattingRoomUserNameCounterpart", "").toString()
        chattingRoomUserProfileCounterpart =
            arguments?.getString("chattingRoomUserProfileCounterpart", "").toString()

        initData()
        initUI()
        observe()

        return fragmentChattingBinding.root
    }

    private fun initData(){
        chattingViewModel.run{
            getChattingList(LoginViewModel.loginUserInfo.copy().userId, chattingRoomUserIdCounterpart)
            notifyNewMessage()
            notifyUserChange()
        }
    }

    private fun initUI(){
        fragmentChattingBinding.run {
            toolbarChatting.run{
                setNavigationIcon(R.drawable.ic_back_24dp)
                setNavigationOnClickListener {
                    mainActivity.removeFragment("ChattingFragment")
                }
                inflateMenu(R.menu.menu_chatting)
                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_item_chatting_leave -> {
                            chattingViewModel.leaveChattingRoom(LoginViewModel.loginUserInfo.userId, chattingRoomUserIdCounterpart)
                            mainActivity.removeFragment("ChattingFragment")
                        }
                        R.id.menu_item_chatting_block -> {
                            if(chattingViewModel.chattingRoom.value?.chattingRoomBlock == true){
                                chattingViewModel.blockChattingRoom(LoginViewModel.loginUserInfo.userId, chattingRoomUserIdCounterpart, false)
                                it.title = "차단하기"
                            } else {
                                chattingViewModel.blockChattingRoom(LoginViewModel.loginUserInfo.userId, chattingRoomUserIdCounterpart, true)
                                it.title = "차단해제하기"
                            }
                        }
                    }
                    false
                }
                chattingViewModel.getUser(chattingRoomUserIdCounterpart)
            }

            recyclerViewChatting.run{
                adapter = RecyclerAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
                ChattingViewModel.receivingState.value = false
            }

            textInputLayoutChattingMessage.setEndIconOnClickListener {
                sendMessage(editTextChattingMessage.text.toString())
            }
        }
    }

    private fun observe(){
        chattingViewModel.chattingRoom.observe(viewLifecycleOwner){
            if(it.chattingRoomBlock){
                fragmentChattingBinding.editTextChattingMessage.isEnabled = false
                fragmentChattingBinding.editTextChattingMessage.setText("대화가 불가능한 상태입니다")
            }
            else{
                fragmentChattingBinding.editTextChattingMessage.isEnabled = true
                fragmentChattingBinding.editTextChattingMessage.setText("")
            }
        }
        chattingViewModel.sendingState.observe(viewLifecycleOwner){
            when(it){
                ChattingViewModel.SEND_MESSAGE_COMPLETE -> {
                    chattingViewModel.getChattingList(LoginViewModel.loginUserInfo.copy().userId, chattingRoomUserIdCounterpart)
                }
                ChattingViewModel.GET_MESSAGE_COMPLETE -> {
                    fragmentChattingBinding.recyclerViewChatting.run {
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
        chattingViewModel.chattingRoomUserNameCounterpart.observe(viewLifecycleOwner){
            fragmentChattingBinding.toolbarChatting.title = chattingViewModel.chattingRoomUserNameCounterpart.value
        }
        ChattingViewModel.receivingState.observe(viewLifecycleOwner){
            if(it){
                chattingViewModel.getChattingList(LoginViewModel.loginUserInfo.copy().userId, chattingRoomUserIdCounterpart)
                ChattingViewModel.receivingState.value = false
            }
        }
        ChattingViewModel.userChangeState.observe(viewLifecycleOwner){
            if(it){
                chattingViewModel.updateChattingRoomProfile(chattingRoomUserIdCounterpart)
                ChattingViewModel.userChangeState.value = false
            }
        }
    }

    // 유저 채팅 생성
    private fun sendMessage(inputMessage: String){
        if(!chattingViewModel.chattingRoom.value?.chattingRoomBlock!!){
            chattingViewModel.sendMessage(LoginViewModel.loginUserInfo.userId, chattingRoomUserIdCounterpart, chattingRoomUserNameCounterpart,chattingRoomUserProfileCounterpart, inputMessage, getDate())
            fragmentChattingBinding.editTextChattingMessage.run {
                setText("")
            }
        }
    }

    // 현재 날짜 생성 및 반환
    private fun getDate(): String {
        // 현재 날짜와 시간
        val currentDate = Date()
        // 형식 변경
        val chattingDataFormat = SimpleDateFormat("MM.dd")

        return chattingDataFormat.format(currentDate)
    }

    inner class RecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        // 유저 자신 채팅 ViewHolder
        inner class ViewHolderOneself(itemChattingMessageOneselfRowBinding: ItemChattingMessageOneselfRowBinding)
            : RecyclerView.ViewHolder(itemChattingMessageOneselfRowBinding.root) {

                var textViewMessage: TextView
                var textViewDate: TextView

                init {
                    textViewMessage = itemChattingMessageOneselfRowBinding.textViewItemChattingMessage
                    textViewDate = itemChattingMessageOneselfRowBinding.textViewItemChattingDate
                }
            }

        // 상대방 채팅 ViewHolder
        inner class ViewHolderCounterpart(itemChattingMessageCounterpartRowBinding: ItemChattingMessageCounterpartRowBinding)
            : RecyclerView.ViewHolder(itemChattingMessageCounterpartRowBinding.root) {

                var textViewMessage: TextView
                var textViewDate: TextView
                var imageViewProfile: ImageView

                init {
                    textViewMessage = itemChattingMessageCounterpartRowBinding.textViewItemChattingMessage
                    textViewDate = itemChattingMessageCounterpartRowBinding.textViewItemChattingDate
                    imageViewProfile = itemChattingMessageCounterpartRowBinding.imageViewItemChattingProfile
                }
            }

        override fun getItemViewType(position: Int): Int {
            return if(chattingViewModel.chattingRoom.value!!.chattingRoomMessages[position].messageUserId == LoginViewModel.loginUserInfo.userId){
                MESSAGE_ONESELF
            } else{
                MESSAGE_COUNTERPART
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemChattingMessageOneselfRowBinding = ItemChattingMessageOneselfRowBinding.inflate(layoutInflater)
            val itemChattingMessageCounterpartRowBinding = ItemChattingMessageCounterpartRowBinding.inflate(layoutInflater)

            val viewHolderOneself = ViewHolderOneself(itemChattingMessageOneselfRowBinding)
            val viewHolderCounterpart = ViewHolderCounterpart(itemChattingMessageCounterpartRowBinding)

            // 가로 세로 길이 설정
            val params = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            itemChattingMessageOneselfRowBinding.root.layoutParams = params
            itemChattingMessageCounterpartRowBinding.root.layoutParams = params

            return if(viewType == MESSAGE_ONESELF){
                viewHolderOneself
            } else{
                viewHolderCounterpart
            }
        }

        override fun getItemCount(): Int {
            return chattingViewModel.chattingRoom.value!!.chattingRoomMessages.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when(holder){
                is ViewHolderOneself -> {
                    holder.textViewMessage.text = chattingViewModel.chattingRoom.value!!.chattingRoomMessages[position].messageContent
                    holder.textViewDate.text = chattingViewModel.chattingRoom.value!!.chattingRoomMessages[position].messageDate
                }
                is ViewHolderCounterpart -> {
                    holder.textViewMessage.text = chattingViewModel.chattingRoom.value!!.chattingRoomMessages[position].messageContent
                    holder.textViewDate.text = chattingViewModel.chattingRoom.value!!.chattingRoomMessages[position].messageDate
                    Glide
                        .with(holder.imageViewProfile)
                        .load(chattingViewModel.chattingRoom.value!!.chattingRoomUserProfileCounterpart.toUri())
                        .circleCrop()
                        .into(holder.imageViewProfile)
                }
            }
        }
    }

    companion object {
        const val MESSAGE_ONESELF = 1
        const val MESSAGE_COUNTERPART = 2
    }
}