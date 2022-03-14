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
import danggai.app.resinwidget.ui.main.MainActivity
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import danggai.app.resinwidget.worker.RefreshWorker


class ResinWidget : AppWidgetProvider() {

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
        when (action) {
            Constant.ACTION_APPWIDGET_UPDATE -> {
                log.e("ACTION_APPWIDGET_UPDATE")

                val thisWidget = ComponentName(context!!, ResinWidget::class.java)
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

                if (intent.getBooleanExtra(Constant.REFRESH_DATA, false)) {
                    log.e("REFRESH_DATA")
                    updateAppWidget(context, appWidgetManager, appWidgetIds)

                    context?.let {
                        when (PreferenceManager.getLongAutoRefreshPeriod(context)) {
                            -1L -> {
                                RefreshWorker.startWorkerOneTime(context)
                            }
                            else -> {
                                RefreshWorker.startWorkerPeriodic(context)
                            }
                        }
                    }
                } else if (intent.getBooleanExtra(Constant.REFRESH_UI, false)) {
                    log.e("REFRESH_UI")
                    updateAppWidget(context, appWidgetManager, appWidgetIds)

                    context?.let { it ->

                        val _intent = Intent(it, ResinWidget::class.java)
                        _intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                        val ids = AppWidgetManager.getInstance(it.applicationContext).getAppWidgetIds(ComponentName(it.applicationContext, ResinWidget::class.java))

                        _intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                        it.sendBroadcast(_intent)
                    }

                } else {
                    log.e(action.toString())
                }
            }
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        log.e()
        context?.let {
            RefreshWorker.startWorkerPeriodic(context)
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        log.e()
        context?.let {
            RefreshWorker.shutdownWorker(context)
        }
    }

    private fun addViews(context: Context?): RemoteViews {
        log.e()
        val views = RemoteViews(context!!.packageName, R.layout.widget_resin_fixed)

        val intentUpdate = Intent(context, ResinWidget::class.java)
        intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intentUpdate.putExtra(Constant.REFRESH_DATA, true)
        intentUpdate.putExtra(Constant.REFRESH_UI, true)

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(ComponentName(context.applicationContext, ResinWidget::class.java))

        views.setOnClickPendingIntent(R.id.ll_sync, PendingIntent.getBroadcast(context, 0, intentUpdate, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))

        val intentMainActivity = Intent(context, MainActivity::class.java)
        views.setOnClickPendingIntent(R.id.iv_resin, PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE or  PendingIntent.FLAG_UPDATE_CURRENT))
        views.setOnClickPendingIntent(R.id.ll_disable, PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))

        manager.updateAppWidget(awId, views)

        return views
    }

    private fun syncData(view: RemoteViews, context: Context?) {
        context?.let { _context ->
            CommonFunction.applyWidgetTheme(view, _context)

            if (!PreferenceManager.getBooleanIsValidUserData(context)) {
                log.e()
                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.iv_resin, View.GONE)
                view.setViewVisibility(R.id.ll_resin, View.GONE)
                view.setViewVisibility(R.id.ll_disable, View.VISIBLE)

            } else {
                log.e()
                view.setTextViewText(R.id.tv_resin, PreferenceManager.getIntCurrentResin(_context).toString())
                view.setTextViewText(R.id.tv_resin_max, "/"+PreferenceManager.getIntMaxResin(_context).toString())

                when (PreferenceManager.getIntResinTimeNotation(_context)) {
                    Constant.PREF_TIME_NOTATION_REMAIN_TIME -> view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToRemainTime(_context, PreferenceManager.getStringResinRecoveryTime(_context)))
                    Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToFullChargeTime(_context, PreferenceManager.getStringResinRecoveryTime(_context)))
                    else -> view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToRemainTime(_context, PreferenceManager.getStringResinRecoveryTime(_context)))
                }

                view.setTextViewText(R.id.tv_sync_time, PreferenceManager.getStringRecentSyncTime(_context))
                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.iv_resin, if (PreferenceManager.getIntWidgetResinImageVisibility(context) == 1) View.GONE else View.VISIBLE)
                view.setViewVisibility(R.id.ll_resin, View.VISIBLE)
                view.setViewVisibility(R.id.ll_disable, View.GONE)
            }

        }
    }

    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
//    val widgetText = context.getString(R.string.appwidget_text)
        // Construct the RemoteViews object
//        val views = RemoteViews(context.packageName, R.layout.resin_widget)

        appWidgetIds.forEach { appWidgetId ->
            log.e()
            val view = RemoteViews(context.packageName, R.layout.widget_resin_fixed)

            view.setViewVisibility(R.id.pb_loading, View.VISIBLE)
            view.setViewVisibility(R.id.iv_resin, View.INVISIBLE)
            view.setViewVisibility(R.id.ll_resin, View.INVISIBLE)
            view.setViewVisibility(R.id.ll_disable, View.GONE)

            appWidgetManager.updateAppWidget(appWidgetId, view)
        }
    }
}