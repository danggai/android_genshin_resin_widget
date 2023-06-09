package danggai.app.presentation.util

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.hardware.display.DisplayManagerCompat
import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import danggai.app.presentation.BuildConfig
import danggai.app.presentation.R
import danggai.app.presentation.ui.widget.*
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.network.dailynote.entity.GenshinDailyNoteData
import danggai.domain.network.dailynote.entity.HonkaiSrDailyNoteData
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
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

    fun getRandomNumber(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt(max) + min
    }

    fun sendBroadcastAllWidgetRefreshUI(context: Context) {
        log.e()

        context.apply {
            sendBroadcastAppWidgetUpdate<ResinWidget>(context)
            sendBroadcastAppWidgetUpdate<ResinWidgetResizable>(context)
            sendBroadcastAppWidgetUpdate<DetailWidget>(context)
            sendBroadcastAppWidgetUpdate<MiniWidget>(context)
            sendBroadcastAppWidgetUpdate<TrailPowerWidget>(context)
        }
    }

    inline fun <reified T: AppWidgetProvider> sendBroadcastAppWidgetUpdate(context: Context) {
        val ids = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, T::class.java))

        CoroutineScope(Dispatchers.Main.immediate).launch {
//            ids.onEach { id ->
//                val intent = Intent(context, T::class.java)
//                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//
//                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
//                context.sendBroadcast(intent)
//                delay(100L)
//            }
            val intent = Intent(context, T::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
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
            Constant.NotiType.TRAIL_POWER_EACH_40,
            Constant.NotiType.TRAIL_POWER_170,
            Constant.NotiType.TRAIL_POWER_CUSTOM
            -> {
                notificationId = Constant.PUSH_CHANNEL_TRAIL_POWER_NOTI_ID
                notificationDesc = context.getString(R.string.push_trail_power_noti_description)
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
            Constant.NotiType.CHECK_IN_HONKAI_SR_SUCCESS,
            Constant.NotiType.CHECK_IN_HONKAI_SR_FAILED,
            Constant.NotiType.CHECK_IN_HONKAI_SR_ALREADY,
            Constant.NotiType.CHECK_IN_HONKAI_SR_ACCOUNT_NOT_FOUND
            -> {
                notificationId = Constant.PUSH_CHANNEL_HONKAI_SR_CHECK_IN_NOTI_ID
                notificationDesc = context.getString(R.string.push_honkai_sr_checkin_description)
                priority = NotificationCompat.PRIORITY_LOW
            }
            Constant.NotiType.EXPEDITION_DONE -> {
                notificationId = Constant.PUSH_CHANNEL_EXPEDITION_NOTI_ID
                notificationDesc = context.getString(R.string.push_expedition_description_genshin)
                priority = NotificationCompat.PRIORITY_LOW
            }
            Constant.NotiType.HONKAI_SR_EXPEDITION_DONE -> {
                notificationId = Constant.PUSH_CHANNEL_EXPEDITION_NOTI_ID
                notificationDesc = context.getString(R.string.push_expedition_description_honkai_sr)
                priority = NotificationCompat.PRIORITY_LOW
            }
            Constant.NotiType.REALM_CURRENCY_FULL -> {
                notificationId = Constant.PUSH_CHANNEL_REALM_CURRENCY_NOTI_ID
                notificationDesc = context.getString(R.string.push_realm_currency_description)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
            Constant.NotiType.PARAMETRIC_TRANSFORMER_REACHED -> {
                notificationId = Constant.PUSH_CHANNEL_PARAMETRIC_TRANSFORMER_NOTI_ID
                notificationDesc = context.getString(R.string.push_param_trans_description)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
            Constant.NotiType.DAILY_COMMISSION_YET -> {
                notificationId = Constant.PUSH_CHANNEL_DAILY_COMMISSION_YET_NOTI_ID
                notificationDesc = context.getString(R.string.push_daily_commission_description)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
            Constant.NotiType.WEEKLY_BOSS_YET -> {
                notificationId = Constant.PUSH_CHANNEL_WEEKLY_BOSS_YET_NOTI_ID
                notificationDesc = context.getString(R.string.push_weekly_boss_description)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
            else -> {
                notificationId = Constant.PUSH_CHANNEL_DEFAULT_ID
                notificationDesc = context.getString(R.string.push_default_noti_description)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
        }

        val icon = when (notiType) {
//            Constant.NotiType.CHECK_IN_HONKAI_3RD_SUCCESS,
//            Constant.NotiType.CHECK_IN_HONKAI_3RD_FAILED,
//            Constant.NotiType.CHECK_IN_HONKAI_3RD_ALREADY,
//            Constant.NotiType.CHECK_IN_HONKAI_3RD_ACCOUNT_NOT_FOUND -> R.d
            Constant.NotiType.CHECK_IN_HONKAI_SR_SUCCESS,
            Constant.NotiType.CHECK_IN_HONKAI_SR_FAILED,
            Constant.NotiType.CHECK_IN_HONKAI_SR_ALREADY,
            Constant.NotiType.CHECK_IN_HONKAI_SR_ACCOUNT_NOT_FOUND,
            Constant.NotiType.TRAIL_POWER_EACH_40,
            Constant.NotiType.TRAIL_POWER_170,
            Constant.NotiType.TRAIL_POWER_CUSTOM,
            Constant.NotiType.HONKAI_SR_EXPEDITION_DONE, -> R.drawable.trailblaze_power
            else -> R.drawable.resin
        }

        val notificationManager: NotificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) ?: return

        val builder = NotificationCompat.Builder(context, notificationId).apply {
            setSmallIcon(icon)
            setContentTitle(title)
            setContentText(msg)
            setAutoCancel(true)
            setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            setPriority(priority)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    notificationId,
                    title,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = notificationDesc
                }
            )
        }

        notificationManager.notify((System.currentTimeMillis()).toInt(), builder.build())
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

    fun getExpeditionTime(dailyNote: GenshinDailyNoteData): String {
        return try {
            if (dailyNote.expeditions.isEmpty()) "0"
            else dailyNote.expeditions.maxOf { it.remained_time }
        } catch (e: java.lang.Exception) {
            "0"
        }
    }

    fun getExpeditionTime(dailyNote: HonkaiSrDailyNoteData): String {
        return try {
            if (dailyNote.expeditions.isEmpty()) "0"
            else dailyNote.expeditions.maxOf { it.remaining_time.toString() }
        } catch (e: java.lang.Exception) {
            "0"
        }
    }

    fun getDisplayMetrics(activity: Activity): DisplayMetrics { // Get the metrics
        val metrics = DisplayMetrics()
        DisplayManagerCompat.getInstance(activity).getDisplay(Display.DEFAULT_DISPLAY)
            ?.getRealMetrics(metrics)
        val heightPixels = metrics.heightPixels
        val widthPixels = metrics.widthPixels
        val densityDpi = metrics.densityDpi
        val density = metrics.density
        val scaledDensity = metrics.scaledDensity
        val xdpi: Float = convertPxToDp(activity, widthPixels)
        val ydpi: Float = convertPxToDp(activity, heightPixels)
        log.e("Screen W x H pixels: $widthPixels x $heightPixels")
        log.e("Screen X(swXXdp) x Y dpi: $xdpi x $ydpi")
        log.e("density = $density  scaledDensity = $scaledDensity  densityDpi = $densityDpi")
        return metrics
    }

    fun convertPxToDp(ctx: Context, px: Int): Float {
        val display = DisplayManagerCompat.getInstance(ctx).getDisplay(Display.DEFAULT_DISPLAY)
        val metrics = DisplayMetrics()
        display?.getRealMetrics(metrics)
        val logicalDensity = metrics.density
        return px / logicalDensity
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

        when (view.layoutId) {
            R.layout.widget_resin_fixed,
            R.layout.widget_trailblaze_power -> {
                val fontSize = widgetDesign.fontSize
                view.setFloat(R.id.tv_resin, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_trail_power, "setTextSize", fontSize.toFloat())
            }
        }

        view.setTextColor(R.id.tv_resin, mainFontColor)
        view.setTextColor(R.id.tv_resin_max, mainFontColor)
        view.setTextColor(R.id.tv_trail_power, mainFontColor)
        view.setTextColor(R.id.tv_trail_power_max, mainFontColor)
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

    private const val widgetHasNoUid = "nouid"
    fun isUidValidate(widgetId: Int, context: Context): Boolean {
        val uid = PreferenceManager.getString(context, Constant.PREF_UID + "_$widgetId")

        return if (uid == "") { // uid 없이 처음 인입되는 경우
            if (PreferenceManager.getString(context, Constant.PREF_UID) == "") {
                log.e()
                PreferenceManager.setString(
                    context,
                    Constant.PREF_UID + "_$widgetId",
                    widgetHasNoUid
                )
                false
            } else {    // 마이그레이션용
                log.e("uid -> $uid")
                PreferenceManager.setString(
                    context,
                    Constant.PREF_UID + "_$widgetId",
                    PreferenceManager.getString(context, Constant.PREF_UID)
                )
                true
            }
        } else if (uid == widgetHasNoUid) { // 반복적으로 uid 없이 인입되는 경우
            log.e("widget($widgetId) deleted")
            try {
                val a = AppWidgetHost(context, 0)
                a.deleteAppWidgetId(widgetId)
            } catch (e: Exception) {
                log.e()
            }
            false
        } else { // 정상
            log.e("widget($widgetId) uid -> $uid")
            true
        }
    }
}