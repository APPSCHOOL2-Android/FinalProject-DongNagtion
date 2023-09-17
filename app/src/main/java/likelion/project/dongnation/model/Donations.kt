package likelion.project.dongnation.model

import com.google.firebase.Timestamp
import android.os.Parcel
import android.os.Parcelable

data class Donations(
    val donationIdx: String = "",
    val donationTitle: String = "",
    val donationSubtitle: String = "",
    val donationType: String = "",
    val donationUser: String = "",
    val donationCategory: String = "",
    val donationContent: String = "",
    var donationImg: MutableList<String> = mutableListOf(),
    val donationTimeStamp: Timestamp = Timestamp.now(),
    val donationReview: List<Review> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        (parcel.createStringArrayList() ?: mutableListOf<String>()).toMutableList(),
        parcel.readParcelable(Timestamp::class.java.classLoader) ?: Timestamp.now(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(donationTitle)
        parcel.writeString(donationSubtitle)
        parcel.writeString(donationType)
        parcel.writeString(donationUser)
        parcel.writeString(donationCategory)
        parcel.writeString(donationContent)
        parcel.writeStringList(donationImg)
        parcel.writeParcelable(donationTimeStamp, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Donations> {
        override fun createFromParcel(parcel: Parcel): Donations {
            return Donations(parcel)
        }

        override fun newArray(size: Int): Array<Donations?> {
            return arrayOfNulls(size)
        }
    }
}

data class Review(
    val reviewWriter: String = "",
    val reviewRate: String = "",
    val reviewDate: Timestamp = Timestamp.now(),
    val reviewContent: String = "",
    val reviewImg: List<String> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Timestamp::class.java.classLoader) ?: Timestamp.now(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reviewWriter)
        parcel.writeString(reviewRate)
        parcel.writeParcelable(reviewDate, flags)
        parcel.writeString(reviewContent)
        parcel.writeStringList(reviewImg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }
}
