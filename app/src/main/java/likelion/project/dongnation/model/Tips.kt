package likelion.project.dongnation.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Tips(
    var tipIdx : String = "",
    val tipWriterId : String = "",
    val tipWriterName : String = "",
    val tipTitle : String = "",
    val tipContent : String = "",
    val tipDate : Timestamp = Timestamp.now(),
    val tipsImg : List<String> = ArrayList(),
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Timestamp::class.java.classLoader) ?: Timestamp.now(),
        parcel.createStringArrayList() ?: ArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tipIdx)
        parcel.writeString(tipWriterId)
        parcel.writeString(tipWriterName)
        parcel.writeString(tipTitle)
        parcel.writeString(tipContent)
        parcel.writeParcelable(tipDate, flags)
        parcel.writeStringList(tipsImg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tips> {
        override fun createFromParcel(parcel: Parcel): Tips {
            return Tips(parcel)
        }

        override fun newArray(size: Int): Array<Tips?> {
            return arrayOfNulls(size)
        }
    }
}
