package danggai.domain.checkin.entity

data class CheckIn (
    val retcode: String,
    val message: String,
    val data: Data?
) {
    data class Data(
        val code: String
    )
}