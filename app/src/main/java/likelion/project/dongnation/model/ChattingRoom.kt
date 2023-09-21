package likelion.project.dongnation.model


data class ChattingRoom (
    val chattingRoomUserId: String = "",
    val chattingRoomUserIdCounterpart: String = "",
    val chattingRoomUserNameCounterpart: String = "",
    val chattingRoomBlock: Boolean = false,
    var chattingRoomMessages: ArrayList<Message> = ArrayList()
)