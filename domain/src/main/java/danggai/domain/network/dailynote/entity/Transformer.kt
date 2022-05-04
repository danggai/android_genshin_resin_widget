package danggai.domain.network.dailynote.entity

data class Transformer (
    val obtained: Boolean,                  // 해금여부
    val recovery_time: TransformerTime
) {
    companion object {
        val EMPTY = Transformer (
            false,
            TransformerTime.EMPTY
        )
    }
}