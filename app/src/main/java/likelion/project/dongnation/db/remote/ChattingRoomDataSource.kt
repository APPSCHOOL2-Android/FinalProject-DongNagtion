package likelion.project.dongnation.db.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.dongnation.model.ChattingRoom
import likelion.project.dongnation.model.Message
import likelion.project.dongnation.model.User

class ChattingRoomDataSource {
    private val db = Firebase.firestore

    // 전체 채팅방
    suspend fun getAllChattingRooms(): MutableList<ChattingRoom> {
        val querySnapshot = db.collection("chattingRooms").get().await()
        return querySnapshot.toObjects(ChattingRoom::class.java)
    }

    // 유저 본인이 소속된 모든 채팅방
    suspend fun getChattingRooms(user: User): MutableList<ChattingRoom> {
        val querySnapshot = db.collection("chattingRooms")
            .whereEqualTo("chattingRoomUserId", user.userId)
            .get().await()
        return querySnapshot.toObjects(ChattingRoom::class.java)
    }

    // 두 명의 유저가 소속된 채팅방
    suspend fun getChattingRoom(user: User, userCounterpart: User): ChattingRoom {
        val chattingRoomList1 = getChattingRooms(user)
        var chattingRoomResult = ChattingRoom()

        for(chattingRoom in chattingRoomList1){
            if(chattingRoom.chattingRoomUserId == user.userId
                && chattingRoom.chattingRoomUserIdCounterpart == userCounterpart.userId) {
                chattingRoomResult = chattingRoom
                break
            }
        }

        return chattingRoomResult
    }

    suspend fun addChattingRoom(chattingRoom: ChattingRoom) = withContext(Dispatchers.IO){
        db.collection("chattingRooms").add(chattingRoom)
    }

    // 두 명의 유저가 소속된 채팅방의 메시지
    suspend fun getMessages(user: User, userCounterpart: User): ArrayList<Message>{
        return getChattingRoom(user, userCounterpart).chattingRoomMessages
    }

    // 메시지 데이터 베이스 저장
    suspend fun sendMessage(user: User, userCounterpart: User, message: Message) = withContext(Dispatchers.IO){
        // 발송 유저의 데이터 베이스 저장
        val messageList = getMessages(user, userCounterpart)
        messageList.add(message)
        db.collection("chattingRooms")
            .whereEqualTo("userId", user.userId)
            .whereEqualTo("userIdCounterpart", userCounterpart.userId)
            .get()
            .addOnSuccessListener {
                if(it.documents.size != 0){
                    val filePath = it.documents[0].reference.path
                    db.document(filePath).update("chattingRoomMessages", messageList)
                }
                else {
                    val newChattingRoom = ChattingRoom(user.userId, userCounterpart.userId)
                    newChattingRoom.chattingRoomMessages.add(message)
                    db.collection("chattingRoms").add(newChattingRoom)
                }
            }

        // 상대 유저의 데이터 베이스 저장
        val messageListCounterpart = getMessages(userCounterpart, user)
        messageListCounterpart.add(message)
        db.collection("chattingRooms")
            .whereEqualTo("userId", userCounterpart.userId)
            .whereEqualTo("userIdCounterpart", user.userId)
            .get()
            .addOnSuccessListener {
                if(it.documents.size != 0){
                    val filePath = it.documents[0].reference.path
                    db.document(filePath).update("chattingRoomMessages", messageList)
                }
                else {
                    val newChattingRoom = ChattingRoom(userCounterpart.userId, user.userId)
                    newChattingRoom.chattingRoomMessages.add(message)
                    db.collection("chattingRoms").add(newChattingRoom)
                }
            }
    }
}