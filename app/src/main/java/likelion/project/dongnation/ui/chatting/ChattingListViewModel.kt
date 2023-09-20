package likelion.project.dongnation.ui.chatting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import likelion.project.dongnation.model.ChattingRoom
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

    companion object {
        var receivingState = MutableLiveData<Boolean>()
    }
}