package likelion.project.dongnation.model

data class ReverseGeocodingUI(
    val name: String = "",
    val code: Int = 0,
    val areaName: String = "",
)

fun ReverseGeocoding.toReverseGeocodingUI(): ReverseGeocodingUI {
    if (this.status.code != 0) {
        return ReverseGeocodingUI(code = this.status.code)
    }
    val results = this.results[0]
    val status = this.status
    val region = results.region
    val land = results.land
    val areaName = results.run {
        listOf(
            region.area1.name,
            region.area2.name,
            region.area3.name,
            region.area4.name,
            land.number1,
            land.number2,
        ).filter { it != "" }.joinToString(" ")
    }
    return ReverseGeocodingUI(
        name = results.name,
        code = status.code,
        areaName = areaName,
    )
}