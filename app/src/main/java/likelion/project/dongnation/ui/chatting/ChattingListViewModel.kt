package likelion.project.dongnation.ui.chatting

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import likelion.project.dongnation.model.ChattingRoom
import likelion.project.dongnation.model.User
import likelion.project.dongnation.repository.ChattingRoomRepository
import likelion.project.dongnation.repository.UserRepository
import likelion.project.dongnation.ui.login.LoginViewModel

class ChattingListViewModel : ViewModel() {
    private val chattingRoomRepository = ChattingRoomRepository()
    private val userRepository = UserRepository()
    val chattingList = MutableLiveData<ArrayList<ChattingRoom>>()
    var chattingRoomUserCounterpartList = MutableLiveData<ArrayList<User>>()

    init {
        chattingList.value = ArrayList()
        chattingRoomUserCounterpartList.value = ArrayList()
    }

    fun getChattingList(){
        viewModelScope.async {
            chattingList.value = ArrayList(chattingRoomRepository.getChattingRooms(LoginViewModel.loginUserInfo.copy()))
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

    fun getUserList(chattingList: MutableLiveData<ArrayList<ChattingRoom>>){
        viewModelScope.async {
            val userList = ArrayList<User>()
            for(chattingRoom in chattingList.value!!){
                val user = async {
                    userRepository.getUser(User(userId = chattingRoom.chattingRoomUserIdCounterpart))[0]
                }
                userList.add(user.await())
            }
            chattingRoomUserCounterpartList.value = userList
        }
    }

    companion object {
        var receivingState = MutableLiveData<Boolean>()
    }
}