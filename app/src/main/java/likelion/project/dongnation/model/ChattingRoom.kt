package likelion.project.dongnation.model


data class ChattingRoom (
    val chattingRoomUserId: String = "",
    val chattingRoomUserIdCounterpart: String = "",
    val chattingRoomMessages: ArrayList<Message> = ArrayList()
)