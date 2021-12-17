package danggai.app.resinwidget.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.ui.main.MainActivity
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.CommonFunction.isDarkMode
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log


class ResinWidgetResizable : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        log.e()

        appWidgetIds.forEach { appWidgetId ->
            val views: RemoteViews = addViews(context)
            syncData(views, context)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent?.action
        when (action) {
            Constant.ACTION_APPWIDGET_UPDATE -> {
                log.e("ACTION_APPWIDGET_UPDATE")

                val thisWidget = ComponentName(context!!, ResinWidgetResizable::class.java)
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

                if (intent.getBooleanExtra(Constant.REFRESH_DATA, false)) {
                    log.e("REFRESH_DATA")
                    updateAppWidget(context, appWidgetManager, appWidgetIds)
                    context?.let { CommonFunction.startOneTimeRefreshWorker(context) }
                    context?.let { CommonFunction.startUniquePeriodicRefreshWorker(context) }
                } else if (intent.getBooleanExtra(Constant.REFRESH_UI, false)) {
                    log.e("REFRESH_UI")
                    updateAppWidget(context, appWidgetManager, appWidgetIds)

                    context?.let { it ->

                        val _intent = Intent(it, ResinWidgetResizable::class.java)
                        _intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                        val ids = AppWidgetManager.getInstance(it.applicationContext).getAppWidgetIds(ComponentName(it.applicationContext, ResinWidgetResizable::class.java))

                        _intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                        it.sendBroadcast(_intent)
                    }

                } else {
                    log.e(action.toString())
                }
            }
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        log.e()
        context?.let {
            Toast.makeText(context, context.getString(R.string.msg_toast_resizable_widget_enable), Toast.LENGTH_SHORT).show()
            CommonFunction.startOneTimeRefreshWorker(context)
            CommonFunction.startUniquePeriodicRefreshWorker(context)
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        log.e()
        context?.let {
            CommonFunction.shutdownRefreshWorker(context)
        }
    }

    private fun addViews(context: Context?): RemoteViews {
        log.e()
        val views = RemoteViews(context!!.packageName, R.layout.widget_resin_resizable)

        val intentUpdate = Intent(context, ResinWidgetResizable::class.java)
        intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intentUpdate.putExtra(Constant.REFRESH_DATA, true)
        intentUpdate.putExtra(Constant.REFRESH_UI, true)

        val manager: AppWidgetManager = AppWidgetManager.getInstance(context)
        val awId = manager.getAppWidgetIds(ComponentName(context.applicationContext, ResinWidgetResizable::class.java))

        views.setOnClickPendingIntent(R.id.ll_sync, PendingIntent.getBroadcast(context, 0, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT))

        val intentMainActivity = Intent(context, MainActivity::class.java)
        views.setOnClickPendingIntent(R.id.iv_resin, PendingIntent.getActivity(context, 0, intentMainActivity, 0))
        views.setOnClickPendingIntent(R.id.ll_disable, PendingIntent.getActivity(context, 0, intentMainActivity, 0))

        manager.updateAppWidget(awId, views)

        return views
    }

    private fun syncData(view: RemoteViews, context: Context?) {
        context?.let { _context ->
            if (!PreferenceManager.getBooleanIsValidUserData(context)) {
                log.e()
                view.setViewVisibility(R.id.progress1, View.GONE)
                view.setViewVisibility(R.id.iv_resin, View.GONE)
                view.setViewVisibility(R.id.ll_resin, View.GONE)
                view.setViewVisibility(R.id.ll_disable, View.VISIBLE)

                if (PreferenceManager.getIntWidgetTheme(_context) == Constant.PREF_WIDGET_THEME_LIGHT) {
                    view.setTextColor(R.id.tv_disable, getColor(_context, R.color.widget_font_main_light))
                } else if ((PreferenceManager.getIntWidgetTheme(_context) == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                    view.setTextColor(R.id.tv_disable, getColor(_context, R.color.widget_font_main_dark))
                } else {
                    view.setTextColor(R.id.tv_disable, getColor(_context, R.color.widget_font_main_light))
                }

            } else {
                log.e()
                view.setTextViewText(R.id.tv_resin, PreferenceManager.getIntCurrentResin(_context).toString())
                view.setTextViewText(R.id.tv_resin_max, "/"+PreferenceManager.getIntMaxResin(_context).toString())

                val unwrappedDrawable = AppCompatResources.getDrawable(_context, R.drawable.rounded_square_5dp)
                val wrappedDraable = DrawableCompat.wrap(unwrappedDrawable!!)

                if (PreferenceManager.getIntWidgetTheme(_context) == Constant.PREF_WIDGET_THEME_LIGHT) {
                    log.e()
//                    view.setInt(R.id.ll_body, "setBackgroundColor", ColorUtils.setAlphaComponent(getColor(_context, R.color.white), PreferenceManager.getIntBackgroundTransparency(_context)))
                    view.setTextColor(R.id.tv_resin, getColor(_context, R.color.widget_font_main_light))
                    view.setTextColor(R.id.tv_resin_max, getColor(_context, R.color.widget_font_main_light))
                    view.setTextColor(R.id.tv_remain_time, getColor(_context, R.color.widget_font_main_light))
                    view.setInt(R.id.iv_refersh, "setColorFilter", getColor(_context, R.color.widget_font_sub_light))
                    view.setTextColor(R.id.tv_sync_time, getColor(_context, R.color.widget_font_sub_light))
                } else if ((PreferenceManager.getIntWidgetTheme(_context) == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                    log.e()
//                    view.setInt(R.id.ll_body, "setBackgroundColor", ColorUtils.setAlphaComponent(getColor(_context, R.color.white), PreferenceManager.getIntBackgroundTransparency(_context)))
                    view.setTextColor(R.id.tv_resin, getColor(_context, R.color.widget_font_main_dark))
                    view.setTextColor(R.id.tv_resin_max, getColor(_context, R.color.widget_font_main_dark))
                    view.setTextColor(R.id.tv_remain_time, getColor(_context, R.color.widget_font_main_dark))
                    view.setInt(R.id.iv_refersh, "setColorFilter", getColor(_context, R.color.widget_font_sub_dark))
                    view.setTextColor(R.id.tv_sync_time, getColor(_context, R.color.widget_font_sub_dark))
                } else {
                    log.e()
//                    view.setInt(R.id.ll_body, "setBackgroundColor", ColorUtils.setAlphaComponent(getColor(_context, R.color.white), PreferenceManager.getIntBackgroundTransparency(_context)))
                    view.setTextColor(R.id.tv_resin, getColor(_context, R.color.widget_font_main_light))
                    view.setTextColor(R.id.tv_resin_max, getColor(_context, R.color.widget_font_main_light))
                    view.setTextColor(R.id.tv_remain_time, getColor(_context, R.color.widget_font_main_light))
                    view.setInt(R.id.iv_refersh, "setColorFilter", getColor(_context, R.color.widget_font_sub_light))
                    view.setTextColor(R.id.tv_sync_time, getColor(_context, R.color.widget_font_sub_light))
                }

                when (PreferenceManager.getIntTimeNotation(_context)) {
                    Constant.PREF_TIME_NOTATION_REMAIN_TIME -> view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToRemainTime(_context, PreferenceManager.getStringResinRecoveryTime(_context)))
                    Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToFullChargeTime(_context, PreferenceManager.getStringResinRecoveryTime(_context)))
                    else -> view.setTextViewText(R.id.tv_remain_time, CommonFunction.secondToRemainTime(_context, PreferenceManager.getStringResinRecoveryTime(_context)))
                }

                view.setTextViewText(R.id.tv_sync_time, PreferenceManager.getStringRecentSyncTime(_context))
                view.setViewVisibility(R.id.progress1, View.GONE)
                view.setViewVisibility(R.id.iv_resin, View.VISIBLE)
                view.setViewVisibility(R.id.ll_resin, View.VISIBLE)
                view.setViewVisibility(R.id.ll_disable, View.GONE)
            }

        }
    }

    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
//    val widgetText = context.getString(R.string.appwidget_text)
        // Construct the RemoteViews object
//        val views = RemoteViews(context.packageName, R.layout.resin_widget)

        appWidgetIds.forEach { appWidgetId ->
            log.e()
            val view = RemoteViews(context.packageName, R.layout.widget_resin_resizable)

            view.setViewVisibility(R.id.progress1, View.VISIBLE)
            view.setViewVisibility(R.id.iv_resin, View.GONE)
            view.setViewVisibility(R.id.ll_resin, View.GONE)
            view.setViewVisibility(R.id.ll_disable, View.GONE)

            appWidgetManager.updateAppWidget(appWidgetId, view)
        }
    }
}