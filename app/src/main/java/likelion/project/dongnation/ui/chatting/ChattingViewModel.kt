package likelion.project.dongnation.ui.chatting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.runBlocking
import likelion.project.dongnation.model.Message
import likelion.project.dongnation.model.User
import likelion.project.dongnation.repository.ChattingRoomRepository

class ChattingViewModel : ViewModel() {
    private val chattingRoomRepository = ChattingRoomRepository()
    var sendingState = MutableLiveData<Int>()

    fun sendMessage(userId: String, userCounterpartId: String, content: String, date: String){
        val user = User(userId = userId)
        val userCounterpart = User(userId = userCounterpartId)
        val message = Message(userId, content, date)

        runBlocking {
            chattingRoomRepository.sendMessage(user, userCounterpart, message)
            sendingState.value = SEND_MESSAGE_COMPLETE
        }
    }

    companion object {
        const val SEND_MESSAGE_ATTEMPT = 1
        const val SEND_MESSAGE_COMPLETE = 2
        const val SEND_MESSAGE_FAILURE = 3
    }
}