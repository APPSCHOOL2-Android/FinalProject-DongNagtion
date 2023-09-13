package likelion.project.dongnation.model

import com.google.firebase.Timestamp

data class Tips(
    var tipIdx : String = "",
    val tipWriterId : String = "",
    val tipWriterName : String = "",
    val tipTitle : String = "",
    val tipContent : String = "",
    val tipDate : Timestamp = Timestamp.now(),
    val tipsImg : List<String> = ArrayList(),
)
