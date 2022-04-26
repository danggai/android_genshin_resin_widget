package danggai.domain.network.dailynote.entity

data class DailyNote (
    val retcode: String,
    val message: String,
    val data: DailyNoteData?
) {
    companion object {
        val EMPTY = DailyNote(
            retcode = "",
            message = "",
            DailyNoteData.EMPTY
        )
    }
}
