package danggai.app.resinwidget.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.work.*
import com.google.android.gms.common.internal.Preconditions.checkArgument
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import danggai.app.resinwidget.BuildConfig
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.worker.RefreshWorker
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.streams.asSequence


object CommonFunction {

    fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    fun getGenshinDS(): String {
        val source = "abcdefghijklmnopqrstuvwxyz"

        val t = System.currentTimeMillis() / 1000L
        val r = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Random().ints(6, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
        } else {
            "abcdef"
        }
        val hash = encodeToMD5("salt=${Constant.OS_SALT}&t=$t&r=$r")
        return "${t},${r},${hash}"
    }

    fun shutdownRefreshWorker(context: Context) {
//        if (!PreferenceManager.getBooleanAutoRefresh(context)) return
        log.e()

        val workManager = WorkManager.getInstance(context)

        workManager.cancelUniqueWork(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH)
    }

    fun startOneTimeRefreshWorker(context: Context) {
//        if (!PreferenceManager.getBooleanAutoRefresh(context)) return
        log.e()

        if (!PreferenceManager.getBooleanIsValidUserData(context)) {
            log.e()
            return
        }

        val workManager = WorkManager.getInstance(context)
        val workRequest = OneTimeWorkRequestBuilder<RefreshWorker>()
            .setInputData(workDataOf(Constant.ARG_IS_ONE_TIME to true))
            .build()
        workManager.enqueue(workRequest)
    }

    fun startUniquePeriodicRefreshWorker(context: Context) {
        val period = PreferenceManager.getLongAutoRefreshPeriod(context)

        startUniquePeriodicRefreshWorker(context, period)
    }

    fun startUniquePeriodicRefreshWorker(context: Context, period: Long) {
        if (PreferenceManager.getLongAutoRefreshPeriod(context) == -1L ||
            !PreferenceManager.getBooleanIsValidUserData(context)) {
                log.e()
                return
        }

        log.e("period -> $period")

        val workManager = WorkManager.getInstance(context)
        val workRequest = PeriodicWorkRequestBuilder<RefreshWorker>(period, TimeUnit.MINUTES)
            .setInitialDelay(period, TimeUnit.MINUTES)
            .setInputData(workDataOf(Constant.ARG_IS_ONE_TIME to false))
            .build()
        workManager.enqueueUniquePeriodicWork(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
    }

    private fun encodeToMD5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
            .lowercase(Locale.getDefault())
    }

    fun getIntentAppWidgetUiUpdate(): Intent {
        val broadcast= Intent()
        broadcast.action = Constant.ACTION_APPWIDGET_UPDATE
        broadcast.putExtra(Constant.REFRESH_DATA, false)
        broadcast.putExtra(Constant.REFRESH_UI, true)

        return broadcast
    }

    fun getTimeSyncTimeFormat(): String {
        return SimpleDateFormat(Constant.DATE_FORMAT_SYNC_TIME).format(Date())
    }

    fun sendCrashlyticsApiLog(apiName: String, metaCode: Int, retCode: String?) {
        if (BuildConfig.DEBUG) return

        log.e()
        val keysAndValues = CustomKeysAndValues.Builder()
            .putString("api name", apiName)
            .putInt("meta code", metaCode)
            .putString("ret code", retCode?:"")
            .build()

        FirebaseCrashlytics.getInstance().setCustomKeys(keysAndValues)
    }

    fun secondToTime(context: Context, _second: String): String {
        var hour: Int = 0
        var minute: Int = 0
        var second: Int = 0

        if (_second == "0") return context.getString(R.string.done)

        try {
            hour = _second.toInt() / 3600
            minute = (_second.toInt() - hour * 3600) / 60
            second = _second.toInt() % 600
        } catch (e: Exception) {
            hour = 0
            minute = 0
            second = 0
        }

        return String.format(context.getString(R.string.widget_ui_simple_time), hour, minute)
    }

    fun secondToRemainTime(context: Context, _second: String): String {
        var hour: Int = 0
        var minute: Int = 0
        var second: Int = 0

        try {
            hour = _second.toInt() / 3600
            minute = (_second.toInt() - hour * 3600) / 60
            second = _second.toInt() % 600
        } catch (e: Exception) {
            hour = 0
            minute = 0
            second = 0
        }

        return String.format(context.getString(R.string.widget_ui_remain_time), hour, minute)
    }

