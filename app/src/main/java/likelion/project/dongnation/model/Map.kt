package likelion.project.dongnation.model

import android.media.Image

data class AddressLatLng(
    var latitude: Double,
    var longitude: Double
)

data class MarkerData(
    val idx: String,
    val type: String,
    val address: String,
    var name: String,
    val title: String,
    val content: String,
    val img: MutableList<String>
)