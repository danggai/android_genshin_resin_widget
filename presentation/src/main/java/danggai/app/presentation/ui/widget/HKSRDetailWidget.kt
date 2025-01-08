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

            try {
                syncView(appWidgetId, remoteView, context)
            } catch (e: NullPointerException) {
                // 무한재시도 방지용 임시코드
                val lastAttemptTime =
                    PreferenceManager.getLong(context, Constant.PREF_LAST_HONKAI_SR_FAIL_TIME, 0L)
                val currentTime = System.currentTimeMillis()

                // 10분 내 재시도면 그냥 에러 쓰로우
                if (currentTime - lastAttemptTime < 600000) {
                    throw e
                }

                RefreshWorker.startWorkerOneTime(context)
                PreferenceManager.setLong(
                    context,
                    Constant.PREF_LAST_HONKAI_SR_FAIL_TIME,
                    currentTime
                )
            }

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
        fun setText(viewId: Int, text: String?) {
            view.setTextViewText(viewId, text ?: "")
        }

        fun setVisibility(viewId: Int, isVisible: Boolean) {
            view.setViewVisibility(viewId, if (isVisible) View.VISIBLE else View.GONE)
        }

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
                        .ifEmpty { TimeFunction.getSyncDateTimeString() }
                val recentSyncTimeDate =
                    SimpleDateFormat(Constant.DATE_FORMAT_SYNC_DATE_TIME).parse(recentSyncTimeString)
                        ?: Date()

                log.e()

                setVisibility(R.id.pb_loading, false)
                setVisibility(R.id.ll_disable, false)
                setVisibility(R.id.ll_body, true)
                setVisibility(R.id.ll_bottom, true)

                setText(
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
                setVisibility(R.id.rl_assignment, widgetDesign.assignmentTimeDataVisibility)

                setText(R.id.tv_sync_time, recentSyncTimeString)

                setVisibility(R.id.tv_uid, widgetDesign.uidVisibility)
                setText(R.id.tv_uid, uid)

                setVisibility(R.id.tv_name, widgetDesign.nameVisibility)
                setText(R.id.tv_name, name)

                val data = PreferenceManager.getT<HonkaiSrDataLocal>(
                    context,
                    Constant.PREF_HONKAI_SR_DAILY_NOTE_DATA + "_$uid"
                ) ?: HonkaiSrDataLocal.EMPTY

                setVisibility(R.id.ll_error, data.isError)

                with(data.dailyNote) {
                    setText(
                        R.id.tv_trailblaze_power_title,
                        _context.getString(R.string.trailblaze_power)
                    )
                    setText(
                        R.id.tv_trailblaze_power,
                        "$currentStamina/$maxStamina"
                    )
                    setVisibility(
                        R.id.rl_trailblaze_power,
                        widgetDesign.trailBlazepowerDataVisibility
                    )

                    setText(
                        R.id.tv_reserve_trailblaze_power_title,
                        _context.getString(R.string.reserve_trailblaze_power)
                    )
                    setText(
                        R.id.tv_reserve_trailblaze_power,
                        currentReserveStamina.toString()
                    )
                    setVisibility(
                        R.id.rl_reserve_trailblaze_power,
                        widgetDesign.reserveTrailBlazepowerDataVisibility
                    )

                    setText(
                        R.id.tv_daily_training_title,
                        _context.getString(R.string.daily_training)
                    )
                    setText(
                        R.id.tv_daily_training,
                        if (currentTrainScore == maxTrainScore) _context.getString(R.string.done)
                        else "$currentTrainScore/$maxTrainScore"
                    )
                    setVisibility(R.id.rl_daily_training, widgetDesign.dailyTrainingDataVisibility)

                    setText(
                        R.id.tv_echo_of_war_title,
                        _context.getString(R.string.echo_of_war)
                    )
                    setText(
                        R.id.tv_echo_of_war,
                        if (weeklyCocoonCnt == 0) {
                            context.getString(R.string.done)
                        } else {
                            CommonFunction.convertIntToTimes(weeklyCocoonCnt, _context)
                        }
                    )
                    setVisibility(R.id.rl_echo_of_war, widgetDesign.echoOfWarDataVisibility)

                    setText(
                        R.id.tv_simulated_universe_title,
                        _context.getString(R.string.simulated_universe)
                    )
                    setText(
                        R.id.tv_simulated_universe,
                        if (currentRogueScore == maxRogueScore) context.getString(R.string.done)
                        else "$currentRogueScore/$maxRogueScore"
                    )
                    setVisibility(
                        R.id.rl_simulated_universe,
                        widgetDesign.simulatedUniverseDataVisibility
                    )

                    setText(
                        R.id.tv_simulated_universe_title_cleared,
                        _context.getString(R.string.clear_count)
                    )
                    setText(
                        R.id.tv_simulated_universe_cleared,
                        CommonFunction.convertIntToTimes(data.rogueClearCount, _context)
                    )
                    setVisibility(
                        R.id.rl_simulated_universe_cleared,
                        widgetDesign.simulatedUniverseDataVisibility && widgetDesign.simulatedUniverseClearTimeVisibility
                    )

                    setText(
                        R.id.tv_synchronicity_point_title,
                        _context.getString(R.string.divergent_universe)
                    )
                    setText(
                        R.id.tv_synchronicity_point,
                        if (rogueTournWeeklyUnlocked) "$rogueTournWeeklyCur/$rogueTournWeeklyMax"
                        else context.getString(R.string.widget_ui_locked)
                    )
                    setVisibility(
                        R.id.rl_synchronicity_point,
                        widgetDesign.synchronicityPointVisibility
                    )

                    setText(
                        R.id.tv_trailblaze_power_time, TimeFunction.resinSecondToTime(
                            _context,
                            recentSyncTimeDate,
                            staminaRecoverTime,
                            TimeNotation.fromValue(widgetDesign.timeNotation)
                        )
                    )
                    setVisibility(
                        R.id.rl_trailblaze_power_time,
                        widgetDesign.trailBlazepowerDataVisibility &&
                                TimeNotation.fromValue(widgetDesign.timeNotation) != TimeNotation.DISABLE_TIME
                    )
                }

                when (TimeNotation.fromValue(widgetDesign.timeNotation)) {
                    TimeNotation.DEFAULT,
                    TimeNotation.REMAIN_TIME -> {
                        setText(
                            R.id.tv_trailblaze_power_time_title,
                            _context.getString(R.string.until_fully_replenished)
                        )
                        setText(
                            R.id.tv_assignment_title,
                            _context.getString(R.string.until_assignment_done)
                        )
                    }

                    TimeNotation.FULL_CHARGE_TIME -> {
                        setText(
                            R.id.tv_trailblaze_power_time_title,
                            _context.getString(R.string.estimated_replenishment_time)
                        )
                        setText(
                            R.id.tv_assignment_title,
                            _context.getString(R.string.assignment_done_at)
                        )
                    }

                    TimeNotation.DISABLE_TIME -> {}
                }
            } else {
                setVisibility(R.id.pb_loading, false)
                setVisibility(R.id.ll_body, false)
                setVisibility(R.id.ll_bottom, false)
                setVisibility(R.id.ll_disable, true)

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