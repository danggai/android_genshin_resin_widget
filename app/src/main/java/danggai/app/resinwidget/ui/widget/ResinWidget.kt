package danggai.app.resinwidget.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
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

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        log.e()

        appWidgetIds.forEach { appWidgetId ->
            log.e()
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
                log.e("ACTION_BUTTON_REFRESH")

                context?.let { CommonFunction.startOneTimeRefreshWorker(context) }
            }
            Constant.ACTION_APPWIDGET_UPDATE -> {
                log.e("ACTION_APPWIDGET_UPDATE")
                if (intent.getBooleanExtra(Constant.REFRESH_DATA, false)) {
                    log.e("REFRESH_DATA")
                    context?.let { CommonFunction.startOneTimeRefreshWorker(context) }
                } else if (intent.getBooleanExtra(Constant.REFRESH_UI, false)) {
                    log.e("REFRESH_UI")

                    context?.let { it ->

                        val _intent = Intent(it, ResinWidget::class.java)
                        _intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                        val ids = AppWidgetManager.getInstance(it.applicationContext).getAppWidgetIds(ComponentName(it.applicationContext, ResinWidget::class.java))

                        _intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                        it.sendBroadcast(_intent)
                    }

                } else {
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

    private fun addViews(context: Context?): RemoteViews {
        log.e()
        val views = RemoteViews(context!!.packageName, R.layout.resin_widget)

        val intentSync = Intent(context, ResinWidget::class.java)
        intentSync.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intentSync.putExtra(Constant.REFRESH_DATA, true)
        intentSync.putExtra(Constant.REFRESH_UI, true)

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(ComponentName(context.applicationContext, ResinWidget::class.java))

        val pendingSync = PendingIntent.getBroadcast(context, 0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT)

        views.setOnClickPendingIntent(R.id.ll_sync, pendingSync)

        manager.updateAppWidget(awId, views)

        return views
    }

    private fun syncData(view: RemoteViews, context: Context?) {
        context?.let { _context ->
            if (!PreferenceManager.getBooleanIsValidUserData(context)) {
                log.e()
                view.setViewVisibility(R.id.progress1, View.GONE)
                view.setViewVisibility(R.id.iv_resin, View.GONE)
                view.setViewVisibility(R.id.ll_resin, View.GONE)
                view.setViewVisibility(R.id.ll_disable, View.VISIBLE)
            } else {
                log.e()
                view.setTextViewText(R.id.tv_resin, PreferenceManager.getIntCurrentResin(_context).toString())
                view.setTextViewText(R.id.tv_resin_max, "/"+PreferenceManager.getIntMaxResin(_context).toString())
                view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToTime(PreferenceManager.getStringResinRecoveryTime(_context)))
                view.setTextViewText(R.id.tv_sync_time, PreferenceManager.getStringRecentSyncTime(_context))
                view.setViewVisibility(R.id.progress1, View.GONE)
                view.setViewVisibility(R.id.iv_resin, View.VISIBLE)
                view.setViewVisibility(R.id.ll_resin, View.VISIBLE)
                view.setViewVisibility(R.id.ll_disable, View.GONE)
            }

        }
    }
}