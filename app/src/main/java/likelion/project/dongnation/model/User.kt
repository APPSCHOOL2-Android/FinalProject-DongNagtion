package likelion.project.dongnation.model

data class User (
    val userType: Int = 0,
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userAddressMain: String = "",
    val userAddressDetail: String = ""
)