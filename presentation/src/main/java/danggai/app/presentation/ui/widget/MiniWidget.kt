package danggai.app.presentation.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import danggai.app.presentation.R
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.TimeFunction
import danggai.app.presentation.util.WidgetDesignUtils
import danggai.app.presentation.util.WidgetUtils
import danggai.app.presentation.util.log
import danggai.app.presentation.worker.RefreshWorker
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.network.dailynote.entity.GenshinDailyNoteData
import danggai.domain.util.Constant


class MiniWidget() : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

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

        val thisWidget = ComponentName(context!!, MiniWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        val widgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1
        val uid = intent?.getStringExtra("uid") ?: ""
        val name = intent?.getStringExtra("name") ?: ""
        val paramType = intent?.getStringExtra("paramType") ?: ""

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
        if (widgetId != -1 && paramType.isNotEmpty()) {
            context.let {
                PreferenceManager.setString(
                    context,
                    Constant.PREF_MINI_WIDGET_TYPE + "_$widgetId",
                    paramType
                )
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

    private fun makeRemoteViews(context: Context?, appWidgetId: Int): RemoteViews {
        val views = RemoteViews(context!!.packageName, R.layout.widget_mini)

        WidgetUtils.setOnClickBroadcastPendingIntent(
            context,
            views,
            R.id.ll_sync,
            WidgetUtils.getUpdateIntent(context, MiniWidget::class.java)
        )

        val mainActivityTargetViews = listOf(
            R.id.iv_resin,
            R.id.iv_transformer,
            R.id.tv_realm_currency,
            R.id.ll_disable
        )
        WidgetUtils.setOnClickActivityPendingIntent(
            context,
            views,
            mainActivityTargetViews,
            WidgetUtils.getMainActivityIntent(context)
        )

        WidgetUtils.setOnClickBroadcastPendingIntent(
            context,
            views,
            R.id.ll_disable,
            WidgetUtils.getWidgetConfigActivityIntent(context, appWidgetId)
        )

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(
            ComponentName(
                context.applicationContext,
                MiniWidget::class.java
            )
        )

        manager.updateAppWidget(awId, views)

        return views
    }

    private fun syncView(widgetId: Int, view: RemoteViews, context: Context?) {
        context?.let {
            val widgetDesign =
                PreferenceManager.getT<ResinWidgetDesignSettings>(
                    context,
                    Constant.PREF_RESIN_WIDGET_DESIGN_SETTINGS
                ) ?: ResinWidgetDesignSettings.EMPTY

            WidgetDesignUtils.applyWidgetTheme(widgetDesign, context, view)

            if (CommonFunction.isUidValidate(widgetId, context)) {
                val uid = PreferenceManager.getString(context, Constant.PREF_UID + "_$widgetId")
                val name = PreferenceManager.getString(context, Constant.PREF_NAME + "_$widgetId")
                val recentSyncTimeString =
                    PreferenceManager.getString(context, Constant.PREF_RECENT_SYNC_TIME + "_$uid")
                        .ifEmpty {
                            TimeFunction.getSyncDateTimeString()
                        }.split(" ")[1]
                val dailyNote = PreferenceManager.getT<GenshinDailyNoteData>(
                    context,
                    Constant.PREF_DAILY_NOTE_DATA + "_$uid"
                ) ?: GenshinDailyNoteData.EMPTY
                log.e()

                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.ll_disable, View.GONE)
                view.setViewVisibility(R.id.ll_resin, View.GONE)
                view.setViewVisibility(R.id.ll_realm_currency, View.GONE)
                view.setViewVisibility(R.id.ll_transformer, View.GONE)

                when (PreferenceManager.getString(
                    context,
                    Constant.PREF_MINI_WIDGET_TYPE + "_$widgetId"
                )) {
                    Constant.PREF_MINI_WIDGET_RESIN -> {
                        view.setViewVisibility(R.id.ll_resin, View.VISIBLE)
                        view.setTextViewText(R.id.tv_resin, dailyNote.currentResin.toString())
                        view.setTextViewText(
                            R.id.tv_resin_max,
                            "/" + dailyNote.maxResin.toString()
                        )
                    }

                    Constant.PREF_MINI_WIDGET_REALM_CURRENCY -> {
                        view.setViewVisibility(R.id.ll_realm_currency, View.VISIBLE)
                        view.setTextViewText(
                            R.id.tv_realm_currency,
                            dailyNote.currentHomeCoin.toString()
                        )
                        view.setTextViewText(
                            R.id.tv_realm_currency_max,
                            "/" + dailyNote.maxHomeCoin.toString()
                        )
                    }

                    Constant.PREF_MINI_WIDGET_PARAMETRIC_TRANSFORMER -> {
                        view.setViewVisibility(R.id.ll_transformer, View.VISIBLE)
                        view.setTextViewText(
                            R.id.tv_transformer,
                            when {
                                dailyNote.transformer == null -> context.getString(R.string.widget_ui_unknown)
                                !dailyNote.transformer!!.obtained -> context.getString(R.string.widget_ui_transformer_not_obtained)
                                !dailyNote.transformer!!.recoveryTime.reached -> context.getString(R.string.widget_ui_transformer_not_reached)
                                else -> context.getString(R.string.widget_ui_transformer_reached)
                            }
                        )
                    }
                }

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

                view.setTextViewText(R.id.tv_sync_time, recentSyncTimeString)
            } else {
                view.setViewVisibility(R.id.pb_loading, View.GONE)
                view.setViewVisibility(R.id.iv_resin, View.GONE)
                view.setViewVisibility(R.id.ll_resin, View.GONE)
                view.setViewVisibility(R.id.ll_bottom, View.GONE)
                view.setViewVisibility(R.id.ll_disable, View.VISIBLE)
            }
        }
    }

    private fun setWidgetRefreshing(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { appWidgetId ->
            log.e()
            val view = RemoteViews(context.packageName, R.layout.widget_resin_resizable)

            view.setViewVisibility(R.id.pb_loading, View.VISIBLE)
            view.setViewVisibility(R.id.iv_resin, View.INVISIBLE)
            view.setViewVisibility(R.id.ll_resin, View.INVISIBLE)
            view.setViewVisibility(R.id.ll_bottom, View.INVISIBLE)
            view.setViewVisibility(R.id.ll_disable, View.GONE)

            appWidgetManager.updateAppWidget(appWidgetId, view)
        }
    }
}