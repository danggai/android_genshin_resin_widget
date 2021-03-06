package danggai.domain.network.checkin.entity


data class CheckIn (
    val retcode: String,
    val message: String,
    val data: Data?
) {
    data class Data(
        val code: String
    )

    companion object {
        val EMPTY = CheckIn(
            retcode = "",
            message = "",
            Data("")
        )
    }

}