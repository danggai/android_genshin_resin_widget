package danggai.domain.local

enum class TimeNotation(val value: Int) {
    DEFAULT(-1),
    REMAIN_TIME(0),
    FULL_CHARGE_TIME(1),
    DISABLE_TIME(2);

    companion object {
        fun fromValue(value: Int): TimeNotation {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}