    fun secondToFullChargeTime(context: Context, second: String): String {
        val cal = Calendar.getInstance()
        val date= Date()
        cal.time = date

        try {
            if (second.toInt() == 0)
                return context.getString(R.string.widget_ui_max_time_resin_max)

            if (second.toInt() > 144000 || second.toInt() < -144000)
                return String.format(context.getString(R.string.widget_ui_max_time), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))

            cal.add(Calendar.SECOND, second.toInt())

            val minute = String.format("%02d", cal.get(Calendar.MINUTE))

            return String.format(context.getString(R.string.widget_ui_max_time), cal.get(Calendar.HOUR_OF_DAY), minute)
        } catch (e: NumberFormatException) {
            return context.getString(R.string.widget_ui_max_time_resin_max)
        }
    }

    fun secondToTime(context: Context, second: String, includeDate: Boolean): String {
        val cal = Calendar.getInstance()
        val date= Date()
        cal.time = date

        try {
            if (second.toInt() == 0)
                return context.getString(R.string.widget_ui_max_time_resin_max)

            if (second.toInt() > 360000 || second.toInt() < -144000)
                return String.format(context.getString(R.string.widget_ui_time), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))

            cal.add(Calendar.SECOND, second.toInt())

            val minute = String.format("%02d", cal.get(Calendar.MINUTE))

            return if (includeDate) {
                log.e()
                String.format(context.getString(R.string.widget_ui_date), getDayWithMonthSuffix(context, cal.get(Calendar.DATE)), cal.get(Calendar.HOUR_OF_DAY), minute)
            }
            else String.format(context.getString(R.string.widget_ui_time), cal.get(Calendar.HOUR_OF_DAY), minute)
        } catch (e: NumberFormatException) {
            return context.getString(R.string.widget_ui_max_time_resin_max)
        }
    }

    fun getDayWithMonthSuffix(context: Context, n: Int): String {
        checkArgument(n in 1..31, "illegal day of month: $n")
        return if (n in 11..13) {
            n.toString() + context.getString(R.string.date_th)
        } else when (n % 10) {
            1 -> n.toString() + context.getString(R.string.date_st)
            2 -> n.toString() + context.getString(R.string.date_nd)
            3 -> n.toString() + context.getString(R.string.date_rd)
            else -> n.toString() + context.getString(R.string.date_th)
        }
    }

    fun sendNotification(id: Int, context: Context, title: String, msg: String) {
        var notificationId = ""
        var notificationDesc = ""
        var periority: Int = 0

        when (id) {
            in 1..9 -> {
                notificationId = Constant.PUSH_CHANNEL_RESIN_NOTI_ID
                notificationDesc = context.getString(R.string.push_resin_noti_description)
                periority = NotificationCompat.PRIORITY_DEFAULT
            }
            in 10..19 -> {
                notificationId = Constant.PUSH_CHANNEL_CHECK_IN_NOTI_ID
                notificationDesc = context.getString(R.string.push_checkin_description)
                periority = NotificationCompat.PRIORITY_LOW
            }
            in 20..29 -> {
                notificationId = Constant.PUSH_CHANNEL_EXPEDITION_NOTI_ID
                notificationDesc = context.getString(R.string.push_expedition_description)
                periority = NotificationCompat.PRIORITY_DEFAULT
            }
            in 30..39 -> {
                notificationId = Constant.PUSH_CHANNEL_REALM_CURRENCY_NOTI_ID
                notificationDesc = context.getString(R.string.push_realm_currency_description)
                periority = NotificationCompat.PRIORITY_DEFAULT
            }
            else -> {
                notificationId = Constant.PUSH_CHANNEL_DEFAULT_ID
                notificationDesc = context.getString(R.string.push_default_noti_description)
                periority = NotificationCompat.PRIORITY_DEFAULT
            }
        }

        val notificationManager: NotificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) ?: return

        val builder = NotificationCompat.Builder(context, notificationId)
            .setSmallIcon(R.drawable.resin)
            .setContentTitle(title)
            .setContentText(msg)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            .setPriority(periority)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(notificationId, title, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = notificationDesc
                }
            )
        }

        notificationManager.notify(id, builder.build())
    }

    fun calculateDelayUntilChinaMidnight(startCalendar: Calendar): Long {
        val targetCalendar = Calendar.getInstance()
        targetCalendar.timeZone = TimeZone.getTimeZone(Constant.CHINA_TIMEZONE)
        targetCalendar.set(Calendar.MINUTE, 1)
        targetCalendar.set(Calendar.HOUR, 0)
        targetCalendar.set(Calendar.AM_PM, Calendar.AM)

        if (startCalendar.get(Calendar.HOUR_OF_DAY) >= 1) targetCalendar.add(Calendar.DAY_OF_YEAR, 1)

        val delay = (targetCalendar.time.time - startCalendar.time.time) / 60000

        log.e("now time -> ${startCalendar.time}")
        log.e("target time -> ${targetCalendar.time}")
        log.e("delayed -> ${delay / 60}h ${delay % 60}m")

        return if (delay < 0) {
            targetCalendar.add(Calendar.DAY_OF_YEAR, 1)
            (targetCalendar.time.time - startCalendar.time.time) / 60000
        } else {
            delay
        }
    }

    fun Context.isDarkMode(): Boolean {
        return resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    fun getExpeditionTime(dailyNote: DailyNote): String {
        return try {
            if (dailyNote.expeditions == null || dailyNote.expeditions.isEmpty()) "0"
            else dailyNote.expeditions.maxOf { it.remained_time }
        } catch (e: java.lang.Exception) {
            "0"
        }
    }

    fun applyWidgetTheme(view: RemoteViews, context: Context) {
        val bgColor: Int =  if (PreferenceManager.getIntWidgetTheme(context) == Constant.PREF_WIDGET_THEME_LIGHT) {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.white), PreferenceManager.getIntBackgroundTransparency(context))
        } else if  ((PreferenceManager.getIntWidgetTheme(context) == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.black), PreferenceManager.getIntBackgroundTransparency(context))
        } else {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.white), PreferenceManager.getIntBackgroundTransparency(context))
        }

        val mainFontColor: Int =  if (PreferenceManager.getIntWidgetTheme(context) == Constant.PREF_WIDGET_THEME_LIGHT) {
            ContextCompat.getColor(context, R.color.widget_font_main_light)
        } else if  ((PreferenceManager.getIntWidgetTheme(context) == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
            ContextCompat.getColor(context, R.color.widget_font_main_dark)
        } else {
            ContextCompat.getColor(context, R.color.widget_font_main_light)
        }

        val subFontColor: Int =  if (PreferenceManager.getIntWidgetTheme(context) == Constant.PREF_WIDGET_THEME_LIGHT) {
            ContextCompat.getColor(context, R.color.widget_font_sub_light)
        } else if  ((PreferenceManager.getIntWidgetTheme(context) == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
            ContextCompat.getColor(context, R.color.widget_font_sub_dark)
        } else {
            ContextCompat.getColor(context, R.color.widget_font_sub_light)
        }

        view.setInt(R.id.ll_root, "setBackgroundColor", bgColor)
        view.setInt(R.id.iv_refersh, "setColorFilter", subFontColor)
        view.setTextColor(R.id.tv_sync_time, subFontColor)
        view.setTextColor(R.id.tv_disable, mainFontColor)

        when (view.layoutId) {
            R.layout.widget_resin_fixed,
            R.layout.widget_resin_resizable -> {
                log.e()
                view.setTextColor(R.id.tv_resin, mainFontColor)
                view.setTextColor(R.id.tv_resin_max, mainFontColor)
                view.setTextColor(R.id.tv_remain_time, mainFontColor)
            }
            R.layout.widget_detail_fixed -> {
                log.e()
                val fontSize = PreferenceManager.getIntWidgetDetailFontSize(context)

                view.setTextColor(R.id.tv_resin, mainFontColor)
                view.setTextColor(R.id.tv_resin_title, mainFontColor)
                view.setTextColor(R.id.tv_resin_time, mainFontColor)
                view.setTextColor(R.id.tv_resin_time_title, mainFontColor)
                view.setTextColor(R.id.tv_daily_commission, mainFontColor)
                view.setTextColor(R.id.tv_daily_commission_title, mainFontColor)
                view.setTextColor(R.id.tv_weekly_boss, mainFontColor)
                view.setTextColor(R.id.tv_weekly_boss_title, mainFontColor)
                view.setTextColor(R.id.tv_expedition, mainFontColor)
                view.setTextColor(R.id.tv_expedition_title, mainFontColor)
                view.setTextColor(R.id.tv_expedition_time, mainFontColor)
                view.setTextColor(R.id.tv_expedition_time_title, mainFontColor)
                view.setTextColor(R.id.tv_realm_currency, mainFontColor)
                view.setTextColor(R.id.tv_realm_currency_title, mainFontColor)
                view.setTextColor(R.id.tv_realm_currency_time, mainFontColor)
                view.setTextColor(R.id.tv_realm_currency_time_title, mainFontColor)

                view.setFloat(R.id.tv_resin, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_resin_title, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_resin_time, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_resin_time_title, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_daily_commission, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_daily_commission_title, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_weekly_boss, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_weekly_boss_title, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_expedition, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_expedition_title, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_expedition_time, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_expedition_time_title, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_realm_currency, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_realm_currency_title, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_realm_currency_time, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_realm_currency_time_title, "setTextSize", fontSize.toFloat())
            }
            else -> {
                log.e()
            }
        }
    }

    fun setDailyNoteData(context: Context, dailyNote: DailyNote) {
        log.e()
        PreferenceManager.setStringRecentSyncTime(context, getTimeSyncTimeFormat())

        PreferenceManager.setIntCurrentResin(context, dailyNote.current_resin)
        PreferenceManager.setIntMaxResin(context, dailyNote.max_resin)
        PreferenceManager.setStringResinRecoveryTime(context, dailyNote.resin_recovery_time?:"-1")

        PreferenceManager.setIntCurrentDailyCommission(context, dailyNote.finished_task_num)
        PreferenceManager.setIntMaxDailyCommission(context, dailyNote.total_task_num)
        PreferenceManager.setBooleanGetDailyCommissionReward(context, dailyNote.is_extra_task_reward_received)

        PreferenceManager.setIntCurrentHomeCoin(context, dailyNote.current_home_coin?:0)
        PreferenceManager.setIntMaxHomeCoin(context, dailyNote.max_home_coin?:0)
        PreferenceManager.setStringHomeCoinRecoveryTime(context, dailyNote.home_coin_recovery_time?:"-1")

        PreferenceManager.setIntCurrentWeeklyBoss(context, dailyNote.remain_resin_discount_num)
        PreferenceManager.setIntMaxWeeklyBoss(context, dailyNote.resin_discount_num_limit)

        PreferenceManager.setIntCurrentExpedition(context, dailyNote.current_expedition_num)
        PreferenceManager.setIntMaxExpedition(context, dailyNote.max_expedition_num)

        val expeditionTime: String = getExpeditionTime(dailyNote)

        PreferenceManager.setStringExpeditionTime(context, expeditionTime)
    }

    fun getDailyNoteData(context: Context): DailyNote {
        return DailyNote(
            PreferenceManager.getIntCurrentResin(context),
            PreferenceManager.getIntMaxResin(context),
            PreferenceManager.getStringResinRecoveryTime(context),

            PreferenceManager.getIntCurrentDailyCommission(context),
            PreferenceManager.getIntMaxDailyCommission(context),
            PreferenceManager.getBooleanGetDailyCommissionReward(context),

            PreferenceManager.getIntCurrentWeeklyBoss(context),
            PreferenceManager.getIntMaxWeeklyBoss(context),

            PreferenceManager.getIntCurrentHomeCoin(context),
            PreferenceManager.getIntMaxHomeCoin(context),
            PreferenceManager.getStringHomeCoinRecoveryTime(context),

            PreferenceManager.getIntCurrentExpedition(context),
            PreferenceManager.getIntMaxExpedition(context),
            listOf()
        )
    }

}