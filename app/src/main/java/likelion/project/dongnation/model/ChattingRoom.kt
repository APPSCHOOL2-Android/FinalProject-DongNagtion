package likelion.project.dongnation.model


data class ChattingRoom (
    val chattingRoomUserId1: String = "",
    val chattingRoomUserId2: String = "",
    val chattingRoomMessages: ArrayList<Message> = ArrayList()
)