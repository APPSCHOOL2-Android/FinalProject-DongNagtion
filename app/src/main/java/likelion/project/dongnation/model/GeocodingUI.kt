package likelion.project.dongnation.model

data class GeocodingUI(
    val roadAddress: String = "",
    val jibunAddress: String = "",
    val englishAddress: String = "",
    val addressElements: Array<Region> = emptyArray(),
    val x: String = "",
    val y: String = "",
    val coords: String = "",
    val distance: Double = 0.0,
)

fun Geocoding.toGeocodingUI(): GeocodingUI {
    val addresses = this.addresses ?: emptyArray()
    if (addresses.isEmpty()) {
        return GeocodingUI()
    }
    val address = addresses[0]
    val coords = "${address.y},${address.x}"
    return GeocodingUI(
        roadAddress = address.roadAddress,
        jibunAddress = address.jibunAddress,
        englishAddress = address.englishAddress,
        addressElements = address.addressElements,
        x = address.x,
        y = address.y,
        coords = coords,
        distance = address.distance,
    )
}