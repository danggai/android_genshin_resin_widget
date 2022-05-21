package danggai.app.presentation.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import danggai.app.presentation.R
import danggai.app.presentation.service.TalentWidgetItemService
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.CommonFunction.getTimeSyncTimeFormat
import danggai.app.presentation.util.PlayableCharacters
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.app.presentation.worker.TalentWorker
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.util.Constant
import kotlinx.coroutines.*


class TalentWidget() : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            log.e(appWidgetId)
            val views: RemoteViews = addViews(context)

            CoroutineScope(Dispatchers.IO).launch {
                syncView(views, context)
                delay(200L)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }

        val serviceIntent = Intent(context, TalentWidgetItemService::class.java)
        val widget = RemoteViews(context.packageName, R.layout.widget_talent)
        widget.setRemoteAdapter(R.id.gv_characters, serviceIntent)
        appWidgetManager.updateAppWidget(appWidgetIds, widget)

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action

        val thisWidget = ComponentName(context!!, TalentWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        when (action) {
            Constant.ACTION_TALENT_WIDGET_REFRESH,
            Constant.ACTION_ON_BOOT_COMPLETED -> {
                log.e("REFRESH_UI")

                context.let {
                    val _intent = Intent(it, TalentWidget::class.java)
                    _intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                    val ids = AppWidgetManager.getInstance(it.applicationContext).getAppWidgetIds(ComponentName(it.applicationContext, TalentWidget::class.java))

                    _intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    it.sendBroadcast(_intent)
                }

                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.gv_characters)
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

    private fun addViews(context: Context?): RemoteViews {
        log.e()
        val views = RemoteViews(context!!.packageName, R.layout.widget_talent)

        val intentUpdate = Intent(context, TalentWidget::class.java).apply {
            action = Constant.ACTION_TALENT_WIDGET_REFRESH
        }
        views.setOnClickPendingIntent(R.id.ll_sync, PendingIntent.getBroadcast(context, 0, intentUpdate, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(ComponentName(context.applicationContext, TalentWidget::class.java))

        manager.updateAppWidget(awId, views)

        return views
    }

    private fun syncView(view: RemoteViews, context: Context?) {
        context?.let { _context ->
            log.e()

            val widgetDesign =
                PreferenceManager.getT<ResinWidgetDesignSettings>(context, Constant.PREF_RESIN_WIDGET_DESIGN_SETTINGS)?: ResinWidgetDesignSettings.EMPTY

            CommonFunction.applyWidgetTheme(widgetDesign, _context, view)

            val selectedCharacterIds = PreferenceManager.getIntArray(context, Constant.PREF_SELECTED_CHARACTER_ID_LIST)

            val targetCharacters = PlayableCharacters
                .filter {
                    selectedCharacterIds.contains(it.id) &&
                            when (it.talentDay) {
                                Constant.TALENT_DATE_MONTHU -> CommonFunction.getDateInGenshin() in listOf(1, 2, 5)
                                Constant.TALENT_DATE_TUEFRI -> CommonFunction.getDateInGenshin() in listOf(1, 3, 6)
                                Constant.TALENT_DATE_WEDSAT -> CommonFunction.getDateInGenshin() in listOf(1, 4, 7)
                                Constant.TALENT_DATE_ALL -> true
                                else -> false
                            }
                }

            view.setTextViewText(R.id.tv_no_talent_ingredient, _context.getString(R.string.widget_ui_no_talent_ingredient))
            view.setTextViewText(R.id.tv_no_selected_characters, _context.getString(R.string.widget_ui_no_selected_characters))

            view.setViewVisibility(R.id.gv_characters, View.GONE)
            view.setViewVisibility(R.id.tv_no_talent_ingredient, View.GONE)
            view.setViewVisibility(R.id.tv_no_selected_characters, View.GONE)

            if (selectedCharacterIds.isEmpty()) {
                view.setViewVisibility(R.id.tv_no_selected_characters, View.VISIBLE)
            } else if (targetCharacters.isEmpty()) {
                view.setViewVisibility(R.id.tv_no_talent_ingredient, View.VISIBLE)
            } else {
                view.setViewVisibility(R.id.gv_characters, View.VISIBLE)
            }

            view.setTextViewText(R.id.tv_sync_time, getTimeSyncTimeFormat())
            view.setViewVisibility(R.id.pb_loading, View.GONE)
        }
    }
}