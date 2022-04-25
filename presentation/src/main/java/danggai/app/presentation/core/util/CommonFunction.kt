package danggai.app.presentation.core.util

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
import com.google.android.gms.common.internal.Preconditions.checkArgument
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import danggai.app.presentation.BuildConfig
import danggai.app.presentation.R
import danggai.app.presentation.ui.widget.ResinWidget
import danggai.domain.network.dailynote.entity.DailyNote
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.util.Constant
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
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

    private fun encodeToMD5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
            .lowercase(Locale.getDefault())
    }

    fun sendBroadcastResinWidgetRefreshData(context: Context) {
        log.e()

        context.apply {
            sendBroadcast( Intent(context, ResinWidget::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_DATA))
            sendBroadcast( Intent(context, danggai.app.presentation.ui.widget.ResinWidgetResizable::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_DATA))
            sendBroadcast( Intent(context, danggai.app.presentation.ui.widget.DetailWidget::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_DATA))
        }
    }

    fun sendBroadcastResinWidgetRefreshUI(context: Context) {
        log.e()

        context.apply {
            sendBroadcast( Intent(context, danggai.app.presentation.ui.widget.ResinWidget::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_UI))
            sendBroadcast( Intent(context, danggai.app.presentation.ui.widget.ResinWidgetResizable::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_UI))
            sendBroadcast( Intent(context, danggai.app.presentation.ui.widget.DetailWidget::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_UI))
        }
    }

    fun getTimeSyncTimeFormat(): String {
        return SimpleDateFormat(Constant.DATE_FORMAT_SYNC_TIME).format(Date())
    }

    fun sendCrashlyticsApiLog(apiName: String, metaCode: Int?, retCode: String?) {
        if (BuildConfig.DEBUG) return

        log.e()
        val keysAndValues = CustomKeysAndValues.Builder()
            .putString("api name", apiName)
            .putInt("meta code", metaCode?:-1)
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

            val minute = cal.get(Calendar.MINUTE)

            return String.format(context.getString(R.string.widget_ui_max_time), cal.get(Calendar.HOUR_OF_DAY), minute)
        } catch (e: NumberFormatException) {
            return context.getString(R.string.widget_ui_max_time_resin_max)
        }
    }

    fun secondToTime(context: Context, second: String, includeDate: Boolean): String {
        val now = Calendar.getInstance()
        val date= Date()
        now.time = date

        try {
            if (second.toInt() == 0)
                return context.getString(R.string.widget_ui_max_time_resin_max)

            if (second.toInt() > 360000 || second.toInt() < -144000)
                return String.format(context.getString(R.string.widget_ui_today), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))

            val target: Calendar = Calendar.getInstance().apply {
                this.time = Date()
                this.add(Calendar.SECOND, second.toInt())
            }

            val minute = now.get(Calendar.MINUTE)

            return if (includeDate || now.get(Calendar.DATE) != target.get(Calendar.DATE)) {
                log.e()
                String.format(context.getString(R.string.widget_ui_date), getDayWithMonthSuffix(context, target.get(Calendar.DATE)), target.get(Calendar.HOUR_OF_DAY), minute)
            }
            else String.format(context.getString(R.string.widget_ui_today), target.get(Calendar.HOUR_OF_DAY), minute)
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

    fun sendNotification(notiType: Constant.NotiType, context: Context, title: String, msg: String) {
        log.e()
        val notificationId: String
        val notificationDesc: String
        val priority: Int

        when (notiType) {
            Constant.NotiType.RESIN_EACH_40,
            Constant.NotiType.RESIN_140,
            Constant.NotiType.RESIN_CUSTOM -> {
                notificationId = Constant.PUSH_CHANNEL_RESIN_NOTI_ID
                notificationDesc = context.getString(R.string.push_resin_noti_description)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
            Constant.NotiType.CHECK_IN_GENSHIN_SUCCESS,
            Constant.NotiType.CHECK_IN_GENSHIN_FAILED,
            Constant.NotiType.CHECK_IN_GENSHIN_ALREADY,
            Constant.NotiType.CHECK_IN_GENSHIN_ACCOUNT_NOT_FOUND -> {
                notificationId = Constant.PUSH_CHANNEL_GENSHIN_CHECK_IN_NOTI_ID
                notificationDesc = context.getString(R.string.push_genshin_checkin_description)
                priority = NotificationCompat.PRIORITY_LOW
            }
            Constant.NotiType.CHECK_IN_HONKAI_3RD_SUCCESS,
            Constant.NotiType.CHECK_IN_HONKAI_3RD_FAILED,
            Constant.NotiType.CHECK_IN_HONKAI_3RD_ALREADY,
            Constant.NotiType.CHECK_IN_HONKAI_3RD_ACCOUNT_NOT_FOUND -> {
                notificationId = Constant.PUSH_CHANNEL_HONKAI_3RD_CHECK_IN_NOTI_ID
                notificationDesc = context.getString(R.string.push_honkai_3rd_checkin_description)
                priority = NotificationCompat.PRIORITY_LOW
            }
            Constant.NotiType.EXPEDITION_DONE -> {
                notificationId = Constant.PUSH_CHANNEL_EXPEDITION_NOTI_ID
                notificationDesc = context.getString(R.string.push_expedition_description)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
            Constant.NotiType.REALM_CURRENCY_FULL -> {
                notificationId = Constant.PUSH_CHANNEL_REALM_CURRENCY_NOTI_ID
                notificationDesc = context.getString(R.string.push_realm_currency_description)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
            else -> {
                notificationId = Constant.PUSH_CHANNEL_DEFAULT_ID
                notificationDesc = context.getString(R.string.push_default_noti_description)
                priority = NotificationCompat.PRIORITY_DEFAULT
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
            .setPriority(priority)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(notificationId, title, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = notificationDesc
                }
            )
        }

        notificationManager.notify(notiType.ordinal, builder.build())
    }

    fun getTimeLeftUntilChinaMidnight(startCalendar: Calendar): Long {
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

    fun getExpeditionTime(dailyNote: DailyNote.Data): String {
        return try {
            if (dailyNote.expeditions.isNullOrEmpty()) "0"
            else dailyNote.expeditions!!.maxOf { it.remained_time }
        } catch (e: java.lang.Exception) {
            "0"
        }
    }

    fun applyWidgetTheme(
        preference: PreferenceManagerRepository,
        context: Context,
        view: RemoteViews,
    ) {
        val bgColor: Int = if (preference.getIntWidgetTheme() == Constant.PREF_WIDGET_THEME_LIGHT) {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.white),
                preference.getIntBackgroundTransparency())
        } else if ((preference.getIntWidgetTheme() == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.black),
                preference.getIntBackgroundTransparency())
        } else {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.white),
                preference.getIntBackgroundTransparency())
        }

        val mainFontColor: Int =
            if (preference.getIntWidgetTheme() == Constant.PREF_WIDGET_THEME_LIGHT) {
                ContextCompat.getColor(context, R.color.widget_font_main_light)
            } else if ((preference.getIntWidgetTheme() == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
                ContextCompat.getColor(context, R.color.widget_font_main_dark)
            } else {
                ContextCompat.getColor(context, R.color.widget_font_main_light)
            }

        val subFontColor: Int =
            if (preference.getIntWidgetTheme() == Constant.PREF_WIDGET_THEME_LIGHT) {
                ContextCompat.getColor(context, R.color.widget_font_sub_light)
            } else if ((preference.getIntWidgetTheme() == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
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
            R.layout.widget_resin_resizable
            -> {
                log.e()

                if (view.layoutId == R.layout.widget_resin_fixed) {
                    val fontSize = preference.getIntWidgetResinFontSize()
                    view.setFloat(R.id.tv_resin, "setTextSize", fontSize.toFloat())
                }

                view.setTextColor(R.id.tv_resin, mainFontColor)
                view.setTextColor(R.id.tv_resin_max, mainFontColor)
                view.setTextColor(R.id.tv_remain_time, mainFontColor)
            }
            R.layout.widget_detail_fixed -> {
                log.e()
                val fontSize = preference.getIntWidgetDetailFontSize()

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

    fun setDailyNoteData(preference: PreferenceManagerRepository, dailyNote: DailyNote.Data) {
        log.e()
        preference.setStringRecentSyncTime(getTimeSyncTimeFormat())

        preference.setIntCurrentResin(dailyNote.current_resin)
        preference.setIntMaxResin(dailyNote.max_resin)
        preference.setStringResinRecoveryTime(dailyNote.resin_recovery_time ?: "-1")

        preference.setIntCurrentDailyCommission(dailyNote.finished_task_num)
        preference.setIntMaxDailyCommission(dailyNote.total_task_num)
        preference.setBooleanGetDailyCommissionReward(dailyNote.is_extra_task_reward_received)

        preference.setIntCurrentHomeCoin(dailyNote.current_home_coin ?: 0)
        preference.setIntMaxHomeCoin(dailyNote.max_home_coin ?: 0)
        preference.setStringHomeCoinRecoveryTime(dailyNote.home_coin_recovery_time ?: "-1")

        preference.setIntCurrentWeeklyBoss(dailyNote.remain_resin_discount_num)
        preference.setIntMaxWeeklyBoss(dailyNote.resin_discount_num_limit)

        preference.setIntCurrentExpedition(dailyNote.current_expedition_num)
        preference.setIntMaxExpedition(dailyNote.max_expedition_num)

        val expeditionTime: String = getExpeditionTime(dailyNote)

        preference.setStringExpeditionTime(expeditionTime)
    }

    fun getDailyNoteData(preference: PreferenceManagerRepository): DailyNote.Data {
        return DailyNote.Data(
            preference.getIntCurrentResin(),
            preference.getIntMaxResin(),
            preference.getStringResinRecoveryTime(),

            preference.getIntCurrentDailyCommission(),
            preference.getIntMaxDailyCommission(),
            preference.getBooleanGetDailyCommissionReward(),

            preference.getIntCurrentWeeklyBoss(),
            preference.getIntMaxWeeklyBoss(),

            preference.getIntCurrentHomeCoin(),
            preference.getIntMaxHomeCoin(),
            preference.getStringHomeCoinRecoveryTime(),

            preference.getIntCurrentExpedition(),
            preference.getIntMaxExpedition(),
            listOf()
        )
    }

}