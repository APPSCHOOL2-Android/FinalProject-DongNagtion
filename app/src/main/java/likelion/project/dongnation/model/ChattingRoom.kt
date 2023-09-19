package likelion.project.dongnation.model


data class ChattingRoom (
    val chattingRoomUserId: String = "",
    val chattingRoomUserIdCounterpart: String = "",
    var chattingRoomMessages: ArrayList<Message> = ArrayList()
)