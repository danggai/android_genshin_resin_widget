package danggai.app.resinwidget.data.res

data class ResChangeDataSwitch (
    val meta: Meta,
    val data: Data
) {

    data class Data (
        val retcode: String,
        val message: String
    )
}