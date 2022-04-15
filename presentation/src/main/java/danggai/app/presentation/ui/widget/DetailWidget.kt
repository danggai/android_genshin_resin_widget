package danggai.app.presentation.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat.getColor
import danggai.app.presentation.R
import danggai.app.presentation.core.util.CommonFunction
import danggai.app.presentation.core.util.CommonFunction.isDarkMode
import danggai.app.presentation.core.util.PreferenceManager
import danggai.app.presentation.core.util.log
import danggai.app.presentation.ui.main.MainActivity
import danggai.app.presentation.worker.RefreshWorker
import danggai.domain.util.Constant
import java.util.*


class DetailWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        log.e()

        val sLocale = Locale(PreferenceManager.getStringLocale(context))

        val res = context.resources
        val config = res.configuration
        config.setLocale(sLocale)

        appWidgetIds.forEach { appWidgetId ->
            val views: RemoteViews = addViews(context)
            syncData(views, context)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action

        val thisWidget = ComponentName(context!!, DetailWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        when (action) {
            Constant.ACTION_RESIN_WIDGET_REFRESH_UI -> {
                log.e("REFRESH_UI")

                context.let { it ->
                    val _intent = Intent(it, DetailWidget::class.java)
                    _intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                    val ids = AppWidgetManager.getInstance(it.applicationContext).getAppWidgetIds(ComponentName(it.applicationContext, DetailWidget::class.java))

                    _intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    it.sendBroadcast(_intent)
                }
            }
            Constant.ACTION_RESIN_WIDGET_REFRESH_DATA,
            Constant.ACTION_ON_BOOT_COMPLETED -> {
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
        val views = RemoteViews(context!!.packageName, R.layout.widget_detail_fixed)

        val intentUpdate = Intent(context, DetailWidget::class.java).apply {
            action = Constant.ACTION_RESIN_WIDGET_REFRESH_DATA
        }
        views.setOnClickPendingIntent(R.id.ll_sync, PendingIntent.getBroadcast(context, 0, intentUpdate, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))

        val intentMainActivity = Intent(context, MainActivity::class.java)
        views.setOnClickPendingIntent(R.id.iv_resin, PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE))
        views.setOnClickPendingIntent(R.id.iv_daily_commission, PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE))
        views.setOnClickPendingIntent(R.id.iv_domain, PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE))
        views.setOnClickPendingIntent(R.id.iv_serenitea_pot, PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE))
        views.setOnClickPendingIntent(R.id.iv_warp, PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE))
        views.setOnClickPendingIntent(R.id.ll_disable, PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE))

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(ComponentName(context.applicationContext, DetailWidget::class.java))

        manager.updateAppWidget(awId, views)

        return views
    }

    private fun syncData(view: RemoteViews, context: Context?) {
        context?.let { _context ->
            CommonFunction.applyWidgetTheme(view, _context)

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

                val dailyNote = CommonFunction.getDailyNoteData(_context)

                view.setTextViewText(R.id.tv_resin_title, _context.getString(R.string.resin))
                view.setTextViewText(R.id.tv_resin, dailyNote.current_resin.toString()+"/"+dailyNote.max_resin.toString())

                view.setTextViewText(R.id.tv_daily_commission_title, _context.getString(R.string.daily_commissions))
                view.setTextViewText(R.id.tv_daily_commission,
                    if (dailyNote.is_extra_task_reward_received) { _context.getString(R.string.done) }
                    else { (dailyNote.total_task_num - dailyNote.finished_task_num).toString()+"/"+dailyNote.total_task_num.toString() }
                )

                view.setTextViewText(R.id.tv_weekly_boss_title, _context.getString(R.string.enemies_of_note))
                view.setTextViewText(R.id.tv_weekly_boss,
                    if (dailyNote.remain_resin_discount_num == 0) { context.getString(R.string.done) }
                    else { dailyNote.remain_resin_discount_num.toString()+"/"+dailyNote.resin_discount_num_limit.toString() }
                )

                view.setTextViewText(R.id.tv_realm_currency_title, _context.getString(R.string.realm_currency))
                view.setTextViewText(R.id.tv_realm_currency, (dailyNote.current_home_coin?:0).toString()+"/"+(dailyNote.max_home_coin?:0).toString())

                view.setTextViewText(R.id.tv_expedition_title, _context.getString(R.string.number_of_expedition))
                view.setTextViewText(R.id.tv_expedition, dailyNote.current_expedition_num.toString()+"/"+dailyNote.max_expedition_num.toString())

                view.setTextViewText(R.id.tv_sync_time, PreferenceManager.getStringRecentSyncTime(_context))


                when (PreferenceManager.getIntDetailTimeNotation(_context)) {
                    Constant.PREF_TIME_NOTATION_REMAIN_TIME -> {
                        log.e()
                        view.setTextViewText(R.id.tv_resin_time_title, _context.getString(R.string.until_fully_replenished))
                        view.setTextViewText(R.id.tv_resin_time, CommonFunction.secondToRemainTime(_context, dailyNote.resin_recovery_time.toString()))
                        view.setTextViewText(R.id.tv_realm_currency_time_title, _context.getString(R.string.until_fully_replenished))
                        view.setTextViewText(R.id.tv_realm_currency_time, CommonFunction.secondToRemainTime(_context, PreferenceManager.getStringHomeCoinRecoveryTime(_context)))
                        view.setTextViewText(R.id.tv_expedition_time_title, _context.getString(R.string.until_all_completed))
                        view.setTextViewText(R.id.tv_expedition_time, CommonFunction.secondToRemainTime(_context, PreferenceManager.getStringExpeditionTime(_context)))
                    }
                    Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> {
                        log.e()
                        view.setTextViewText(R.id.tv_resin_time_title, _context.getString(R.string.when_fully_replenished))
                        view.setTextViewText(R.id.tv_resin_time, CommonFunction.secondToTime(_context, dailyNote.resin_recovery_time.toString(), false))
                        view.setTextViewText(R.id.tv_realm_currency_time_title, _context.getString(R.string.when_fully_replenished))
                        view.setTextViewText(R.id.tv_realm_currency_time, CommonFunction.secondToTime(_context, PreferenceManager.getStringHomeCoinRecoveryTime(_context), true))
                        view.setTextViewText(R.id.tv_expedition_time_title, _context.getString(R.string.estimated_completion_time))
                        view.setTextViewText(R.id.tv_expedition_time, CommonFunction.secondToTime(_context, PreferenceManager.getStringExpeditionTime(_context), false))
                    }
                    else -> {
                        log.e()
                        view.setTextViewText(R.id.tv_resin_time_title, _context.getString(R.string.until_fully_replenished))
                        view.setTextViewText(R.id.tv_resin_time, CommonFunction.secondToRemainTime(_context, dailyNote.resin_recovery_time.toString()))
                        view.setTextViewText(R.id.tv_realm_currency_time_title, _context.getString(R.string.until_fully_replenished))
                        view.setTextViewText(R.id.tv_realm_currency_time, CommonFunction.secondToRemainTime(_context, PreferenceManager.getStringHomeCoinRecoveryTime(_context)))
                        view.setTextViewText(R.id.tv_expedition_time_title, _context.getString(R.string.until_all_completed))
                        view.setTextViewText(R.id.tv_expedition_time, CommonFunction.secondToRemainTime(_context, PreferenceManager.getStringExpeditionTime(_context)))
                    }
                }

                view.setViewVisibility(R.id.rl_resin, if (PreferenceManager.getBooleanWidgetResinDataVisibility(_context)) View.VISIBLE else View.GONE)
                view.setViewVisibility(R.id.rl_resin_time, if (PreferenceManager.getBooleanWidgetResinDataVisibility(_context) && PreferenceManager.getIntDetailTimeNotation(_context) != Constant.PREF_TIME_NOTATION_DISABLE)
                    View.VISIBLE else View.GONE)
                view.setViewVisibility(R.id.rl_daily_commission, if (PreferenceManager.getBooleanWidgetDailyCommissionDataVisibility(_context)) View.VISIBLE else View.GONE)
                view.setViewVisibility(R.id.rl_weekly_boss, if (PreferenceManager.getBooleanWidgetWeeklyBossDataVisibility(_context)) View.VISIBLE else View.GONE)
                view.setViewVisibility(R.id.rl_realm_currency, if (PreferenceManager.getBooleanWidgetRealmCurrencyDataVisibility(_context)) View.VISIBLE else View.GONE)
                view.setViewVisibility(R.id.rl_realm_currency_time, if (PreferenceManager.getBooleanWidgetRealmCurrencyDataVisibility(_context) && PreferenceManager.getIntDetailTimeNotation(_context) != Constant.PREF_TIME_NOTATION_DISABLE)
                    View.VISIBLE else View.GONE)
                view.setViewVisibility(R.id.rl_expedition, if (PreferenceManager.getBooleanWidgetExpeditionDataVisibility(_context)) View.VISIBLE else View.GONE)
                view.setViewVisibility(R.id.rl_expedition_time, if (PreferenceManager.getBooleanWidgetExpeditionDataVisibility(_context) && PreferenceManager.getIntDetailTimeNotation(_context) != Constant.PREF_TIME_NOTATION_DISABLE)
                    View.VISIBLE else View.GONE)
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
            val view = RemoteViews(context.packageName, R.layout.widget_detail_fixed)

            view.setViewVisibility(R.id.pb_loading, View.VISIBLE)
            view.setViewVisibility(R.id.ll_body, View.INVISIBLE)
            view.setViewVisibility(R.id.ll_disable, View.GONE)

            appWidgetManager.updateAppWidget(appWidgetId, view)
        }
    }
}