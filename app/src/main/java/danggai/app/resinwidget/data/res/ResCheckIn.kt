package danggai.app.resinwidget.data.res

import danggai.app.resinwidget.data.local.CheckIn

data class ResCheckIn (
    val meta: Meta,
    val data: Data
) {

    data class Data (
        val retcode: String,
        val message: String,
        val data: CheckIn?
    )
}