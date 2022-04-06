package danggai.app.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import danggai.app.presentation.R
import danggai.app.presentation.core.util.CommonFunction
import danggai.app.presentation.core.util.PreferenceManager
import danggai.app.presentation.core.util.log
import danggai.app.presentation.main.MainActivity
import danggai.app.presentation.worker.RefreshWorker
import danggai.domain.util.Constant


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

        val thisWidget = ComponentName(context!!, ResinWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        when (action) {
            Constant.ACTION_RESIN_WIDGET_REFRESH_UI -> {
                log.e("REFRESH_UI")

                context.let { it ->
                    val _intent = Intent(it, ResinWidget::class.java)
                    _intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                    val ids = AppWidgetManager.getInstance(it.applicationContext).getAppWidgetIds(ComponentName(it.applicationContext, ResinWidget::class.java))

                    _intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    it.sendBroadcast(_intent)
                }
            }
            Constant.ACTION_RESIN_WIDGET_REFRESH_DATA -> {
                log.e("REFRESH_DATA")
                setWidgetRefreshing(context, appWidgetManager, appWidgetIds)
                context.let { RefreshWorker.startWorkerPeriodic(context) }
            }
            Constant.ACTION_APPWIDGET_UPDATE -> {
                log.e(action.toString())
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

        val intentUpdate = Intent(context, ResinWidget::class.java).apply {
            action = Constant.ACTION_RESIN_WIDGET_REFRESH_DATA
        }
        views.setOnClickPendingIntent(R.id.ll_sync, PendingIntent.getBroadcast(context, 0, intentUpdate, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))

        val intentMainActivity = Intent(context, MainActivity::class.java)
        views.setOnClickPendingIntent(R.id.iv_resin, PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE or  PendingIntent.FLAG_UPDATE_CURRENT))
        views.setOnClickPendingIntent(R.id.ll_disable, PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(ComponentName(context.applicationContext, ResinWidget::class.java))

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
                view.setTextViewText(R.id.tv_resin_max, "/"+ PreferenceManager.getIntMaxResin(_context).toString())

                when (PreferenceManager.getIntResinTimeNotation(_context)) {
                    Constant.PREF_TIME_NOTATION_REMAIN_TIME -> view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToRemainTime(_context, PreferenceManager.getStringResinRecoveryTime(_context)))
                    Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToFullChargeTime(_context, PreferenceManager.getStringResinRecoveryTime(_context)))
                    else -> view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToRemainTime(_context, PreferenceManager.getStringResinRecoveryTime(_context)))
                }

                view.setTextViewText(R.id.tv_sync_time, PreferenceManager.getStringRecentSyncTime(_context))
                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.iv_resin, if (PreferenceManager.getIntWidgetResinImageVisibility(context) == Constant.PREF_WIDGET_RESIN_IMAGE_INVISIBLE) View.GONE else View.VISIBLE)
                view.setViewVisibility(R.id.ll_resin, View.VISIBLE)
                view.setViewVisibility(R.id.ll_disable, View.GONE)
            }

        }
    }

    private fun setWidgetRefreshing(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
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