package danggai.domain.local

import danggai.domain.util.Constant

data class ResinWidgetDesignSettings(
    val widgetTheme: Int,
    val timeNotation: Int,
    val resinImageVisibility: Int,
    val uidVisibility: Boolean,
    val fontSize: Int,
    val backgroundTransparency: Int
) {
    companion object {
        val EMPTY = ResinWidgetDesignSettings (
            widgetTheme = 0,
            timeNotation = 0,
            resinImageVisibility = 0,
            uidVisibility = false,
            fontSize = Constant.PREF_DEFAULT_WIDGET_RESIN_FONT_SIZE,
            backgroundTransparency = Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY
        )
    }
}
