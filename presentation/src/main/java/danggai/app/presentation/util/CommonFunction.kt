package danggai.app.presentation.util

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
import danggai.app.presentation.ui.widget.DetailWidget
import danggai.app.presentation.ui.widget.ResinWidget
import danggai.app.presentation.ui.widget.ResinWidgetResizable
import danggai.domain.local.CheckInSettings
import danggai.domain.local.DailyNoteSettings
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.network.dailynote.entity.DailyNoteData
import danggai.domain.network.dailynote.entity.TransformerTime
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

    private fun encodeToMD5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
            .lowercase(Locale.getDefault())
    }

    fun sendBroadcastResinWidgetRefreshData(context: Context) {
        log.e()

        context.apply {
            sendBroadcast(Intent(context,
                ResinWidget::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_DATA))
            sendBroadcast(Intent(context,
                ResinWidgetResizable::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_DATA))
            sendBroadcast(Intent(context,
                DetailWidget::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_DATA))
        }
    }

    fun sendBroadcastResinWidgetRefreshUI(context: Context) {
        log.e()

        context.apply {
            sendBroadcast(Intent(context,
                ResinWidget::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_UI))
            sendBroadcast(Intent(context,
                ResinWidgetResizable::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_UI))
            sendBroadcast(Intent(context,
                DetailWidget::class.java).setAction(Constant.ACTION_RESIN_WIDGET_REFRESH_UI))
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
            .putInt("meta code", metaCode ?: -1)
            .putString("ret code", retCode ?: "")
            .build()

        FirebaseCrashlytics.getInstance().setCustomKeys(keysAndValues)
    }

    fun secondToRemainTime(context: Context, _second: String): String {
        var hour: Int
        var minute: Int

        try {
            hour = _second.toInt() / 3600
            minute = (_second.toInt() - hour * 3600) / 60
        } catch (e: Exception) {
            hour = 0
            minute = 0
        }

        return String.format(context.getString(R.string.widget_ui_remain_time), hour, minute)
    }

    fun secondToFullChargeTime(
        context: Context,
        second: String
    ): String {
        val cal = Calendar.getInstance()
        val date = Date()
        cal.time = date

        try {
            if (second.toInt() == 0)
                return context.getString(R.string.widget_ui_parameter_max)

            if (second.toInt() > 144000 || second.toInt() < -144000)
                return String.format(context.getString(R.string.widget_ui_max_time),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE))

            cal.add(Calendar.SECOND, second.toInt())

            val minute = cal.get(Calendar.MINUTE)

            return String.format(context.getString(R.string.widget_ui_max_time),
                cal.get(Calendar.HOUR_OF_DAY),
                minute)
        } catch (e: NumberFormatException) {
            return context.getString(R.string.widget_ui_parameter_max)
        }
    }

    fun secondToTime(
        context: Context,
        second: String,
        includeDate: Boolean,
        isMaxParam: Boolean = false,
        isDoneParam: Boolean = false,
    ): String {
        val now = Calendar.getInstance()
        val date = Date()
        now.time = date

        try {
            if (second.toInt() == 0)
                return when {
                    isMaxParam -> context.getString(R.string.widget_ui_parameter_max)
                    isDoneParam -> context.getString(R.string.widget_ui_parameter_done)
                    else -> context.getString(R.string.widget_ui_parameter_max)
                }

            if (second.toInt() > 360000 || second.toInt() < -144000)
                return String.format(context.getString(R.string.widget_ui_today),
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE))

            val target: Calendar = Calendar.getInstance().apply {
                this.time = Date()
                this.add(Calendar.SECOND, second.toInt())
            }

            val minute = now.get(Calendar.MINUTE)

            return if (includeDate || now.get(Calendar.DATE) != target.get(Calendar.DATE)) {
                log.e()
                String.format(context.getString(R.string.widget_ui_date),
                    getDayWithMonthSuffix(context, target.get(Calendar.DATE)),
                    target.get(Calendar.HOUR_OF_DAY),
                    minute)
            } else String.format(context.getString(R.string.widget_ui_today),
                target.get(Calendar.HOUR_OF_DAY),
                minute)
        } catch (e: NumberFormatException) {
            return when {
                isMaxParam -> context.getString(R.string.widget_ui_parameter_max)
                isDoneParam -> context.getString(R.string.widget_ui_parameter_done)
                else -> context.getString(R.string.widget_ui_parameter_max)
            }
        }
    }

    fun transformerTimeToSecond(time: TransformerTime): String {
        return (time.Day*86400 +
                time.Hour*3600 +
                time.Minute*60 +
                time.Second).toString()
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

    fun sendNotification(
        notiType: Constant.NotiType,
        context: Context,
        title: String,
        msg: String
    ) {
        log.e()
        val notificationId: String
        val notificationDesc: String
        val priority: Int

        when (notiType) {
            Constant.NotiType.RESIN_EACH_40,
            Constant.NotiType.RESIN_140,
            Constant.NotiType.RESIN_CUSTOM
            -> {
                notificationId = Constant.PUSH_CHANNEL_RESIN_NOTI_ID
                notificationDesc = context.getString(R.string.push_resin_noti_description)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
            Constant.NotiType.CHECK_IN_GENSHIN_SUCCESS,
            Constant.NotiType.CHECK_IN_GENSHIN_FAILED,
            Constant.NotiType.CHECK_IN_GENSHIN_ALREADY,
            Constant.NotiType.CHECK_IN_GENSHIN_ACCOUNT_NOT_FOUND
            -> {
                notificationId = Constant.PUSH_CHANNEL_GENSHIN_CHECK_IN_NOTI_ID
                notificationDesc = context.getString(R.string.push_genshin_checkin_description)
                priority = NotificationCompat.PRIORITY_LOW
            }
            Constant.NotiType.CHECK_IN_HONKAI_3RD_SUCCESS,
            Constant.NotiType.CHECK_IN_HONKAI_3RD_FAILED,
            Constant.NotiType.CHECK_IN_HONKAI_3RD_ALREADY,
            Constant.NotiType.CHECK_IN_HONKAI_3RD_ACCOUNT_NOT_FOUND
            -> {
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
                NotificationChannel(notificationId,
                    title,
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
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

        if (startCalendar.get(Calendar.HOUR_OF_DAY) >= 1) targetCalendar.add(Calendar.DAY_OF_YEAR,
            1)

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

    fun getExpeditionTime(dailyNote: DailyNoteData): String {
        return try {
            if (dailyNote.expeditions.isNullOrEmpty()) "0"
            else dailyNote.expeditions!!.maxOf { it.remained_time }
        } catch (e: java.lang.Exception) {
            "0"
        }
    }

    fun applyWidgetTheme(
        widgetDesign: ResinWidgetDesignSettings,
        context: Context,
        view: RemoteViews,
    ) {
        val bgColor: Int = if (widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_LIGHT) {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.white),
                widgetDesign.backgroundTransparency)
        } else if ((widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.black),
                widgetDesign.backgroundTransparency)
        } else {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.white),
                widgetDesign.backgroundTransparency)
        }

        val mainFontColor: Int =
            if (widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_LIGHT) {
                ContextCompat.getColor(context, R.color.widget_font_main_light)
            } else if ((widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
                ContextCompat.getColor(context, R.color.widget_font_main_dark)
            } else {
                ContextCompat.getColor(context, R.color.widget_font_main_light)
            }

        val subFontColor: Int =
            if (widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_LIGHT) {
                ContextCompat.getColor(context, R.color.widget_font_sub_light)
            } else if ((widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
                ContextCompat.getColor(context, R.color.widget_font_sub_dark)
            } else {
                ContextCompat.getColor(context, R.color.widget_font_sub_light)
            }

        view.setInt(R.id.ll_root, "setBackgroundColor", bgColor)
        view.setInt(R.id.iv_refersh, "setColorFilter", subFontColor)
        view.setTextColor(R.id.tv_sync_time, subFontColor)
        view.setTextColor(R.id.tv_disable, mainFontColor)

        log.e()

        if (view.layoutId == R.layout.widget_resin_fixed) {
            val fontSize = widgetDesign.fontSize
            view.setFloat(R.id.tv_resin, "setTextSize", fontSize.toFloat())
        }

        view.setTextColor(R.id.tv_resin, mainFontColor)
        view.setTextColor(R.id.tv_resin_max, mainFontColor)
        view.setTextColor(R.id.tv_remain_time, mainFontColor)
    }

    fun applyWidgetTheme(
        widgetDesign: DetailWidgetDesignSettings,
        context: Context,
        view: RemoteViews,
    ) {
        val bgColor: Int = if (widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_LIGHT) {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.white),
                widgetDesign.backgroundTransparency)
        } else if ((widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.black),
                widgetDesign.backgroundTransparency)
        } else {
            ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.white),
                widgetDesign.backgroundTransparency)
        }

        val mainFontColor: Int =
            if (widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_LIGHT) {
                ContextCompat.getColor(context, R.color.widget_font_main_light)
            } else if ((widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
                ContextCompat.getColor(context, R.color.widget_font_main_dark)
            } else {
                ContextCompat.getColor(context, R.color.widget_font_main_light)
            }

        val subFontColor: Int =
            if (widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_LIGHT) {
                ContextCompat.getColor(context, R.color.widget_font_sub_light)
            } else if ((widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
                ContextCompat.getColor(context, R.color.widget_font_sub_dark)
            } else {
                ContextCompat.getColor(context, R.color.widget_font_sub_light)
            }

        view.setInt(R.id.ll_root, "setBackgroundColor", bgColor)
        view.setInt(R.id.iv_refersh, "setColorFilter", subFontColor)
        view.setTextColor(R.id.tv_sync_time, subFontColor)
        view.setTextColor(R.id.tv_disable, mainFontColor)


        log.e()
        val fontSize = widgetDesign.fontSize

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
        view.setTextColor(R.id.tv_transformer_title, mainFontColor)
        view.setTextColor(R.id.tv_transformer, mainFontColor)
        view.setTextColor(R.id.tv_transformer_time_title, mainFontColor)
        view.setTextColor(R.id.tv_transformer_time, mainFontColor)
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
        view.setFloat(R.id.tv_transformer_title, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_transformer, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_transformer_time_title, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_transformer_time, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_realm_currency, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_realm_currency_title, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_realm_currency_time, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_realm_currency_time_title, "setTextSize", fontSize.toFloat())
    }

    /*
    * preference 마이그레이션용 임시 함수.
    * 차후 버전(1.1.5)에서 삭제 요망
    * */
    fun migrateSettings(context: Context) {
        log.e()

        PreferenceManager.setT(context, Constant.PREF_WIDGET_SETTINGS,
            DailyNoteSettings(
                PreferenceManager.getInt(context, Constant.PREF_SERVER),
                PreferenceManager.getLong(context, Constant.PREF_AUTO_REFRESH_PERIOD, Constant.PREF_DEFAULT_REFRESH_PERIOD),
                PreferenceManager.getBoolean(context, Constant.PREF_NOTI_EACH_40_RESIN, false),
                PreferenceManager.getBoolean(context, Constant.PREF_NOTI_140_RESIN, false),
                PreferenceManager.getBoolean(context, Constant.PREF_NOTI_CUSTOM_RESIN_BOOLEAN, false),
                PreferenceManager.getInt(context, Constant.PREF_NOTI_CUSTOM_TARGET_RESIN),
                PreferenceManager.getBoolean(context, Constant.PREF_NOTI_EXPEDITION_DONE, false),
                PreferenceManager.getBoolean(context, Constant.PREF_NOTI_HOME_COIN_FULL, false)
            )
        )

        PreferenceManager.setT(context, Constant.PREF_CHECK_IN_SETTINGS,
            CheckInSettings(
                PreferenceManager.getBoolean(context, Constant.PREF_ENABLE_GENSHIN_AUTO_CHECK_IN, false),
                PreferenceManager.getBoolean(context, Constant.PREF_ENABLE_HONKAI_3RD_AUTO_CHECK_IN, false),
                PreferenceManager.getBoolean(context, Constant.PREF_NOTI_CHECK_IN_SUCCESS, true),
                PreferenceManager.getBoolean(context, Constant.PREF_NOTI_CHECK_IN_FAILED, false)
            )
        )

        PreferenceManager.setT(context, Constant.PREF_RESIN_WIDGET_DESIGN_SETTINGS,
            ResinWidgetDesignSettings(
                PreferenceManager.getInt(context, Constant.PREF_WIDGET_THEME),
                PreferenceManager.getInt(context, Constant.PREF_WIDGET_RESIN_TIME_NOTATION),
                PreferenceManager.getInt(context, Constant.PREF_WIDGET_RESIN_IMAGE_VISIBILITY),
                PreferenceManager.getIntDefault(context, Constant.PREF_DEFAULT_WIDGET_RESIN_FONT_SIZE, Constant.PREF_WIDGET_RESIN_FONT_SIZE),
                PreferenceManager.getIntDefault(context, Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY, Constant.PREF_WIDGET_BACKGROUND_TRANSPARENCY)
            )
        )

        PreferenceManager.setT(context, Constant.PREF_DETAIL_WIDGET_DESIGN_SETTINGS,
            DetailWidgetDesignSettings(
                PreferenceManager.getInt(context, Constant.PREF_WIDGET_THEME),
                PreferenceManager.getInt(context, Constant.PREF_WIDGET_DETAIL_TIME_NOTATION),
                PreferenceManager.getBoolean(context, Constant.PREF_WIDGET_RESIN_DATA_VISIBILITY, true),
                PreferenceManager.getBoolean(context, Constant.PREF_WIDGET_DAILY_COMMISSION_DATA_VISIBILITY, true),
                PreferenceManager.getBoolean(context, Constant.PREF_WIDGET_WEEKLY_BOSS_DATA_VISIBILITY, true),
                PreferenceManager.getBoolean(context, Constant.PREF_WIDGET_REALM_CURRENCY_DATA_VISIBILITY, true),
                PreferenceManager.getBoolean(context, Constant.PREF_WIDGET_EXPEDITION_DATA_VISIBILITY, true),
                true,
                PreferenceManager.getIntDefault(context, Constant.PREF_DEFAULT_WIDGET_DETAIL_FONT_SIZE, Constant.PREF_WIDGET_DETAIL_FONT_SIZE),
                PreferenceManager.getIntDefault(context, Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY, Constant.PREF_WIDGET_BACKGROUND_TRANSPARENCY)
            )
        )

    }
}