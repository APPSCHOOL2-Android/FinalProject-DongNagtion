package likelion.project.dongnation.ui.chatting

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import likelion.project.dongnation.model.ChattingRoom
import likelion.project.dongnation.model.Message
import likelion.project.dongnation.model.User
import likelion.project.dongnation.repository.ChattingRoomRepository
import likelion.project.dongnation.repository.UserRepository
import likelion.project.dongnation.ui.login.LoginViewModel

class ChattingViewModel : ViewModel() {
    private val chattingRoomRepository = ChattingRoomRepository()
    private val userRepository = UserRepository()
    var sendingState = MutableLiveData<Int>()
    val chattingRoom = MutableLiveData<ChattingRoom>()
    val chattingRoomUserNameCounterpart = MutableLiveData<String>()

    fun sendMessage(userId: String,
                    userCounterpartId: String,
                    userCounterpartName: String,
                    userCounterpartProfile: String,
                    content: String,
                    date: String)
    = viewModelScope.launch{
        val user = User(userId = userId, userName = LoginViewModel.loginUserInfo.userName, userProfile = LoginViewModel.loginUserInfo.userProfile)
        val userCounterpart = User(userId = userCounterpartId, userName = userCounterpartName, userProfile = userCounterpartProfile)
        val message = Message(userId, content, date)

        val result = async {
            chattingRoomRepository.sendMessage(user, userCounterpart, message)
            SEND_MESSAGE_COMPLETE
        }
        sendingState.value = result.await()
    }

    fun getChattingList(userId: String, userCounterpartId: String)
    = viewModelScope.launch{
        val user = User(userId = userId)
        val userCounterpart = User(userId = userCounterpartId)

        val result = async {
            chattingRoom.value = chattingRoomRepository.getChattingRoom(user, userCounterpart)
            GET_MESSAGE_COMPLETE
        }
        sendingState.value = result.await()
    }

    fun getUser(userId: String){
        viewModelScope.async {
            chattingRoomUserNameCounterpart.value = userRepository.getUserForId(userId)?.userName
        }
    }

    fun notifyNewMessage()
    = viewModelScope.async{
        chattingRoomRepository.notifyNewMessage()
    }

    fun leaveChattingRoom(userId: String, userIdCounterpart: String)
            = viewModelScope.async {
        val user = User(userId = userId)
        val userCounterpart = User(userId = userIdCounterpart)
        chattingRoomRepository.leaveChattingRoom(user, userCounterpart)
    }

    fun blockChattingRoom(userId: String, userCounterpartId: String, block: Boolean)
    = viewModelScope.async{
        val user = User(userId = userId)
        val userCounterpart = User(userId = userCounterpartId)

        val result = async {
            chattingRoomRepository.blockChattingRoom(user, userCounterpart, block)
            SEND_MESSAGE_COMPLETE
        }
        sendingState.value = result.await()
    }

    companion object {
        const val SEND_MESSAGE_NORMAL = 0
        const val SEND_MESSAGE_ATTEMPT = 1
        const val SEND_MESSAGE_COMPLETE = 2
        const val SEND_MESSAGE_FAILURE = 3
        const val GET_MESSAGE_COMPLETE = 4

        var receivingState = MutableLiveData<Boolean>()
    }
}