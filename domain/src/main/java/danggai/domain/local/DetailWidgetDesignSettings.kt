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
    val transformerDataVisibility: Boolean = true,
    val uidVisibility: Boolean = false,
    val nameVisibility: Boolean = false,
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
            transformerDataVisibility = true,
            uidVisibility = false,
            nameVisibility = false,
            fontSize = Constant.PREF_DEFAULT_WIDGET_DETAIL_FONT_SIZE,
            backgroundTransparency = Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY
        )
    }
}
