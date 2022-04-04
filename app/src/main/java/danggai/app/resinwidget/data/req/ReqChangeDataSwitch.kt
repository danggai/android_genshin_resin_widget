package danggai.app.resinwidget.data.req

data class ReqChangeDataSwitch (
    val gameId: Int,
    val switchId: Int,
    val isPublic: Boolean,
    val cookie: String,
) {
}