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

enum class Province(val number: Int) {
    SEOUL(0),
    BUSAN(1),
    DAEGU(2),
    INCHEON(3),
    GWANGJU(4),
    DAEJEON(5),
    ULSAN(6),
    SEJONG(7),
    GYEONGGI(8),
    GANGWON(9),
    CHUNG_BUK(10),
    CHUNG_NAM(11),
    GYEONG_BUK(12),
    GYEONG_NAM(13),
    JEON_BUK(14),
    JEON_NAM(15),
    JEJU(16),
}
