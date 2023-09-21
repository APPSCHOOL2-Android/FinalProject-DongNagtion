package likelion.project.dongnation.ui.chatting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import likelion.project.dongnation.model.ChattingRoom
import likelion.project.dongnation.model.User
import likelion.project.dongnation.repository.ChattingRoomRepository
import likelion.project.dongnation.ui.login.LoginViewModel

class ChattingListViewModel : ViewModel() {
    private val chattingRoomRepository = ChattingRoomRepository()
    val chattingList = MutableLiveData<ArrayList<ChattingRoom>>()

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

    companion object {
        var receivingState = MutableLiveData<Boolean>()
    }
}