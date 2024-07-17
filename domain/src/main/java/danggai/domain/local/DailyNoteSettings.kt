package danggai.domain.local

import danggai.domain.util.Constant
import java.util.*

data class DailyNoteSettings(
    val autoRefreshPeriod: Long,

    val notiEach40Resin: Boolean,
    val noti140Resin: Boolean, // 180으로 변경하여 적용함
    val notiCustomResin: Boolean,
    val customResin: Int,
    val notiExpedition: Boolean,
    val notiHomeCoin: Boolean,
    val notiParamTrans: Boolean,
    val notiDailyYet: Boolean,
    val notiDailyYetTime: Int,
    val notiWeeklyYet: Boolean,
    val notiWeeklyYetDay: Int,
    val notiWeeklyYetTime: Int,

    val notiEach40TrailPower: Boolean,
    val noti170TrailPower: Boolean, // 230으로 변경하여 적용함
    val notiCustomTrailPower: Boolean,
    val customTrailPower: Int,
    val notiExpeditionHonkaiSr: Boolean,

    val notiEach40Battery: Boolean,
    val notiEach60Battery: Boolean,
    val noti230Battery: Boolean,
    val notiCustomBattery: Boolean,
    val customBattery: Int,
) {
    companion object {
        val EMPTY = DailyNoteSettings (
            autoRefreshPeriod = Constant.PREF_DEFAULT_REFRESH_PERIOD,
            notiEach40Resin = false,
            noti140Resin = false,
            notiCustomResin = false,
            customResin = 0,
            notiExpedition = false,
            notiHomeCoin = false,
            notiParamTrans = false,
            notiDailyYet = false,
            notiDailyYetTime = 21,
            notiWeeklyYet = false,
            notiWeeklyYetDay = Calendar.SUNDAY,
            notiWeeklyYetTime = 21,

            notiEach40TrailPower = false,
            noti170TrailPower = false,
            notiCustomTrailPower = false,
            customTrailPower = 0,
            notiExpeditionHonkaiSr = false,

            notiEach40Battery = false,
            notiEach60Battery = false,
            noti230Battery = false,
            notiCustomBattery = false,
            customBattery = 0,
        )
    }
}
