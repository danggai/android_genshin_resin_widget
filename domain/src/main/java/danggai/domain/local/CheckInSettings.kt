package danggai.domain.local

data class CheckInSettings(
    val notiCheckInSuccess: Boolean,
    val notiCheckInFailed: Boolean
) {
    companion object {
        val EMPTY = CheckInSettings (
            notiCheckInSuccess = true,
            notiCheckInFailed = true
        )
    }
}
