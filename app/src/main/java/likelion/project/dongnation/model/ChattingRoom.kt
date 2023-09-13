package likelion.project.dongnation.model

import com.google.firebase.firestore.CollectionReference

data class ChattingRoom (
    val chattingRoomUserId1: String = "",
    val chattingRoomUserId2: String = "",
    val chattingRoomMessagesRef: CollectionReference? = null
)