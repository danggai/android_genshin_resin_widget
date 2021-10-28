package danggai.app.resinwidget.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log


class ResinWidget : AppWidgetProvider() {
    companion object {
        private const val ACTION_BUTTON_REFRESH = Constant.ACTION_BUTTON_REFRESH
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        log.e()

        appWidgetIds.forEach { appWidgetId ->
            val views: RemoteViews = addViews(context)
            syncData(views, context)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action
        log.e()
        when (action) {
            Constant.ACTION_BUTTON_REFRESH -> {
                log.e()

                //            this.onUpdate(context)
            }
            Constant.ACTION_APPWIDGET_UPDATE -> {
                log.e()
                if (intent.getBooleanExtra(Constant.REFRESH_DATA, false)) {
                    log.e()
                    context?.let { CommonFunction.startOneTimeRefreshWorker(context) }
                } else if (intent.getBooleanExtra(Constant.REFRESH_UI, false)) {
                    log.e()
                }
            }
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        log.e()
        context?.let {
            CommonFunction.startOneTimeRefreshWorker(context)
        }
    }

    private fun setMyAction(context: Context?): PendingIntent {
        log.e()

        val intent = Intent(ACTION_BUTTON_REFRESH)
        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
//        context?.let {
//            this.onUpdate(context, manager, manager.getAppWidgetIds(ResinWidget))
//        }

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun addViews(context: Context?): RemoteViews {
        log.e()
        val views = RemoteViews(context?.packageName, R.layout.resin_widget)
        views.setOnClickPendingIntent(R.id.ll_sync, setMyAction(context))
        views.setOnClickPendingIntent(R.id.iv_refersh, setMyAction(context))
        return views
    }

    private fun syncData(view: RemoteViews, context: Context?) {
        context?.let { _context ->
            log.e()
            view.setTextViewText(R.id.tv_resin, PreferenceManager.getIntCurrentResin(_context).toString())
            view.setTextViewText(R.id.tv_resin_max, "/"+PreferenceManager.getIntMaxResin(_context).toString())
            view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToTime(PreferenceManager.getStringResinRecoveryTime(_context)))
            view.setTextViewText(R.id.tv_sync_time, PreferenceManager.getStringRecentSyncTime(_context))
            view.setViewVisibility(R.id.progress1, View.GONE)
            view.setViewVisibility(R.id.ll_resin, View.VISIBLE)
        }
    }
}