package danggai.app.presentation.util

import android.app.PendingIntent
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import danggai.app.presentation.ui.main.MainActivity
import danggai.app.presentation.ui.widget.config.WidgetConfigActivity
import danggai.domain.util.Constant

object WidgetUtils {
    fun setOnClickPendingIntentForWidget(
        context: Context,
        view: RemoteViews,
        viewId: Int,
        intent: Intent
    ) {
        view.setOnClickPendingIntent(
            viewId,
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    fun setOnClickPendingIntentForWidget(
        context: Context,
        view: RemoteViews,
        viewIds: List<Int>,
        intent: Intent
    ) {
        viewIds.forEach { viewId ->
            view.setOnClickPendingIntent(
                viewId,
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
    }

    fun <T : AppWidgetProvider> getUpdateIntent(context: Context, widgetClass: Class<T>) =
        Intent(context, widgetClass).apply {
            action = Constant.ACTION_RESIN_WIDGET_REFRESH_DATA
        }

    fun getMainActivityIntent(context: Context) = Intent(context, MainActivity::class.java)

    fun getWidgetConfigActivityIntent(context: Context) =
        Intent(context, WidgetConfigActivity::class.java)
}