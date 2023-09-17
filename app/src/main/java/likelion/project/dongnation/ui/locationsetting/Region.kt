package likelion.project.dongnation.ui.locationsetting

import android.content.Context
import likelion.project.dongnation.R


class Region {
    companion object {
        fun getArray(context: Context, position: Int = -1): Array<String> {
            return when (position) {
                Province.SEOUL.number -> context.resources.getStringArray(R.array.array_region_seoul)
                Province.BUSAN.number -> context.resources.getStringArray(R.array.array_region_busan)
                Province.DAEGU.number -> context.resources.getStringArray(R.array.array_region_daegu)
                Province.INCHEON.number -> context.resources.getStringArray(R.array.array_region_incheon)
                Province.GWANGJU.number -> context.resources.getStringArray(R.array.array_region_gwangju)
                Province.DAEJEON.number -> context.resources.getStringArray(R.array.array_region_daejeon)
                Province.ULSAN.number -> context.resources.getStringArray(R.array.array_region_ulsan)
                Province.SEJONG.number -> context.resources.getStringArray(R.array.array_region_sejong)
                Province.GYEONGGI.number -> context.resources.getStringArray(R.array.array_region_gyeonggi)
                Province.GANGWON.number -> context.resources.getStringArray(R.array.array_region_gangwon)
                Province.CHUNG_BUK.number -> context.resources.getStringArray(R.array.array_region_chung_buk)
                Province.CHUNG_NAM.number -> context.resources.getStringArray(R.array.array_region_chung_nam)
                Province.GYEONG_BUK.number -> context.resources.getStringArray(R.array.array_region_gyeong_buk)
                Province.GYEONG_NAM.number -> context.resources.getStringArray(R.array.array_region_gyeong_nam)
                Province.JEON_BUK.number -> context.resources.getStringArray(R.array.array_region_jeon_buk)
                Province.JEON_NAM.number -> context.resources.getStringArray(R.array.array_region_jeon_nam)
                Province.JEJU.number -> context.resources.getStringArray(R.array.array_region_jeju)
                else -> context.resources.getStringArray(R.array.array_region)
            }
        }
    }
}

enum class Province(val krDo: String, val number: Int) {
    SEOUL("서울특별시", 0),
    BUSAN("부산광역시", 1),
    DAEGU("대구광역시", 2),
    INCHEON("인천광역시", 3),
    GWANGJU("광주광역시", 4),
    DAEJEON("대전광역시", 5),
    ULSAN("울산광역시", 6),
    SEJONG("세종특별자치시", 7),
    GYEONGGI("경기도", 8),
    GANGWON("강원도", 9),
    CHUNG_BUK("충청북도", 10),
    CHUNG_NAM("충청남도", 11),
    GYEONG_BUK("경상북도", 12),
    GYEONG_NAM("경상남도", 13),
    JEON_BUK("전라북도", 14),
    JEON_NAM("전라남도", 15),
    JEJU("제주특별자치도", 16);

    companion object {
        fun findKrDo(krDo: String): Province {
            return values().find { it.krDo == krDo } ?: throw IllegalArgumentException()
        }
    }
}
