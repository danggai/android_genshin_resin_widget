package danggai.app.presentation.util

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
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
import danggai.app.presentation.ui.widget.BatteryWidget
import danggai.app.presentation.ui.widget.DetailWidget
import danggai.app.presentation.ui.widget.HKSRDetailWidget
import danggai.app.presentation.ui.widget.MiniWidget
import danggai.app.presentation.ui.widget.ResinWidget
import danggai.app.presentation.ui.widget.ResinWidgetResizable
import danggai.app.presentation.ui.widget.TrailPowerWidget
import danggai.app.presentation.ui.widget.ZZZDetailWidget
import danggai.domain.db.account.entity.Account
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.NotiType
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.network.dailynote.entity.GenshinDailyNoteData
import danggai.domain.network.dailynote.entity.HonkaiSrDataLocal
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Calendar
import java.util.Locale
import java.util.Random
import java.util.TimeZone
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

        sendBroadcastAppWidgetUpdate<ResinWidget>(context)
        sendBroadcastAppWidgetUpdate<ResinWidgetResizable>(context)
        sendBroadcastAppWidgetUpdate<DetailWidget>(context)
        sendBroadcastAppWidgetUpdate<MiniWidget>(context)
        sendBroadcastAppWidgetUpdate<TrailPowerWidget>(context)
        sendBroadcastAppWidgetUpdate<HKSRDetailWidget>(context)
        sendBroadcastAppWidgetUpdate<BatteryWidget>(context)
        sendBroadcastAppWidgetUpdate<ZZZDetailWidget>(context)
    }

    inline fun <reified T : AppWidgetProvider> sendBroadcastAppWidgetUpdate(context: Context) {
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
        notiType: NotiType,
        context: Context,
        account: Account,
        title: String,
        msg: String,
    ) {
        log.e()

        val notificationParams = NotificationMapper.getNotiParams(context, notiType, account)
        val icon = NotificationMapper.getNotiIcon(notiType)

        val notificationManager: NotificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) ?: return

        val builder = NotificationCompat.Builder(context, notificationParams.channelId).apply {
            setSmallIcon(icon)
            setContentTitle(title)
            setContentText(msg)
            setAutoCancel(true)
            setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            setPriority(notificationParams.priority)

            if (notiType == NotiType.CheckIn._Genshin.CaptchaOccured) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://act.hoyolab.com/ys/event/signin-sea-v3/index.html?act_id=e202102251931481&lang=ko-kr")
                )
                val pendingIntent =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                    else PendingIntent.getActivity(context, 0, intent, 0);

                setContentIntent(pendingIntent)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    notificationParams.channelId,
                    title,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = notificationParams.channelDesc
                }
            )
        }

        notificationManager.notify(notificationParams.notificationId, builder.build())
    }

    fun getTimeLeftUntilChinaTime(isAM: Boolean, hour: Int, startCalendar: Calendar): Long {
        val targetCalendar = Calendar.getInstance()
        targetCalendar.timeZone = TimeZone.getTimeZone(Constant.CHINA_TIMEZONE)
        targetCalendar.set(Calendar.MINUTE, 1)
        targetCalendar.set(Calendar.HOUR, hour)
        targetCalendar.set(Calendar.AM_PM, if (isAM) Calendar.AM else Calendar.PM)

        if (startCalendar.get(Calendar.HOUR_OF_DAY) >= 1) targetCalendar.add(
            Calendar.DAY_OF_YEAR,
            1
        )

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

    fun getExpeditionTime(dailyNote: HonkaiSrDataLocal): String {
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

    /**
     * 1 -> 1 time
     *
     * etc. -> 2 times
     **/
    fun convertIntToTimes(int: Int, context: Context): String {
        return when (int) {
            1 -> int.toString() + context.getString(R.string.time)
            else -> int.toString() + context.getString(R.string.times)
        }
    }

    fun applyWidgetTheme(
        widgetDesign: ResinWidgetDesignSettings,
        context: Context,
        view: RemoteViews,
    ) {
        val bgColor: Int = if (widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_LIGHT) {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.white),
                widgetDesign.backgroundTransparency
            )
        } else if ((widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.black),
                widgetDesign.backgroundTransparency
            )
        } else {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.white),
                widgetDesign.backgroundTransparency
            )
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
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.white),
                widgetDesign.backgroundTransparency
            )
        } else if ((widgetDesign.widgetTheme == Constant.PREF_WIDGET_THEME_DARK) || context.isDarkMode()) {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.black),
                widgetDesign.backgroundTransparency
            )
        } else {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.white),
                widgetDesign.backgroundTransparency
            )
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

        val mainFontViews = listOf(
            R.id.tv_disable,
            R.id.tv_no_selected_characters,

            R.id.tv_resin,
            R.id.tv_resin_title,
            R.id.tv_resin_time,
            R.id.tv_resin_time_title,
            R.id.tv_daily_commission,
            R.id.tv_daily_commission_title,
            R.id.tv_weekly_boss,
            R.id.tv_weekly_boss_title,
            R.id.tv_expedition_title,
            R.id.tv_expedition_time,
            R.id.tv_transformer_title,
            R.id.tv_transformer,
            R.id.tv_realm_currency,
            R.id.tv_realm_currency_title,
            R.id.tv_realm_currency_time,
            R.id.tv_realm_currency_time_title,

            R.id.tv_trailblaze_power,
            R.id.tv_trailblaze_power_title,
            R.id.tv_trailblaze_power_time,
            R.id.tv_trailblaze_power_time_title,
            R.id.tv_daily_training,
            R.id.tv_daily_training_title,
            R.id.tv_echo_of_war,
            R.id.tv_echo_of_war_title,
            R.id.tv_assignment_time,
            R.id.tv_assignment_title,
            R.id.tv_simulated_universe,
            R.id.tv_simulated_universe_title,
            R.id.tv_simulated_universe_cleared,
            R.id.tv_simulated_universe_title_cleared,

            R.id.tv_battery,
            R.id.tv_battery_title,
            R.id.tv_battery_time,
            R.id.tv_battery_time_title,
            R.id.tv_engagement_today,
            R.id.tv_engagement_today_title,
            R.id.tv_scratch_card,
            R.id.tv_scratch_card_title,
            R.id.tv_video_store_management,
            R.id.tv_video_store_management_title,
        )

        fun setFontColorAndSize(view: RemoteViews, id: Int, color: Int, size: Float) {
            view.setTextColor(id, color)
            view.setFloat(id, "setTextSize", size)
        }

        val fontSize = widgetDesign.fontSize.toFloat()

        mainFontViews.forEach { id ->
            setFontColorAndSize(view, id, mainFontColor, fontSize)
        }
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