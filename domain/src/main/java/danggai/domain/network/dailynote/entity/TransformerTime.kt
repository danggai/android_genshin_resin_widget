package danggai.domain.network.dailynote.entity

data class TransformerTime(
    val Day: Int,
    val Hour: Int,
    val Minute: Int,
    val Second: Int,
    val reached: Boolean
) {
    companion object {
        val EMPTY = TransformerTime (
            0,
            0,
            0,
            0,
            false
        )
    }
}