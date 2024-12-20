package danggai.app.presentation.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat.getColor
import danggai.app.presentation.R
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.CommonFunction.isDarkMode
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.TimeFunction
import danggai.app.presentation.util.WidgetDesignUtils
import danggai.app.presentation.util.WidgetUtils
import danggai.app.presentation.util.log
import danggai.app.presentation.worker.RefreshWorker
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.TimeNotation
import danggai.domain.network.dailynote.entity.HonkaiSrDataLocal
import danggai.domain.util.Constant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HKSRDetailWidget() : AppWidgetProvider() {

    companion object {
        val className = HKSRDetailWidget::class.java
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        setLocale(context)

        appWidgetIds.forEach { appWidgetId ->
            log.e(appWidgetId)
            val remoteView: RemoteViews = makeRemoteViews(context, appWidgetId)

            syncView(appWidgetId, remoteView, context)
            appWidgetManager.updateAppWidget(appWidgetId, remoteView)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action

        val thisWidget = ComponentName(context!!, className)
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

    private fun makeRemoteViews(context: Context?, appWidgetId: Int): RemoteViews {
        val views = RemoteViews(context!!.packageName, R.layout.widget_hksr_detail_fixed)

        WidgetUtils.setOnClickBroadcastPendingIntent(
            context,
            views,
            R.id.ll_sync,
            WidgetUtils.getUpdateIntent(context, className)
        )

        val mainActivityTargetViews = listOf(
            R.id.iv_trailblaze_power,
            R.id.iv_daily_training,
            R.id.iv_echo_of_war,
            R.id.iv_simulated_universe,
            R.id.iv_credit
        )
        WidgetUtils.setOnClickActivityPendingIntent(
            context,
            views,
            mainActivityTargetViews,
            WidgetUtils.getMainActivityIntent(context)
        )

        WidgetUtils.setOnClickActivityPendingIntent(
            context,
            views,
            R.id.ll_disable,
            WidgetUtils.getWidgetConfigActivityIntent(context, appWidgetId)
        )

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(
            ComponentName(
                context.applicationContext,
                className
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

                val data = PreferenceManager.getT<HonkaiSrDataLocal>(
                    context,
                    Constant.PREF_HONKAI_SR_DAILY_NOTE_DATA + "_$uid"
                ) ?: HonkaiSrDataLocal.EMPTY

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

                view.setTextViewText(
                    R.id.tv_trailblaze_power_title,
                    _context.getString(R.string.trailblaze_power)
                )
                view.setTextViewText(
                    R.id.tv_trailblaze_power,
                    data.dailyNote.currentStamina.toString() + "/" + data.dailyNote.maxStamina.toString()
                )

                view.setTextViewText(
                    R.id.tv_reserve_trailblaze_power_title,
                    _context.getString(R.string.reserve_trailblaze_power)
                )
                view.setTextViewText(
                    R.id.tv_reserve_trailblaze_power,
                    data.dailyNote.currentReserveStamina.toString()
                )

                view.setTextViewText(
                    R.id.tv_daily_training_title,
                    _context.getString(R.string.daily_training)
                )
                view.setTextViewText(
                    R.id.tv_daily_training,
                    if (data.dailyNote.currentTrainScore == data.dailyNote.maxTrainScore) {
                        _context.getString(R.string.done)
                    } else {
                        data.dailyNote.currentTrainScore.toString() + "/" + data.dailyNote.maxTrainScore.toString()
                    }
                )

                view.setTextViewText(
                    R.id.tv_echo_of_war_title,
                    _context.getString(R.string.echo_of_war)
                )
                view.setTextViewText(
                    R.id.tv_echo_of_war,
                    if (data.dailyNote.weeklyCocoonCnt == 0) {
                        context.getString(R.string.done)
                    } else {
                        CommonFunction.convertIntToTimes(data.dailyNote.weeklyCocoonCnt, _context)
                    }
                )

                view.setTextViewText(
                    R.id.tv_simulated_universe_title,
                    _context.getString(R.string.simulated_universe)
                )
                view.setTextViewText(
                    R.id.tv_simulated_universe,
                    if (data.dailyNote.currentRogueScore == data.dailyNote.maxRogueScore) {
                        context.getString(R.string.done)
                    } else {
                        data.dailyNote.currentRogueScore.toString() + "/" + data.dailyNote.maxRogueScore.toString()
                    }
                )

                view.setTextViewText(
                    R.id.tv_simulated_universe_title_cleared,
                    _context.getString(R.string.clear_count)
                )
                view.setTextViewText(
                    R.id.tv_simulated_universe_cleared,
                    CommonFunction.convertIntToTimes(data.rogueClearCount, _context)
                )

                view.setTextViewText(
                    R.id.tv_synchronicity_point_title,
                    _context.getString(R.string.divergent_universe)
                )
                view.setTextViewText(
                    R.id.tv_synchronicity_point,
                    if (data.dailyNote.rogueTournWeeklyUnlocked) {
                        data.dailyNote.rogueTournWeeklyCur.toString() + "/" + data.dailyNote.rogueTournWeeklyMax.toString()
                    } else {
                        context.getString(R.string.widget_ui_locked)
                    }
                )

                view.setTextViewText(R.id.tv_sync_time, recentSyncTimeString)

                when (TimeNotation.fromValue(widgetDesign.timeNotation)) {
                    TimeNotation.DEFAULT,
                    TimeNotation.REMAIN_TIME -> {
                        view.setTextViewText(
                            R.id.tv_trailblaze_power_time_title,
                            _context.getString(R.string.until_fully_replenished)
                        )
                        view.setTextViewText(
                            R.id.tv_assignment_title,
                            _context.getString(R.string.until_assignment_done)
                        )
                    }

                    TimeNotation.FULL_CHARGE_TIME -> {
                        view.setTextViewText(
                            R.id.tv_trailblaze_power_time_title,
                            _context.getString(R.string.estimated_replenishment_time)
                        )
                        view.setTextViewText(
                            R.id.tv_assignment_title,
                            _context.getString(R.string.assignment_done_at)
                        )
                    }

                    TimeNotation.DISABLE_TIME -> {}
                }

                view.setTextViewText(
                    R.id.tv_trailblaze_power_time, TimeFunction.resinSecondToTime(
                        _context,
                        recentSyncTimeDate,
                        data.dailyNote.staminaRecoverTime,
                        TimeNotation.fromValue(widgetDesign.timeNotation)
                    )
                )
                view.setTextViewText(
                    R.id.tv_assignment_time, TimeFunction.expeditionSecondToTime(
                        _context,
                        recentSyncTimeDate,
                        PreferenceManager.getString(
                            context,
                            Constant.PREF_ASSIGNMENT_TIME + "_$uid"
                        ),
                        TimeNotation.fromValue(widgetDesign.timeNotation)
                    )
                )

                view.setViewVisibility(
                    R.id.rl_trailblaze_power,
                    if (widgetDesign.trailBlazepowerDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_trailblaze_power_time,
                    if (widgetDesign.trailBlazepowerDataVisibility &&
                        TimeNotation.fromValue(widgetDesign.timeNotation) != TimeNotation.DISABLE_TIME
                    ) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_reserve_trailblaze_power,
                    if (widgetDesign.reserveTrailBlazepowerDataVisibility) View.VISIBLE else View.GONE
                )

                view.setViewVisibility(
                    R.id.rl_daily_training,
                    if (widgetDesign.dailyTrainingDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_echo_of_war,
                    if (widgetDesign.echoOfWarDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_simulated_universe,
                    if (widgetDesign.simulatedUniverseDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_simulated_universe_cleared,
                    if (widgetDesign.simulatedUniverseDataVisibility &&
                        widgetDesign.simulatedUniverseClearTimeVisibility
                    ) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_synchronicity_point,
                    if (widgetDesign.synchronicityPointVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_assignment,
                    if (widgetDesign.assignmentTimeDataVisibility) View.VISIBLE else View.GONE
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
            val view = RemoteViews(context.packageName, R.layout.widget_hksr_detail_fixed)

            view.setViewVisibility(R.id.pb_loading, View.VISIBLE)
            view.setViewVisibility(R.id.ll_body, View.INVISIBLE)
            view.setViewVisibility(R.id.ll_bottom, View.INVISIBLE)
            view.setViewVisibility(R.id.ll_disable, View.GONE)

            appWidgetManager.updateAppWidget(appWidgetId, view)
        }
    }
}