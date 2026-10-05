package danggai.app.presentation.util

import android.app.Activity
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
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import danggai.domain.local.TalentDate
import danggai.domain.local.TalentDays
import danggai.domain.network.dailynote.entity.GenshinDailyNoteData
import danggai.domain.network.dailynote.entity.HonkaiSrDataLocal
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

    /* 중국 시간 기준 다음 hour시 01분까지 남은 시간(분)을 반환. 이미 지났으면 다음 날로 계산 */
    fun getTimeLeftUntilChinaTime(isAM: Boolean, hour: Int, startCalendar: Calendar): Long {
        val targetCalendar = Calendar.getInstance(TimeZone.getTimeZone(Constant.CHINA_TIMEZONE)).apply {
            timeInMillis = startCalendar.timeInMillis
            set(Calendar.HOUR, hour)
            set(Calendar.AM_PM, if (isAM) Calendar.AM else Calendar.PM)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (targetCalendar.timeInMillis <= startCalendar.timeInMillis)
            targetCalendar.add(Calendar.DAY_OF_YEAR, 1)

        val delay = (targetCalendar.timeInMillis - startCalendar.timeInMillis) / 60000

        log.e("now time -> ${startCalendar.time}")
        log.e("target time -> ${targetCalendar.time}")
        log.e("delayed -> ${delay / 60}h ${delay % 60}m")

        return delay
    }

    fun getChinaDate(): String {
        val format = SimpleDateFormat(Constant.DATE_FORMAT_YEAR_MONTH_DATE)
        format.timeZone = TimeZone.getTimeZone(Constant.CHINA_TIMEZONE)
        return format.format(Date())
    }

    /* 중국 기준, 실제 시간보다 4시간 전 요일을 반환 함. */
    fun getDateInGenshin(): Int {
        val targetCalendar = Calendar.getInstance()
        targetCalendar.timeZone = TimeZone.getTimeZone(Constant.CHINA_TIMEZONE)
        targetCalendar.add(Calendar.HOUR, -4)

        return targetCalendar.get(Calendar.DAY_OF_WEEK)     // 1일 2월 3화 4수 5목 6금 7토
    }

    val DETAIL_WIDGET_FIXED_TEXT_IDS = listOf(
        R.id.tv_sync_time,
        R.id.tv_disable,
        R.id.tv_name,
        R.id.tv_uid,
    )

    fun getDetailWidgetTextIds(root: View): List<Int> {
        val textIds = mutableListOf<Int>()
        collectDetailWidgetTextIds(root, textIds)
        return textIds
    }

    private fun collectDetailWidgetTextIds(view: View, textIds: MutableList<Int>) {
        if (view is TextView) {
            val isFixed = view.id in DETAIL_WIDGET_FIXED_TEXT_IDS
            if (view.id != View.NO_ID && !isFixed) {
                textIds.add(view.id)
            }
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                collectDetailWidgetTextIds(view.getChildAt(i), textIds)
            }
        }
    }

    fun setTextColorByIds(root: View, ids: List<Int>, color: Int) {
        for (id in ids) {
            val textView = root.findViewById<TextView>(id)
            if (textView != null) {
                textView.setTextColor(color)
            }
        }
    }

    fun setTextSizeByIds(root: View, ids: List<Int>, size: Float) {
        for (id in ids) {
            val textView = root.findViewById<TextView>(id)
            if (textView != null) {
                textView.textSize = size
            }
        }
    }

    fun getVisibility(isVisible: Boolean): Int {
        return if (isVisible) View.VISIBLE else View.GONE
    }

    fun Context.isDarkMode(): Boolean {
        return resources.configuration.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    /* 문자열 그대로 비교하면 "7200" > "28800" 처럼 사전순이 되므로 숫자로 변환해서 최댓값을 구함 */
    fun getExpeditionTime(dailyNote: GenshinDailyNoteData): String {
        return dailyNote.expeditions
            .maxOfOrNull { it.remainedTime.toIntOrNull() ?: 0 }
            ?.toString() ?: "0"
    }

    fun getExpeditionTime(data: HonkaiSrDataLocal): String {
        return data.dailyNote.expeditions
            .maxOfOrNull { it.remainingTime }
            ?.toString() ?: "0"
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

    fun isUidValidate(widgetId: Int, context: Context): Boolean {
        val uid = PreferenceManager.getString(context, Constant.PREF_UID + "_$widgetId")

        return if (uid == "") { // uid 없이 처음 인입되는 경우
            if (PreferenceManager.getString(context, Constant.PREF_UID) == "") {
                log.e()
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
        } else { // 정상
            log.e("widget($widgetId) uid -> $uid")
            true
        }
    }

    fun isSameDay(date1: Long, date2: Long): Boolean {
        val calendar1 = Calendar.getInstance().apply { timeInMillis = date1 }
        val calendar2 = Calendar.getInstance().apply { timeInMillis = date2 }

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
    }

    fun isTalentAvailableToday(talentDate: TalentDate): Boolean {
        val currentDate = getDateInGenshin()
        return when (talentDate) {
            TalentDate.MON_THU -> currentDate in TalentDays.MON_THU
            TalentDate.TUE_FRI -> currentDate in TalentDays.TUE_FRI
            TalentDate.WED_SAT -> currentDate in TalentDays.WED_SAT
            TalentDate.ALL -> true
        }
    }
}