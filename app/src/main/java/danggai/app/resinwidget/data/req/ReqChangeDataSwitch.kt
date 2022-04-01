package danggai.app.resinwidget.data.req

import danggai.app.resinwidget.data.local.GameRecordCard

data class ReqChangeDataSwitch (
    val gameId: Int,
    val switchId: Int,
    val isPublic: Boolean,
    val cookie: String,
) {
}