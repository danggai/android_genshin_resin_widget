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
import danggai.app.presentation.util.WidgetDesignUtils
import danggai.app.presentation.util.log
import danggai.app.presentation.worker.RefreshWorker
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.TimeNotation
import danggai.domain.network.dailynote.entity.GenshinDailyNoteData
import danggai.domain.util.Constant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DetailWidget() : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        setLocale(context)

        appWidgetIds.forEach { appWidgetId ->
            log.e(appWidgetId)
            val remoteView: RemoteViews = makeRemoteViews(context)

            syncView(appWidgetId, remoteView, context)
            appWidgetManager.updateAppWidget(appWidgetId, remoteView)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action

        val thisWidget = ComponentName(context!!, DetailWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        val widgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
        val uid = intent?.getStringExtra("uid") ?: ""
        val name = intent?.getStringExtra("name") ?: ""

        if (widgetId != -1 && uid.isNotEmpty()) {
            context.let {
                PreferenceManager.setString(context, Constant.PREF_UID + "_$widgetId", uid)
            }
        }
        if (widgetId != -1 && name.isNotEmpty()) {
            context.let {
                PreferenceManager.setString(context, Constant.PREF_NAME + "_$widgetId", name)
            }
        }

        when (action) {
            Constant.ACTION_RESIN_WIDGET_REFRESH_DATA,
            Constant.ACTION_ON_BOOT_COMPLETED -> {
                log.e("REFRESH_DATA")
                setWidgetRefreshing(context, appWidgetManager, appWidgetIds)
                context.let { RefreshWorker.startWorkerPeriodic(context) }
            }

            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                /* 별도 핸들링 없어도 onUpdate 호출 후 여기로 옴 */
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

    private fun setLocale(context: Context) {
        val sLocale = Locale(
            PreferenceManager.getString(
                context,
                Constant.PREF_LOCALE,
                Locale.getDefault().language
            )
        )
        val res = context.resources
        val config = res.configuration
        config.setLocale(sLocale)
    }

    private fun makeRemoteViews(context: Context?): RemoteViews {
        val views = RemoteViews(context!!.packageName, R.layout.widget_detail_fixed)

        val intentUpdate = Intent(context, DetailWidget::class.java).apply {
            action = Constant.ACTION_RESIN_WIDGET_REFRESH_DATA
        }
        views.setOnClickPendingIntent(
            R.id.ll_sync,
            PendingIntent.getBroadcast(
                context,
                0,
                intentUpdate,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        val intentMainActivity = Intent(context, MainActivity::class.java)
        views.setOnClickPendingIntent(
            R.id.iv_resin,
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)
        )
        views.setOnClickPendingIntent(
            R.id.iv_daily_commission,
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)
        )
        views.setOnClickPendingIntent(
            R.id.iv_domain,
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)
        )
        views.setOnClickPendingIntent(
            R.id.iv_serenitea_pot,
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)
        )
        views.setOnClickPendingIntent(
            R.id.iv_warp,
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)
        )
        views.setOnClickPendingIntent(
            R.id.ll_disable,
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)
        )

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(
            ComponentName(
                context.applicationContext,
                DetailWidget::class.java
            )
        )

        manager.updateAppWidget(awId, views)

        return views
    }

    private fun syncView(widgetId: Int, view: RemoteViews, context: Context?) {
        context?.let { _context ->
            val widgetDesign =
                PreferenceManager.getT<DetailWidgetDesignSettings>(
                    context,
                    Constant.PREF_DETAIL_WIDGET_DESIGN_SETTINGS
                ) ?: DetailWidgetDesignSettings.EMPTY

            WidgetDesignUtils.applyWidgetTheme(widgetDesign, _context, view)

            if (CommonFunction.isUidValidate(widgetId, context)) {
                val uid = PreferenceManager.getString(context, Constant.PREF_UID + "_$widgetId")
                val name = PreferenceManager.getString(context, Constant.PREF_NAME + "_$widgetId")
                val recentSyncTimeString =
                    PreferenceManager.getString(context, Constant.PREF_RECENT_SYNC_TIME + "_$uid")
                        .ifEmpty {
                            TimeFunction.getSyncDateTimeString()
                        }
                val recentSyncTimeDate =
                    SimpleDateFormat(Constant.DATE_FORMAT_SYNC_DATE_TIME).parse(recentSyncTimeString)
                        ?: Date()

                log.e()

                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.ll_disable, View.GONE)
                view.setViewVisibility(R.id.ll_body, View.VISIBLE)
                view.setViewVisibility(R.id.ll_bottom, View.VISIBLE)

                val dailyNote = PreferenceManager.getT<GenshinDailyNoteData>(
                    context,
                    Constant.PREF_DAILY_NOTE_DATA + "_$uid"
                ) ?: GenshinDailyNoteData.EMPTY

                view.setViewVisibility(
                    R.id.tv_uid,
                    if (widgetDesign.uidVisibility) View.VISIBLE else View.GONE
                )
                view.setTextViewText(R.id.tv_uid, uid)

                view.setViewVisibility(
                    R.id.tv_name,
                    if (widgetDesign.nameVisibility) View.VISIBLE else View.GONE
                )
                view.setTextViewText(R.id.tv_name, name)

                view.setTextViewText(R.id.tv_resin_title, _context.getString(R.string.resin))
                view.setTextViewText(
                    R.id.tv_resin,
                    dailyNote.currentResin.toString() + "/" + dailyNote.maxResin.toString()
                )

                view.setTextViewText(
                    R.id.tv_daily_commission_title,
                    _context.getString(R.string.daily_commissions)
                )
                view.setTextViewText(
                    R.id.tv_daily_commission,
                    if (dailyNote.isExtraTaskRewardReceived) {
                        _context.getString(R.string.done)
                    } else {
                        (dailyNote.totalTaskNum - dailyNote.finishedTaskNum).toString() + "/" + dailyNote.totalTaskNum.toString()
                    }
                )

                view.setTextViewText(
                    R.id.tv_weekly_boss_title,
                    _context.getString(R.string.enemies_of_note)
                )
                view.setTextViewText(
                    R.id.tv_weekly_boss,
                    if (dailyNote.remainResinDiscountNum == 0) {
                        context.getString(R.string.done)
                    } else {
                        CommonFunction.convertIntToTimes(
                            dailyNote.remainResinDiscountNum,
                            _context
                        )
                    }
                )

                view.setTextViewText(
                    R.id.tv_realm_currency_title,
                    _context.getString(R.string.realm_currency)
                )
                view.setTextViewText(
                    R.id.tv_realm_currency,
                    (dailyNote.currentHomeCoin).toString() + "/" + (dailyNote.maxHomeCoin).toString()
                )

                view.setTextViewText(
                    R.id.tv_transformer_title,
                    _context.getString(R.string.parametric_transformer)
                )
                view.setTextViewText(
                    R.id.tv_transformer,
                    when {
                        dailyNote.transformer == null -> _context.getString(R.string.widget_ui_unknown)
                        !dailyNote.transformer!!.obtained -> _context.getString(R.string.widget_ui_transformer_not_obtained)
                        !dailyNote.transformer!!.recoveryTime.reached -> TimeFunction.transformerToTime(
                            _context,
                            recentSyncTimeDate,
                            dailyNote.transformer,
                            TimeNotation.fromValue(widgetDesign.timeNotation)
                        )

                        else -> _context.getString(R.string.widget_ui_transformer_reached)
                    }
                )

                view.setTextViewText(R.id.tv_sync_time, recentSyncTimeString)

                when (TimeNotation.fromValue(widgetDesign.timeNotation)) {
                    TimeNotation.DEFAULT,
                    TimeNotation.REMAIN_TIME -> {
                        view.setTextViewText(
                            R.id.tv_resin_time_title,
                            _context.getString(R.string.until_fully_replenished)
                        )
                        view.setTextViewText(
                            R.id.tv_realm_currency_time_title,
                            _context.getString(R.string.until_fully_replenished)
                        )
                        view.setTextViewText(
                            R.id.tv_expedition_title,
                            _context.getString(R.string.until_expeditions_done)
                        )
                    }

                    TimeNotation.FULL_CHARGE_TIME -> {
                        view.setTextViewText(
                            R.id.tv_resin_time_title,
                            _context.getString(R.string.estimated_replenishment_time)
                        )
                        view.setTextViewText(
                            R.id.tv_realm_currency_time_title,
                            _context.getString(R.string.estimated_replenishment_time)
                        )
                        view.setTextViewText(
                            R.id.tv_expedition_title,
                            _context.getString(R.string.expeditions_done_at)
                        )
                    }

                    else -> {}
                }

                view.setTextViewText(
                    R.id.tv_resin_time, TimeFunction.resinSecondToTime(
                        _context,
                        recentSyncTimeDate,
                        dailyNote.resinRecoveryTime,
                        TimeNotation.fromValue(widgetDesign.timeNotation)
                    )
                )
                view.setTextViewText(
                    R.id.tv_realm_currency_time, TimeFunction.realmCurrencySecondToTime(
                        _context,
                        recentSyncTimeDate,
                        dailyNote.homeCoinRecoveryTime,
                        TimeNotation.fromValue(widgetDesign.timeNotation)
                    )
                )
                view.setTextViewText(
                    R.id.tv_expedition_time, TimeFunction.expeditionSecondToTime(
                        _context,
                        recentSyncTimeDate,
                        PreferenceManager.getString(
                            context,
                            Constant.PREF_EXPEDITION_TIME + "_$uid"
                        ),
                        TimeNotation.fromValue(widgetDesign.timeNotation)
                    )
                )

                view.setViewVisibility(
                    R.id.rl_resin,
                    if (widgetDesign.resinDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_resin_time,
                    if (widgetDesign.resinDataVisibility &&
                        TimeNotation.fromValue(widgetDesign.timeNotation) != TimeNotation.DISABLE_TIME
                    ) View.VISIBLE else View.GONE
                )

                view.setViewVisibility(
                    R.id.rl_daily_commission,
                    if (widgetDesign.dailyCommissinDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_weekly_boss,
                    if (widgetDesign.weeklyBossDataVisibility) View.VISIBLE else View.GONE
                )

                view.setViewVisibility(
                    R.id.rl_realm_currency,
                    if (widgetDesign.realmCurrencyDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_realm_currency_time,
                    if (widgetDesign.realmCurrencyDataVisibility &&
                        TimeNotation.fromValue(widgetDesign.timeNotation) != TimeNotation.DISABLE_TIME &&
                        dailyNote.homeCoinRecoveryTime != "0"
                    ) View.VISIBLE else View.GONE
                )

                view.setViewVisibility(
                    R.id.rl_expedition,
                    if (widgetDesign.expeditionDataVisibility) View.VISIBLE else View.GONE
                )

                view.setViewVisibility(
                    R.id.rl_transformer,
                    if (widgetDesign.transformerDataVisibility) View.VISIBLE else View.GONE
                )
            } else {
                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.ll_body, View.GONE)
                view.setViewVisibility(R.id.ll_bottom, View.GONE)
                view.setViewVisibility(R.id.ll_disable, View.VISIBLE)

                if ((widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                    view.setTextColor(
                        R.id.tv_disable,
                        getColor(_context, R.color.widget_font_main_dark)
                    )
                } else {
                    view.setTextColor(
                        R.id.tv_disable,
                        getColor(_context, R.color.widget_font_main_light)
                    )
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