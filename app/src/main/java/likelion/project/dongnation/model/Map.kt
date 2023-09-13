package likelion.project.dongnation.model

data class AddressLatLng(
    var latitude: Double,
    var longitude: Double
)

data class MarkerData(
    val type: String,
    val address: String,
    var name: String,
    val title: String,
    val content: String
)