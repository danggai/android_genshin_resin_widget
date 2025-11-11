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
    val transformerDataVisibility: Boolean,

    val trailBlazepowerDataVisibility: Boolean,
    val reserveTrailBlazepowerDataVisibility: Boolean,
    val dailyTrainingDataVisibility: Boolean,
    val echoOfWarDataVisibility: Boolean,
    val simulatedUniverseDataVisibility: Boolean,
    val simulatedUniverseClearTimeVisibility: Boolean,
    val gridFightDataVisibility: Boolean,
    val synchronicityPointVisibility: Boolean,
    val assignmentTimeDataVisibility: Boolean,

    val batteryDataVisibility: Boolean,
    val engagementTodayDataVisibility: Boolean,
    val scratchCardDataVisibility: Boolean,
    val videoStoreManagementDataVisibility: Boolean,
    val coffeeDataVisibility: Boolean,
    val riduWeeklyDataVisibility: Boolean,
    val memberCardDataVisibility: Boolean,
    val investigationPointDataVisibility: Boolean,

    val uidVisibility: Boolean,
    val nameVisibility: Boolean,
    val fontSize: Int,
    val backgroundTransparency: Int
) {
    companion object {
        val EMPTY = DetailWidgetDesignSettings(
            widgetTheme = 0,
            timeNotation = 0,

            resinDataVisibility = true,
            dailyCommissinDataVisibility = true,
            weeklyBossDataVisibility = true,
            realmCurrencyDataVisibility = true,
            expeditionDataVisibility = true,
            transformerDataVisibility = true,

            trailBlazepowerDataVisibility = true,
            reserveTrailBlazepowerDataVisibility = true,
            dailyTrainingDataVisibility = true,
            echoOfWarDataVisibility = true,
            simulatedUniverseDataVisibility = true,
            simulatedUniverseClearTimeVisibility = true,
            gridFightDataVisibility = true,
            synchronicityPointVisibility = true,
            assignmentTimeDataVisibility = true,

            batteryDataVisibility = true,
            engagementTodayDataVisibility = true,
            scratchCardDataVisibility = true,
            videoStoreManagementDataVisibility = true,
            coffeeDataVisibility = true,
            riduWeeklyDataVisibility = true,
            memberCardDataVisibility = true,
            investigationPointDataVisibility = true,

            uidVisibility = false,
            nameVisibility = false,
            fontSize = Constant.PREF_DEFAULT_WIDGET_DETAIL_FONT_SIZE,
            backgroundTransparency = Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY
        )
    }
}
