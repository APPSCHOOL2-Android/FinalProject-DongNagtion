package likelion.project.dongnation.repository

import likelion.project.dongnation.db.remote.ChattingRoomDataSource
import likelion.project.dongnation.model.ChattingRoom
import likelion.project.dongnation.model.Message
import likelion.project.dongnation.model.User
import likelion.project.dongnation.ui.chatting.ChattingViewModel

class ChattingRoomRepository {
    private val chattingRoomDataSource = ChattingRoomDataSource()

    suspend fun getAllChattingRooms()
    = chattingRoomDataSource.getAllChattingRooms()

    suspend fun getChattingRooms(user: User)
    = chattingRoomDataSource.getChattingRooms(user)

    suspend fun getChattingRoom(user: User, userCounterpart: User)
    = chattingRoomDataSource.getChattingRoom(user, userCounterpart)

    suspend fun addChattingRoom(chattingRoom: ChattingRoom)
    = chattingRoomDataSource.addChattingRoom(chattingRoom)

    suspend fun getMessages(user: User, userCounterpart: User)
    = chattingRoomDataSource.getMessages(user, userCounterpart)

    suspend fun sendMessage(user: User, userCounterpart: User, message: Message)
    = chattingRoomDataSource.sendMessage(user, userCounterpart, message)

    suspend fun notifyNewMessage()
    = chattingRoomDataSource.notifyNewMessage()

    suspend fun leaveChattingRoom(user: User, userCounterpart: User)
    = chattingRoomDataSource.leaveChattingRoom(user, userCounterpart)
}