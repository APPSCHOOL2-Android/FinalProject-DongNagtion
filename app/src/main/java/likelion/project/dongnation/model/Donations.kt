package likelion.project.dongnation.model

import com.google.firebase.Timestamp

data class Donations (
    var donationIdx: String = "",
    val donationTitle: String = "",
    val donationSubtitle: String = "",
    val donationType: String = "",
    val donationUser: String = "",
    val donationCategory: String = "",
    val donationContent: String = "",
    val donationImg: List<String> = emptyList(),
    val donationTimeStamp: Timestamp = Timestamp.now(),
    val donationReview: List<Review> = emptyList()
)

data class Review (
    val reviewTitle: String = "",
    val reviewRate: String = "",
    val reviewDate: Timestamp = Timestamp.now(),
    val reviewContent: String = "",
    val reviewImg: List<String> = emptyList()
)