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
import danggai.domain.network.dailynote.entity.ZZZDailyNoteData
import danggai.domain.util.Constant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ZZZDetailWidget() : AppWidgetProvider() {

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

        val thisWidget = ComponentName(context!!, ZZZDetailWidget::class.java)
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
        val views = RemoteViews(context!!.packageName, R.layout.widget_zzz_detail)

        val intentUpdate = Intent(context, ZZZDetailWidget::class.java).apply {
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
            R.id.iv_battery,
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)
        )
        views.setOnClickPendingIntent(
            R.id.iv_engagement_today,
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)
        )
        views.setOnClickPendingIntent(
            R.id.iv_scratch_card,
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)
        )
        views.setOnClickPendingIntent(
            R.id.iv_video_store_management,
            PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_IMMUTABLE)
        )

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(
            ComponentName(
                context.applicationContext,
                ZZZDetailWidget::class.java
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

                val dailyNote = PreferenceManager.getT<ZZZDailyNoteData>(
                    context,
                    Constant.PREF_ZZZ_DAILY_NOTE_DATA + "_$uid"
                ) ?: ZZZDailyNoteData.EMPTY

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
                    R.id.tv_battery_title,
                    _context.getString(R.string.battery)
                )
                view.setTextViewText(
                    R.id.tv_battery,
                    "${dailyNote.energy.progress.current}/${dailyNote.energy.progress.max}"
                )

                view.setTextViewText(
                    R.id.tv_engagement_today_title,
                    _context.getString(R.string.engagement_today)
                )
                view.setTextViewText(
                    R.id.tv_engagement_today,
                    if (dailyNote.vitality.current == dailyNote.vitality.max) {
                        _context.getString(R.string.done)
                    } else {
                        "${dailyNote.vitality.current}/${dailyNote.vitality.max}"
                    }
                )

                view.setTextViewText(
                    R.id.tv_scratch_card_title,
                    _context.getString(R.string.scratch_card)
                )
                view.setTextViewText(
                    R.id.tv_scratch_card,
                    when (dailyNote.card_sign) {
                        Constant.ZZZCardSign.NO.value -> context.getString(R.string.scratch_card_no)
                        Constant.ZZZCardSign.DONE.value -> context.getString(R.string.scratch_card_done)
                        else -> ""
                    }
                )

                view.setTextViewText(
                    R.id.tv_video_store_management_title,
                    _context.getString(R.string.video_store_management)
                )
                view.setTextViewText(
                    R.id.tv_video_store_management,
                    when (dailyNote.vhs_sale.sale_state) {
                        Constant.ZZZSaleStatus.NO.value -> context.getString(R.string.video_store_management_no)
                        Constant.ZZZSaleStatus.DOING.value -> context.getString(R.string.video_store_management_doing)
                        Constant.ZZZSaleStatus.DONE.value -> context.getString(R.string.video_store_management_done)
                        else -> ""
                    }
                )

                view.setTextViewText(R.id.tv_sync_time, recentSyncTimeString)

                when (widgetDesign.timeNotation) {
                    Constant.PREF_TIME_NOTATION_DEFAULT,
                    Constant.PREF_TIME_NOTATION_REMAIN_TIME -> {
                        view.setTextViewText(
                            R.id.tv_battery_time_title,
                            _context.getString(R.string.until_fully_replenished)
                        )
                        view.setTextViewText(
                            R.id.tv_assignment_title,
                            _context.getString(R.string.until_assignment_done)
                        )
                    }

                    Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> {
                        view.setTextViewText(
                            R.id.tv_battery_time_title,
                            _context.getString(R.string.estimated_replenishment_time)
                        )
                        view.setTextViewText(
                            R.id.tv_assignment_title,
                            _context.getString(R.string.assignment_done_at)
                        )
                    }
                }

                view.setTextViewText(
                    R.id.tv_battery_time, TimeFunction.resinSecondToTime(
                        _context,
                        recentSyncTimeDate,
                        dailyNote.energy.restore,
                        widgetDesign.timeNotation
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
                        widgetDesign.timeNotation
                    )
                )

                view.setViewVisibility(
                    R.id.rl_battery,
                    if (widgetDesign.batteryDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_battery_time,
                    if (widgetDesign.batteryDataVisibility &&
                        widgetDesign.timeNotation != Constant.PREF_TIME_NOTATION_DISABLE
                    ) View.VISIBLE else View.GONE
                )

                view.setViewVisibility(
                    R.id.rl_engagement_today,
                    if (widgetDesign.engagementTodayDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_scratch_card,
                    if (widgetDesign.scratchCardDataVisibility) View.VISIBLE else View.GONE
                )
                view.setViewVisibility(
                    R.id.rl_video_store_management,
                    if (widgetDesign.videoStoreManagementDataVisibility) View.VISIBLE else View.GONE
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