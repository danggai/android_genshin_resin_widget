package danggai.domain.local

enum class DesignTabType(val position: Int, val title: String) {
    STAMINA(0, "Stamina\u00A0"),
    DETAIL(1, "Detail\u00A0"),
    TALENT(2, "Talent\u00A0"),
    UNKNOWN(-1, "");

    companion object {
        fun fromPosition(position: Int): DesignTabType {
            return values().find { it.position == position } ?: UNKNOWN
        }
    }
}