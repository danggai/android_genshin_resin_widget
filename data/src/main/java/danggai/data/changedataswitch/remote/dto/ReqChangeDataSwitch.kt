package danggai.data.changedataswitch.remote.dto

data class ReqChangeDataSwitch (
    val gameId: Int,
    val switchId: Int,
    val isPublic: Boolean,
    val cookie: String,
) {
}