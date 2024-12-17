package danggai.app.presentation.ui.widget.config

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import danggai.app.presentation.util.log

class WidgetPinnedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val widgetId =
            intent.getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_ID) // <-- NULL
        log.e("widgetId -> $widgetId")
    }
}
