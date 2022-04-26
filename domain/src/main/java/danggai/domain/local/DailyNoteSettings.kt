package danggai.domain.local

import danggai.domain.util.Constant

data class DailyNoteSettings(
    val server: Int,
    val autoRefreshPeriod: Long,
    val notiEach40Resin: Boolean,
    val noti140Resin: Boolean,
    val notiCustomResin: Boolean,
    val customResin: Int,
    val notiExpedition: Boolean,
    val notiHomeCoin: Boolean
) {
    companion object {
        val EMPTY = DailyNoteSettings (
            server = 0,
            autoRefreshPeriod = Constant.PREF_DEFAULT_REFRESH_PERIOD,
            notiEach40Resin = false,
            noti140Resin = false,
            notiCustomResin = false,
            customResin = 0,
            notiExpedition = false,
            notiHomeCoin = false
        )
    }
}
