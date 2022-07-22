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
import danggai.domain.network.dailynote.entity.Transformer
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

    fun getTimeLeftUntilChinaTime(isAM: Boolean, hour: Int, startCalendar: Calendar): Long {
        val targetCalendar = Calendar.getInstance()
        targetCalendar.timeZone = TimeZone.getTimeZone(Constant.CHINA_TIMEZONE)
        targetCalendar.set(Calendar.MINUTE, 1)
        targetCalendar.set(Calendar.HOUR, hour)
        targetCalendar.set(Calendar.AM_PM, if (isAM) Calendar.AM else Calendar.PM)

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

    /* 중국 기준, 실제 시간보다 4시간 전 요일을 반환 함. */
    fun getDateInGenshin(): Int {
        val targetCalendar = Calendar.getInstance()
        targetCalendar.timeZone = TimeZone.getTimeZone(Constant.CHINA_TIMEZONE)
        targetCalendar.add(Calendar.HOUR, -4)

        return targetCalendar.get(Calendar.DAY_OF_WEEK)     // 1일 2월 3화 4수 5목 6금 7토
    }

    fun Context.isDarkMode(): Boolean {
        return resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    fun getExpeditionTime(dailyNote: DailyNoteData): String {
        return try {
            if (dailyNote.expeditions.isNullOrEmpty()) "0"
            else dailyNote.expeditions.maxOf { it.remained_time }
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

        /* Talent Widget 꼽사리ㅎㅎ; */
        view.setTextColor(R.id.tv_no_talent_ingredient, mainFontColor)
        view.setTextColor(R.id.tv_no_selected_characters, mainFontColor)

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
        view.setTextColor(R.id.tv_no_selected_characters, mainFontColor)

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
        view.setTextColor(R.id.tv_expedition_title, mainFontColor)
        view.setTextColor(R.id.tv_expedition_time, mainFontColor)
        view.setTextColor(R.id.tv_transformer_title, mainFontColor)
        view.setTextColor(R.id.tv_transformer, mainFontColor)
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
        view.setFloat(R.id.tv_expedition_title, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_expedition_time, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_transformer_title, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_transformer, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_realm_currency, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_realm_currency_title, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_realm_currency_time, "setTextSize", fontSize.toFloat())
        view.setFloat(R.id.tv_realm_currency_time_title, "setTextSize", fontSize.toFloat())
    }

    fun Int.idToName(context: Context) {
        when (this) {
            10000001 -> context.getString(R.string.kate)
            10000002 -> context.getString(R.string.ayaka)
            10000003 -> context.getString(R.string.jean)
            10000005 -> context.getString(R.string.aither)
            10000006 -> context.getString(R.string.lisa)
            10000007 -> context.getString(R.string.lumine)
            10000014 -> context.getString(R.string.barbara)
            10000015 -> context.getString(R.string.keaya)
            10000016 -> context.getString(R.string.diluc)
            10000020 -> context.getString(R.string.razor)
            10000021 -> context.getString(R.string.amber)
            10000022 -> context.getString(R.string.venti)
            10000023 -> context.getString(R.string.xiangling)
            10000024 -> context.getString(R.string.beidou)
            10000025 -> context.getString(R.string.xingqiu)
            10000026 -> context.getString(R.string.xiao)
            10000027 -> context.getString(R.string.ningguang)
            10000029 -> context.getString(R.string.klee)
            10000030 -> context.getString(R.string.zhongli)
            10000031 -> context.getString(R.string.fischl)
            10000032 -> context.getString(R.string.bennett)
            10000033 -> context.getString(R.string.childe)
            10000034 -> context.getString(R.string.noelle)
            10000035 -> context.getString(R.string.qiqi)
            10000036 -> context.getString(R.string.chongyun)
            10000037 -> context.getString(R.string.ganyu)
            10000038 -> context.getString(R.string.albedo)
            10000039 -> context.getString(R.string.diona)
            10000041 -> context.getString(R.string.mona)
            10000042 -> context.getString(R.string.keqing)
            10000043 -> context.getString(R.string.sucrose)
            10000044 -> context.getString(R.string.xinyan)
            10000045 -> context.getString(R.string.rosaria)
            10000046 -> context.getString(R.string.hutao)
            10000047 -> context.getString(R.string.kazuha)
            10000048 -> context.getString(R.string.yanfei)
            10000049 -> context.getString(R.string.yoimiya)
            10000050 -> context.getString(R.string.thoma)
            10000051 -> context.getString(R.string.eula)
            10000052 -> context.getString(R.string.raiden)
            10000053 -> context.getString(R.string.sayu)
            10000054 -> context.getString(R.string.kokomi)
            10000055 -> context.getString(R.string.gorou)
            10000056 -> context.getString(R.string.sara)
            10000057 -> context.getString(R.string.itto)
            10000058 -> context.getString(R.string.yae)
            10000060 -> context.getString(R.string.yeran)
            10000062 -> context.getString(R.string.aloy)
            10000063 -> context.getString(R.string.shenhe)
            10000064 -> context.getString(R.string.yunjin)
            10000065 -> context.getString(R.string.kuki)
            10000066 -> context.getString(R.string.ayato)
            else -> "?"
        }
    }

    /*
    * preference 마이그레이션용 임시 함수.
    * 차후 버전(1.1.6~7)에서 삭제 요망
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