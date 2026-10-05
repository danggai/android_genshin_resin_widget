package danggai.app.presentation.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import danggai.app.presentation.ui.main.MainActivity
import danggai.app.presentation.ui.widget.TalentWidget
import danggai.app.presentation.ui.widget.config.WidgetConfigActivity
import danggai.domain.util.Constant
import java.util.Objects

object WidgetUtils {

    /**
     * 같은 인텐트(액션/컴포넌트/extras)와 뷰는 항상 같은 코드가 나오도록 만든다.
     * 갱신할 때마다 새 PendingIntent가 쌓이지 않고, extras(위젯 id 등)가 다른 인텐트끼리는 구분된다.
     */
    @Suppress("DEPRECATION")
    private fun getRequestCode(intent: Intent, viewId: Int): Int {
        val extras = intent.extras
        val extraList = extras?.keySet()?.sorted()?.map { it to extras.get(it) } ?: emptyList()
        return Objects.hash(intent.action, intent.component, intent.data, extraList, viewId)
    }

    fun setOnClickBroadcastPendingIntent(
        context: Context,
        view: RemoteViews,
        viewId: Int,
        intent: Intent
    ) {
        val requestCode = getRequestCode(intent, viewId)

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
        val requestCode = getRequestCode(intent, viewId)

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
            val requestCode = getRequestCode(intent, viewId)

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

    fun getTalentRefreshIntent(context: Context) =
        Intent(context, TalentWidget::class.java).apply {
            action = Constant.ACTION_TALENT_WIDGET_REFRESH
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