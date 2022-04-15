package danggai.data.network.checkin.remote.dto

data class ReqCheckIn (
    val region: String,
    val actId: String,
    val cookie: String,
    val ds: String
) {
}