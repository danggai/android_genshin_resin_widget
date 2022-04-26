package danggai.domain.local

import danggai.domain.util.Constant

data class DetailWidgetDesignSettings(
    val widgetTheme: Int,
    val timeNotation: Int,
    val resinDataVisibility: Boolean,
    val dailyCommissinDataVisibility: Boolean,
    val weeklyBossDataVisibility: Boolean,
    val realmCurrencyDataVisibility: Boolean,
    val expeditionDataVisibility: Boolean,
    val fontSize: Int,
    val backgroundTransparency: Int
) {
    companion object {
        val EMPTY = DetailWidgetDesignSettings (
            widgetTheme = 0,
            timeNotation = 0,
            resinDataVisibility = true,
            dailyCommissinDataVisibility = true,
            weeklyBossDataVisibility = true,
            realmCurrencyDataVisibility = true,
            expeditionDataVisibility = true,
            fontSize = Constant.PREF_DEFAULT_WIDGET_FONT_SIZE_DETAIL,
            backgroundTransparency = Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY
        )
    }
}
