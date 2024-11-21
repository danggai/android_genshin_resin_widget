package danggai.domain.local

enum class Preview(val value: Int) {
    GENSHIN(0),
    STARRAIL(1),
    ZZZ(2);

    companion object {
        fun fromValue(value: Int): Preview {
            return Preview.values().find { it.value == value } ?: GENSHIN
        }
    }
}