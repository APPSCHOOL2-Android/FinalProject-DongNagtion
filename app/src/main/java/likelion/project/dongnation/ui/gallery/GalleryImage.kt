package likelion.project.dongnation.ui.gallery

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GalleryImage(
    var uri: Uri = Uri.EMPTY,
    var name: String = "",
    var bucketId: Long = 0,
    var bucketName: String = "",
    var addedDate: Long = 0
) : Parcelable