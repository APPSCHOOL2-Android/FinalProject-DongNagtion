package likelion.project.dongnation.ui.chatting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.runBlocking
import likelion.project.dongnation.model.ChattingRoom
import likelion.project.dongnation.repository.ChattingRoomRepository
import likelion.project.dongnation.ui.login.LoginViewModel

class ChattingListViewModel : ViewModel() {
    val chattingRoomRepository = ChattingRoomRepository()
    val chattingList = MutableLiveData<ArrayList<ChattingRoom>>()

    fun getChattingList(){
        runBlocking {
            chattingList.value = ArrayList(chattingRoomRepository.getChattingRooms(LoginViewModel.loginUserInfo.copy()))
        }
    }
}