package danggai.app.resinwidget.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat.getColor
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.ui.main.MainActivity
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.CommonFunction.isDarkMode
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log


class DetailWidget : AppWidgetProvider() {

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

                val thisWidget = ComponentName(context!!, DetailWidget::class.java)
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

                if (intent.getBooleanExtra(Constant.REFRESH_DATA, false)) {
                    log.e("REFRESH_DATA")
                    updateAppWidget(context, appWidgetManager, appWidgetIds)
                    context?.let { CommonFunction.startOneTimeRefreshWorker(context) }
                    context?.let { CommonFunction.startUniquePeriodicRefreshWorker(context) }
                } else if (intent.getBooleanExtra(Constant.REFRESH_UI, false)) {
                    log.e("REFRESH_UI")
                    updateAppWidget(context, appWidgetManager, appWidgetIds)

                    context?.let { it ->

                        val _intent = Intent(it, DetailWidget::class.java)
                        _intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                        val ids = AppWidgetManager.getInstance(it.applicationContext).getAppWidgetIds(ComponentName(it.applicationContext, DetailWidget::class.java))

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
            CommonFunction.startUniquePeriodicRefreshWorker(context)
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        log.e()
        context?.let {
            CommonFunction.shutdownRefreshWorker(context)
        }
    }

    private fun addViews(context: Context?): RemoteViews {
        log.e()
        val views = RemoteViews(context!!.packageName, R.layout.widget_detail_fixed)

        val intentUpdate = Intent(context, DetailWidget::class.java)
        intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intentUpdate.putExtra(Constant.REFRESH_DATA, true)
        intentUpdate.putExtra(Constant.REFRESH_UI, true)

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(ComponentName(context.applicationContext, DetailWidget::class.java))

        views.setOnClickPendingIntent(R.id.ll_sync, PendingIntent.getBroadcast(context, 0, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT))

        val intentMainActivity = Intent(context, MainActivity::class.java)
        views.setOnClickPendingIntent(R.id.iv_resin, PendingIntent.getActivity(context, 0, intentMainActivity, 0))
        views.setOnClickPendingIntent(R.id.iv_daily_commission, PendingIntent.getActivity(context, 0, intentMainActivity, 0))
        views.setOnClickPendingIntent(R.id.iv_domain, PendingIntent.getActivity(context, 0, intentMainActivity, 0))
        views.setOnClickPendingIntent(R.id.iv_warp, PendingIntent.getActivity(context, 0, intentMainActivity, 0))
        views.setOnClickPendingIntent(R.id.ll_disable, PendingIntent.getActivity(context, 0, intentMainActivity, 0))

        manager.updateAppWidget(awId, views)

        return views
    }

    private fun syncData(view: RemoteViews, context: Context?) {
        context?.let { _context ->
            if (!PreferenceManager.getBooleanIsValidUserData(context)) {
                log.e()
                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.ll_body, View.GONE)
                view.setViewVisibility(R.id.ll_disable, View.VISIBLE)

                if ((PreferenceManager.getIntWidgetTheme(_context) == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                    view.setTextColor(R.id.tv_disable, getColor(_context, R.color.widget_font_main_dark))
                } else {
                    view.setTextColor(R.id.tv_disable, getColor(_context, R.color.widget_font_main_light))
                }

            } else {
                log.e()
                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.ll_body, View.VISIBLE)
                view.setViewVisibility(R.id.ll_disable, View.GONE)

                CommonFunction.setWidgetTheme(view, _context)

                val dailyNote = CommonFunction.getDailyNoteData(_context)

                view.setTextViewText(R.id.tv_resin, dailyNote.current_resin.toString()+"/"+dailyNote.max_resin.toString())
                view.setTextViewText(R.id.tv_resin_time, CommonFunction.secondToTime(_context, dailyNote.resin_recovery_time.toString()))

                view.setTextViewText(R.id.tv_daily_commission,
                    if (dailyNote.is_extra_task_reward_received) { context.getString(R.string.done) }
                    else { (dailyNote.total_task_num - dailyNote.finished_task_num).toString()+"/"+dailyNote.total_task_num.toString() }
                )

                view.setTextViewText(R.id.tv_weekly_boss,
                    if (dailyNote.remain_resin_discount_num == 0) { context.getString(R.string.done) }
                    else { dailyNote.remain_resin_discount_num.toString()+"/"+dailyNote.resin_discount_num_limit.toString() }
                )

                view.setTextViewText(R.id.tv_expedition, dailyNote.current_expedition_num.toString()+"/"+dailyNote.max_expedition_num.toString())
                view.setTextViewText(R.id.tv_expedition_time, CommonFunction.secondToTime(_context, PreferenceManager.getStringExpeditionTime(_context)))

                view.setTextViewText(R.id.tv_sync_time, PreferenceManager.getStringRecentSyncTime(_context))
            }

        }
    }

    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            log.e()
            val view = RemoteViews(context.packageName, R.layout.widget_detail_fixed)

            view.setViewVisibility(R.id.pb_loading, View.VISIBLE)
            view.setViewVisibility(R.id.ll_body, View.INVISIBLE)
            view.setViewVisibility(R.id.ll_disable, View.GONE)

            appWidgetManager.updateAppWidget(appWidgetId, view)
        }
    }
}