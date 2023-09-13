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

    // 기부 유저로 소속된 모든 채팅방
    private suspend fun getChattingRoomsAsUser1(user: User): MutableList<ChattingRoom> {
        val querySnapshot = db.collection("chattingRooms")
            .whereEqualTo("chattingRoomUserId1", user.userId)
            .get().await()
        return querySnapshot.toObjects(ChattingRoom::class.java)
    }

    // 수혜 유저로 소속된 모든 채팅방
    private suspend fun getChattingRoomsAsUser2(user: User): MutableList<ChattingRoom> {
        val querySnapshot = db.collection("chattingRooms")
            .whereEqualTo("chattingRoomUserId2", user.userId)
            .get().await()
        return querySnapshot.toObjects(ChattingRoom::class.java)
    }

    // 해당 유저가 소속된 모든 채팅방
    suspend fun getChattingRooms(user: User): List<ChattingRoom> {
        val chattingRoomList1 = getChattingRoomsAsUser1(user)
        val chattingRoomList2 = getChattingRoomsAsUser2(user)
        return chattingRoomList1 + chattingRoomList2
    }

    // 두 명의 유저가 소속된 채팅방
    suspend fun getChattingRoom(user1: User, user2: User): ChattingRoom {
        val chattingRoomList1 = getChattingRooms(user1)
        var chattingRoomResult = ChattingRoom()

        for(chattingRoom in chattingRoomList1){
            if(chattingRoom.chattingRoomUserId1 == user1.userId
                && chattingRoom.chattingRoomUserId2 == user2.userId){
                chattingRoomResult = chattingRoom
                break
            } else if(chattingRoom.chattingRoomUserId2 == user1.userId
                && chattingRoom.chattingRoomUserId1 == user2.userId){
                chattingRoomResult = chattingRoom
                break
            }
        }

        return chattingRoomResult
    }

    suspend fun addChattingRoom(chattingRoom: ChattingRoom) = withContext(Dispatchers.IO){
        db.collection("chattingRooms").add(chattingRoom)
    }
}