package likelion.project.dongnation.repository

import likelion.project.dongnation.db.remote.ChattingRoomDataSource
import likelion.project.dongnation.model.ChattingRoom
import likelion.project.dongnation.model.User

class ChattingRoomRepository {
    private val chattingRoomDataSource = ChattingRoomDataSource()

    suspend fun getAllChattingRooms() = chattingRoomDataSource.getAllChattingRooms()

    suspend fun getChattingRooms(user: User) = chattingRoomDataSource.getChattingRooms(user)

    suspend fun getChattingRoom(user1: User, user2: User) = chattingRoomDataSource.getChattingRoom(user1, user2)

    suspend fun addChattingRoom(chattingRoom: ChattingRoom) = chattingRoomDataSource.addChattingRoom(chattingRoom)

    suspend fun getMessages(user1: User, user2: User) = chattingRoomDataSource.getMessages(user1, user2)
}