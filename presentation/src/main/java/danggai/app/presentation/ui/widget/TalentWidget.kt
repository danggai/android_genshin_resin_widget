package danggai.app.presentation.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import danggai.app.presentation.R
import danggai.app.presentation.service.TalentWidgetItemService
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.PlayableCharacters
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.TimeFunction.getSyncDayString
import danggai.app.presentation.util.WidgetDesignUtils
import danggai.app.presentation.util.WidgetUtils
import danggai.app.presentation.util.log
import danggai.app.presentation.worker.TalentWorker
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.local.TalentDate
import danggai.domain.local.TalentDays
import danggai.domain.network.githubRaw.entity.RecentGenshinCharacters
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TalentWidget() : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        appWidgetIds.forEach { appWidgetId ->
            log.e(appWidgetId)

            val paramType = PreferenceManager.getString(
                context,
                Constant.PREF_TELENT_WIDGET_TYPE + "_$appWidgetId"
            )

            log.e("paramType: $paramType")
            val serviceIntent = Intent(context, TalentWidgetItemService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra("paramType", paramType)
                data = Uri.parse("content://danggai.app.resinwidget/$appWidgetId")
            }

            val remoteView = makeRemoteViews(context)
            remoteView.setRemoteAdapter(R.id.gv_characters, serviceIntent)

            CoroutineScope(Dispatchers.Main.immediate).launch {
                syncView(appWidgetId, remoteView, context)
                appWidgetManager.updateAppWidget(appWidgetId, remoteView)
            }
        }
    }

    private fun getWidgetId(intent: Intent?) =
        intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) ?: -1

    private fun getParamType(intent: Intent?) = intent?.getStringExtra("paramType") ?: ""

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action

        val thisWidget = ComponentName(context!!, TalentWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        val widgetId = getWidgetId(intent)
        val paramType = getParamType(intent)

        if (widgetId != -1 && paramType.isNotEmpty()) {
            context.let {
                PreferenceManager.setString(
                    context,
                    Constant.PREF_TELENT_WIDGET_TYPE + "_$widgetId",
                    paramType
                )
            }
        }

        when (action) {
            Constant.ACTION_TALENT_WIDGET_REFRESH,
            Constant.ACTION_ON_BOOT_COMPLETED -> {
                log.e("REFRESH_UI")
                setWidgetRefreshing(context, appWidgetManager, appWidgetIds)

                CoroutineScope(Dispatchers.Main.immediate).launch {
                    delay(500L)

                    context.let {
                        val _intent = Intent(it, TalentWidget::class.java)
                        _intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                        val ids = AppWidgetManager.getInstance(it.applicationContext)
                            .getAppWidgetIds(
                                ComponentName(
                                    it.applicationContext,
                                    TalentWidget::class.java
                                )
                            )

                        _intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                        it.sendBroadcast(_intent)
                    }

                    appWidgetManager.notifyAppWidgetViewDataChanged(
                        appWidgetIds,
                        R.id.gv_characters
                    )
                }
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
            TalentWorker.startWorkerOneTimeImmediately(context)
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        log.e()
        context?.let {
            TalentWorker.shutdownWorker(context)
        }
    }

    private fun makeRemoteViews(context: Context?): RemoteViews {
        val remoteViews = RemoteViews(context!!.packageName, R.layout.widget_talent)

        WidgetUtils.setOnClickBroadcastPendingIntent(
            context,
            remoteViews,
            R.id.ll_sync,
            WidgetUtils.getUpdateIntent(context, DetailWidget::class.java)
        )

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(
            ComponentName(
                context.applicationContext,
                TalentWidget::class.java
            )
        )

        awId.forEach { appWidgetId ->
            log.e(appWidgetId)
            manager.updateAppWidget(appWidgetId, remoteViews)
        }

        return remoteViews
    }

    private fun syncView(widgetId: Int, view: RemoteViews, context: Context?) {
        fun initViews() {
            view.setViewVisibility(R.id.gv_characters, View.GONE)
            view.setViewVisibility(R.id.tv_no_talent_ingredient, View.GONE)
            view.setViewVisibility(R.id.tv_no_selected_characters, View.GONE)


            view.setTextViewText(
                R.id.tv_no_talent_ingredient,
                context?.getString(R.string.widget_ui_no_talent_ingredient)
            )
            view.setTextViewText(
                R.id.tv_no_selected_characters,
                context?.getString(R.string.widget_ui_no_selected_characters)
            )

            view.setTextViewText(R.id.tv_sync_time, getSyncDayString())
        }

        context?.let { _context ->
            log.e()

            val widgetDesign =
                PreferenceManager.getT<ResinWidgetDesignSettings>(
                    context,
                    Constant.PREF_RESIN_WIDGET_DESIGN_SETTINGS
                ) ?: ResinWidgetDesignSettings.EMPTY

            WidgetDesignUtils.applyWidgetTheme(widgetDesign, _context, view)

            val widgetType = PreferenceManager.getString(
                context,
                Constant.PREF_TELENT_WIDGET_TYPE + "_$widgetId"
            )

            initViews()

            when (widgetType) {
                Constant.PREF_TALENT_RECENT_CHARACTERS -> {
                    val selectedCharacterIds = PreferenceManager.getT<RecentGenshinCharacters>(
                        context,
                        Constant.PREF_RECENT_CHARACTER_LIST
                    )?.characters ?: listOf()

                    log.e(selectedCharacterIds)

                    val targetCharacters = selectedCharacterIds.filter {
                        isTalentAvailableToday(it.talentDay)
                    }

                    if (targetCharacters.isEmpty()) {
                        view.setViewVisibility(R.id.tv_no_talent_ingredient, View.VISIBLE)
                    } else {
                        view.setViewVisibility(R.id.gv_characters, View.VISIBLE)
                    }
                }

                else -> {
                    val selectedCharacterIds =
                        PreferenceManager.getIntArray(
                            context,
                            Constant.PREF_SELECTED_CHARACTER_ID_LIST
                        )

                    val targetCharacters = PlayableCharacters.filter {
                        selectedCharacterIds.contains(it.id) && isTalentAvailableToday(it.talentDay)
                    }

                    if (selectedCharacterIds.isEmpty()) {
                        view.setViewVisibility(R.id.tv_no_selected_characters, View.VISIBLE)
                    } else if (targetCharacters.isEmpty()) {
                        view.setViewVisibility(R.id.tv_no_talent_ingredient, View.VISIBLE)
                    } else {
                        view.setViewVisibility(R.id.gv_characters, View.VISIBLE)
                    }
                }
            }

            view.setViewVisibility(R.id.pb_loading, View.GONE)
            view.setViewVisibility(R.id.ll_body, View.VISIBLE)
        }
    }

    private fun isTalentAvailableToday(talentDate: TalentDate): Boolean {
        val currentDate = CommonFunction.getDateInGenshin()
        return when (talentDate) {
            TalentDate.MON_THU -> currentDate in TalentDays.MON_THU
            TalentDate.TUE_FRI -> currentDate in TalentDays.TUE_FRI
            TalentDate.WED_SAT -> currentDate in TalentDays.WED_SAT
            TalentDate.ALL -> true
        }
    }

    private fun setWidgetRefreshing(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            log.e()
            val view = RemoteViews(context.packageName, R.layout.widget_talent)

            view.setViewVisibility(R.id.pb_loading, View.VISIBLE)
            view.setViewVisibility(R.id.ll_body, View.GONE)

            appWidgetManager.updateAppWidget(appWidgetId, view)
        }
    }
}