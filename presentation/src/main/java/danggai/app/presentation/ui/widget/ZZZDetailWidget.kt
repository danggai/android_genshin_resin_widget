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
import danggai.domain.network.dailynote.entity.ZZZDailyNoteData
import danggai.domain.util.Constant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ZZZDetailWidget() : AppWidgetProvider() {

    companion object {
        val className = ZZZDetailWidget::class.java
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
        val views = RemoteViews(context!!.packageName, R.layout.widget_zzz_detail)

        WidgetUtils.setOnClickBroadcastPendingIntent(
            context,
            views,
            R.id.ll_sync,
            WidgetUtils.getUpdateIntent(context, className)
        )

        val mainActivityTargetViews = listOf(
            R.id.iv_battery,
            R.id.iv_engagement_today,
            R.id.iv_scratch_card,
            R.id.iv_video_store_management,
            R.id.iv_investigation_point,
            R.id.iv_ridu_weekly,
            R.id.iv_coffee,
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
                        .ifEmpty {
                            TimeFunction.getSyncDateTimeString()
                        }
                val recentSyncTimeDate =
                    SimpleDateFormat(Constant.DATE_FORMAT_SYNC_DATE_TIME).parse(recentSyncTimeString)
                        ?: Date()

                log.e()

                setVisibility(R.id.pb_loading, false)
                setVisibility(R.id.ll_disable, false)
                setVisibility(R.id.ll_body, true)
                setVisibility(R.id.ll_bottom, true)

                val dailyNote = PreferenceManager.getT<ZZZDailyNoteData>(
                    context,
                    Constant.PREF_ZZZ_DAILY_NOTE_DATA + "_$uid"
                ) ?: ZZZDailyNoteData.EMPTY

                setText(R.id.tv_sync_time, recentSyncTimeString)

                setVisibility(R.id.tv_uid, widgetDesign.uidVisibility)
                setText(R.id.tv_uid, uid)

                setVisibility(R.id.tv_name, widgetDesign.nameVisibility)
                setText(R.id.tv_name, name)

                with(dailyNote) {
                    setText(R.id.tv_battery_title, _context.getString(R.string.battery))
                    setText(R.id.tv_battery, "${energy.progress.current}/${energy.progress.max}")
                    setVisibility(R.id.rl_battery, widgetDesign.batteryDataVisibility)

                    setText(
                        R.id.tv_battery_time, TimeFunction.resinSecondToTime(
                            _context,
                            recentSyncTimeDate,
                            energy.restore,
                            TimeNotation.fromValue(widgetDesign.timeNotation)
                        )
                    )
                    setVisibility(
                        R.id.rl_battery_time,
                        widgetDesign.batteryDataVisibility &&
                                TimeNotation.fromValue(widgetDesign.timeNotation) != TimeNotation.DISABLE_TIME
                    )

                    setText(
                        R.id.tv_engagement_today_title,
                        _context.getString(R.string.engagement_today)
                    )
                    setText(
                        R.id.tv_engagement_today,
                        if (vitality.current == vitality.max) _context.getString(R.string.done)
                        else "${vitality.current}/${vitality.max}"
                    )
                    setVisibility(
                        R.id.rl_engagement_today,
                        widgetDesign.engagementTodayDataVisibility
                    )

                    // TODO(커피 관련 데이터추가 시 해제)
                    setVisibility(R.id.rl_coffee, false)
//                setText(
//                    R.id.tv_coffee_title,
//                    _context.getString(R.string.coffee)
//                )
//                setText(
//                    R.id.tv_coffee,
//                    if (vitality.current == vitality.max) _context.getString(R.string.done)
//                    else "${vitality.current}/${vitality.max}"
//                )
//                setVisibility(R.id.rl_coffee, widgetDesign.coffeeDataVisibility)

                    setText(
                        R.id.tv_ridu_weekly_title,
                        _context.getString(R.string.ridu_weekly)
                    )
                    setText(
                        R.id.tv_ridu_weekly,
                        if ((weeklyTask?.curPoint == weeklyTask?.maxPoint) && weeklyTask !== null) {
                            _context.getString(R.string.done)
                        } else {
                            "${weeklyTask?.curPoint ?: "?"}/${weeklyTask?.maxPoint ?: "?"}"
                        }
                    )
                    setVisibility(R.id.rl_ridu_weekly, widgetDesign.riduWeeklyDataVisibility)

                    val expDays: Int = (memberCard?.expTime ?: 0) / (60 * 60 * 24)
                    val expDaysString =
                        if (expDays < 1)
                            _context.getString(R.string.zzz_member_card_less_1day)
                        else _context.resources.getQuantityString(
                            R.plurals.zzz_member_card_days, expDays, expDays
                        )
                    log.e(expDaysString)
                    val dayText = if (memberCard?.isOpen == true) " ($expDaysString)" else ""

                    setText(
                        R.id.tv_member_card_title,
                        _context.getString(R.string.zzz_member_card) + dayText
                    )
                    setText(
                        R.id.tv_member_card,
                        if (memberCard?.isOpen == false) {
                            _context.getString(R.string.zzz_member_card_not_opened)
                        } else {
                            when (memberCard?.memberCardState) {
                                Constant.ZZZMemberCardState.NO.value -> _context.getString(R.string.zzz_member_card_no)
                                Constant.ZZZMemberCardState.DONE.value -> _context.getString(R.string.zzz_member_card_ack)
                                else -> ""
                            }
                        }
                    )
                    setVisibility(R.id.rl_member_card, widgetDesign.memberCardDataVisibility)

                    setText(
                        R.id.tv_investigation_point_title,
                        _context.getString(R.string.investigation_point)
                    )
                    setText(
                        R.id.tv_investigation_point,
                        if ((surveyPoints?.num == surveyPoints?.total) && surveyPoints !== null) {
                            _context.getString(R.string.done)
                        } else {
                            "${surveyPoints?.num ?: "?"}/${surveyPoints?.total ?: "?"}"
                        }
                    )
                    setVisibility(
                        R.id.rl_investigation_point,
                        widgetDesign.investigationPointDataVisibility
                    )

                    setText(
                        R.id.tv_scratch_card_title,
                        _context.getString(R.string.scratch_card)
                    )
                    setText(
                        R.id.tv_scratch_card,
                        when (cardSign) {
                            Constant.ZZZCardSign.NO.value -> context.getString(R.string.scratch_card_no)
                            Constant.ZZZCardSign.DONE.value -> context.getString(R.string.scratch_card_done)
                            else -> ""
                        }
                    )
                    setVisibility(R.id.rl_scratch_card, widgetDesign.scratchCardDataVisibility)

                    setText(
                        R.id.tv_video_store_management_title,
                        _context.getString(R.string.video_store_management)
                    )
                    setText(
                        R.id.tv_video_store_management,
                        when (vhsSale.saleState) {
                            Constant.ZZZSaleStatus.NO.value -> context.getString(R.string.video_store_management_no)
                            Constant.ZZZSaleStatus.DOING.value -> context.getString(R.string.video_store_management_doing)
                            Constant.ZZZSaleStatus.DONE.value -> context.getString(R.string.video_store_management_done)
                            else -> ""
                        }
                    )
                    setVisibility(
                        R.id.rl_video_store_management,
                        widgetDesign.videoStoreManagementDataVisibility
                    )
                }

                when (TimeNotation.fromValue(widgetDesign.timeNotation)) {
                    TimeNotation.DEFAULT,
                    TimeNotation.REMAIN_TIME -> {
                        setText(
                            R.id.tv_battery_time_title,
                            _context.getString(R.string.until_fully_replenished)
                        )
                    }

                    TimeNotation.FULL_CHARGE_TIME -> {
                        setText(
                            R.id.tv_battery_time_title,
                            _context.getString(R.string.estimated_replenishment_time)
                        )
                    }

                    else -> {}
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