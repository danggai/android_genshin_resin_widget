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
import danggai.app.presentation.ui.main.MainActivity
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.CommonFunction.isDarkMode
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.TimeFunction
import danggai.app.presentation.util.log
import danggai.app.presentation.worker.RefreshWorker
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.network.dailynote.entity.DailyNoteData
import danggai.domain.util.Constant
import java.text.SimpleDateFormat
import java.util.*


class DetailWidget() : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val sLocale = Locale(PreferenceManager.getString(context, Constant.PREF_LOCALE, Locale.getDefault().language))

        val res = context.resources
        val config = res.configuration
        config.setLocale(sLocale)

        appWidgetIds.forEach { appWidgetId ->
            log.e(appWidgetId)
            val views: RemoteViews = addViews(context)
            syncView(appWidgetId, views, context)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action

        val thisWidget = ComponentName(context!!, DetailWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        val widgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)?:-1
        val uid = intent?.getStringExtra("uid")?:""
        val name = intent?.getStringExtra("name")?:""

        if (widgetId != -1 && uid.isNotEmpty()) {
            context!!.let {
                PreferenceManager.setString(context, Constant.PREF_UID + "_$widgetId", uid)
            }
        }
        if (widgetId != -1 && name.isNotEmpty()) {
            context!!.let {
                PreferenceManager.setString(context, Constant.PREF_NAME + "_$widgetId", name)
            }
        }

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

    private fun syncView(widgetId: Int, view: RemoteViews, context: Context?) {
        context?.let { _context ->
            val widgetDesign =
                PreferenceManager.getT<DetailWidgetDesignSettings>(context, Constant.PREF_DETAIL_WIDGET_DESIGN_SETTINGS)?: DetailWidgetDesignSettings.EMPTY

            CommonFunction.applyWidgetTheme(widgetDesign, _context, view)

            if (CommonFunction.isUidValidate(widgetId, context)) {
                val uid = PreferenceManager.getString(context, Constant.PREF_UID + "_$widgetId")
                val name = PreferenceManager.getString(context, Constant.PREF_NAME + "_$widgetId")
                val recentSyncTimeString = PreferenceManager.getString(context, Constant.PREF_RECENT_SYNC_TIME + "_$uid").ifEmpty {
                    TimeFunction.getSyncDateTimeString()
                }
                val recentSyncTimeDate = SimpleDateFormat(Constant.DATE_FORMAT_SYNC_DATE_TIME).parse(recentSyncTimeString)?:Date()

                log.e()

                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.ll_disable, View.GONE)
                view.setViewVisibility(R.id.ll_body, View.VISIBLE)
                view.setViewVisibility(R.id.ll_bottom, View.VISIBLE)

                val dailyNote = PreferenceManager.getT<DailyNoteData>(context, Constant.PREF_DAILY_NOTE_DATA + "_$uid")?: DailyNoteData.EMPTY

                view.setViewVisibility(R.id.tv_uid,
                    if(widgetDesign.uidVisibility) View.VISIBLE else View.GONE
                )
                view.setTextViewText(R.id.tv_uid, uid)

                view.setViewVisibility(R.id.tv_name,
                    if(widgetDesign.nameVisibility) View.VISIBLE else View.GONE
                )
                view.setTextViewText(R.id.tv_name, name)

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
                view.setTextViewText(R.id.tv_realm_currency,
                    (dailyNote.current_home_coin).toString()+"/"+(dailyNote.max_home_coin).toString()
                )

                view.setTextViewText(R.id.tv_transformer_title, _context.getString(R.string.parametric_transformer))
                view.setTextViewText(R.id.tv_transformer,
                    when {
                        dailyNote.transformer == null -> _context.getString(R.string.widget_ui_unknown)
                        !dailyNote.transformer!!.obtained -> _context.getString(R.string.widget_ui_transformer_not_obtained)
                        !dailyNote.transformer!!.recovery_time.reached -> TimeFunction.transformerToTime(
                            _context,
                            recentSyncTimeDate,
                            dailyNote.transformer,
                            widgetDesign.timeNotation
                        )
                        else -> _context.getString(R.string.widget_ui_transformer_reached)
                    }
                )

                view.setTextViewText(R.id.tv_sync_time, recentSyncTimeString)

                when (widgetDesign.timeNotation) {
                    Constant.PREF_TIME_NOTATION_DEFAULT,
                    Constant.PREF_TIME_NOTATION_REMAIN_TIME -> {
                        log.e()
                        view.setTextViewText(R.id.tv_resin_time_title, _context.getString(R.string.until_fully_replenished))
                        view.setTextViewText(R.id.tv_realm_currency_time_title, _context.getString(R.string.until_fully_replenished))
                        view.setTextViewText(R.id.tv_expedition_title, _context.getString(R.string.until_expeditions_done))
                    }
                    Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> {
                        log.e()
                        view.setTextViewText(R.id.tv_resin_time_title, _context.getString(R.string.estimated_replenishment_time))
                        view.setTextViewText(R.id.tv_realm_currency_time_title, _context.getString(R.string.estimated_replenishment_time))
                        view.setTextViewText(R.id.tv_expedition_title, _context.getString(R.string.expeditions_done_at))
                    }
                }

                view.setTextViewText(R.id.tv_resin_time, TimeFunction.resinSecondToTime(
                    _context,
                    recentSyncTimeDate,
                    dailyNote.resin_recovery_time,
                    widgetDesign.timeNotation
                ))
                view.setTextViewText(R.id.tv_realm_currency_time, TimeFunction.realmCurrencySecondToTime(
                    _context,
                    recentSyncTimeDate,
                    dailyNote.home_coin_recovery_time,
                    widgetDesign.timeNotation
                ))
                view.setTextViewText(R.id.tv_expedition_time, TimeFunction.expeditionSecondToTime(
                    _context,
                    recentSyncTimeDate,
                    PreferenceManager.getString(
                        context,
                        Constant.PREF_EXPEDITION_TIME + "_$uid"),
                    widgetDesign.timeNotation
                ))

                view.setViewVisibility(R.id.rl_resin,
                    if (widgetDesign.resinDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(R.id.rl_resin_time,
                    if (widgetDesign.resinDataVisibility &&
                        widgetDesign.timeNotation != Constant.PREF_TIME_NOTATION_DISABLE
                    ) View.VISIBLE else View.GONE
                )

                view.setViewVisibility(R.id.rl_daily_commission,
                    if (widgetDesign.dailyCommissinDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(R.id.rl_weekly_boss,
                    if (widgetDesign.weeklyBossDataVisibility) View.VISIBLE else View.GONE
                )

                view.setViewVisibility(R.id.rl_realm_currency,
                    if (widgetDesign.realmCurrencyDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(R.id.rl_realm_currency_time,
                    if (widgetDesign.realmCurrencyDataVisibility &&
                        widgetDesign.timeNotation != Constant.PREF_TIME_NOTATION_DISABLE &&
                        dailyNote.home_coin_recovery_time != "0"
                    ) View.VISIBLE else View.GONE
                )

                view.setViewVisibility(R.id.rl_expedition,
                    if (widgetDesign.expeditionDataVisibility) View.VISIBLE else View.GONE
                )

                view.setViewVisibility(R.id.rl_transformer,
                    if (widgetDesign.transformerDataVisibility) View.VISIBLE else View.GONE
                )
            } else {
                log.e()
                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.ll_body, View.GONE)
                view.setViewVisibility(R.id.ll_bottom, View.GONE)
                view.setViewVisibility(R.id.ll_disable, View.VISIBLE)

                if ((widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                    view.setTextColor(R.id.tv_disable, getColor(_context, R.color.widget_font_main_dark))
                } else {
                    view.setTextColor(R.id.tv_disable, getColor(_context, R.color.widget_font_main_light))
                }
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
            view.setViewVisibility(R.id.ll_bottom, View.INVISIBLE)
            view.setViewVisibility(R.id.ll_disable, View.GONE)

            appWidgetManager.updateAppWidget(appWidgetId, view)
        }
    }
}