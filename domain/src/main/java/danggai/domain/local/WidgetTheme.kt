package danggai.domain.local

enum class WidgetTheme(val value: Int) {
    AUTOMATIC(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromValue(value: Int): WidgetTheme {
            return WidgetTheme.values().find { it.value == value } ?: WidgetTheme.AUTOMATIC
        }
    }
}