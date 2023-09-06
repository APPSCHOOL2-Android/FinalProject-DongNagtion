package likelion.project.dongnation.model

data class User (
    val userType: Int = 0,
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userAddress: String = "",
    val userTransCode: String = "",
    val userFollowingNum: Int = 0,
    val userFollowList: List<String>,
    val userExperience: Int
)