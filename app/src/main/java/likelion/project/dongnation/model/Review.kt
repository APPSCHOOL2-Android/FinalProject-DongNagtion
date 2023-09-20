package likelion.project.dongnation.model

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val reviewIdx: String = "",
    val donationBoardId: String = "",
    val reviewWriter: String = "",
    val reviewRate: String = "",
    val reviewDate: Timestamp = Timestamp.now(),
    val reviewContent: String = "",
    val reviewImg: List<String> = emptyList(),
) : Parcelable