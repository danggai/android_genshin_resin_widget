package danggai.app.presentation.ui.widget

import android.appwidget.AppWidgetProvider

object WidgetClasses {
    val HONKAI_SR: List<Class<out AppWidgetProvider>> = listOf(
        TrailPowerWidget::class.java,
        HKSRDetailWidget::class.java,
    )

    val ZZZ: List<Class<out AppWidgetProvider>> = listOf(
        BatteryWidget::class.java,
        ZZZDetailWidget::class.java,
    )

    val DAILY_NOTE: List<Class<out AppWidgetProvider>> = listOf(
        ResinWidget::class.java,
        ResinWidgetResizable::class.java,
        DetailWidget::class.java,
        MiniWidget::class.java,
        TrailPowerWidget::class.java,
        HKSRDetailWidget::class.java,
        BatteryWidget::class.java,
        ZZZDetailWidget::class.java,
    )
}
