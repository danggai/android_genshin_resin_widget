package danggai.app.presentation.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import danggai.app.presentation.ui.main.MainActivity
import danggai.app.presentation.ui.widget.config.WidgetConfigActivity
import danggai.domain.util.Constant

object WidgetUtils {
    fun setOnClickBroadcastPendingIntent(
        context: Context,
        view: RemoteViews,
        viewId: Int,
        intent: Intent
    ) {
        val requestCode = System.currentTimeMillis().toInt()

        view.setOnClickPendingIntent(
            viewId,
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    fun setOnClickActivityPendingIntent(
        context: Context,
        view: RemoteViews,
        viewId: Int,
        intent: Intent
    ) {
        val requestCode = System.currentTimeMillis().toInt()

        view.setOnClickPendingIntent(
            viewId,
            PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    fun setOnClickActivityPendingIntent(
        context: Context,
        view: RemoteViews,
        viewIds: List<Int>,
        intent: Intent
    ) {
        viewIds.forEach { viewId ->
            val requestCode = (System.currentTimeMillis() + viewId).toInt()

            view.setOnClickPendingIntent(
                viewId,
                PendingIntent.getActivity(
                    context,
                    requestCode,
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

    fun <T : AppWidgetProvider> getToastIntent(
        context: Context,
        toastMsg: String,
        widgetClass: Class<T>
    ) =
        Intent(context, widgetClass).apply {
            action = Constant.ACTION_SHOW_TOAST
            this.putExtra(Constant.EXTRA_TOAST_MESSAGE, toastMsg)
        }

    fun getMainActivityIntent(context: Context) = Intent(context, MainActivity::class.java)

    fun getWidgetConfigActivityIntent(
        context: Context,
        appWidgetId: Int
    ) = Intent(context, WidgetConfigActivity::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
}