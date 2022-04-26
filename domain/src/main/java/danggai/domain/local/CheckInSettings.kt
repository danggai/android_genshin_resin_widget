package danggai.domain.local

data class CheckInSettings(
    val genshinCheckInEnable: Boolean,
    val honkai3rdCheckInEnable: Boolean,
    val notiCheckInSuccess: Boolean,
    val notiCheckInFailed: Boolean
) {
    companion object {
        val EMPTY = CheckInSettings (
            genshinCheckInEnable = false,
            honkai3rdCheckInEnable = false,
            notiCheckInSuccess = false,
            notiCheckInFailed = false
        )
    }
}
