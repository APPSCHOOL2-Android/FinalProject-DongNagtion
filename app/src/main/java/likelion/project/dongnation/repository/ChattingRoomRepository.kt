package likelion.project.dongnation.repository

import likelion.project.dongnation.db.remote.ChattingRoomDataSource
import likelion.project.dongnation.model.ChattingRoom
import likelion.project.dongnation.model.User

class ChattingRoomRepository {
    private val chattingRoomDataSource = ChattingRoomDataSource()

    suspend fun getAllChattingRooms() = chattingRoomDataSource.getAllChattingRooms()

    suspend fun getChattingRooms(user: User) = chattingRoomDataSource.getChattingRooms(user)

    suspend fun addChattingRoom(chattingRoom: ChattingRoom) = chattingRoomDataSource.addChattingRooms(chattingRoom)
}