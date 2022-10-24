package danggai.app.presentation.util

import android.content.Context
import com.google.android.gms.common.internal.Preconditions
import danggai.app.presentation.R
import danggai.domain.network.dailynote.entity.Transformer
import danggai.domain.network.dailynote.entity.TransformerTime
import danggai.domain.util.Constant
import java.text.SimpleDateFormat
import java.util.*

object TimeFunction {
    fun resinSecondToTime(
        context: Context,
        second: String,
        timeNotation: Int
    ): String {
        return when (timeNotation) {
            Constant.PREF_TIME_NOTATION_DEFAULT,
            Constant.PREF_TIME_NOTATION_REMAIN_TIME, -> secondToRemainTime(context, second, timeType = Constant.TIME_TYPE_MAX)
            Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> getSecondsLaterDate(context, second, false, timeType = Constant.TIME_TYPE_MAX)
            else -> ""
        }
    }

    fun realmCurrencySecondToTime(
        context: Context,
        second: String,
        timeNotation: Int
    ): String {
        return when (timeNotation) {
            Constant.PREF_TIME_NOTATION_DEFAULT,
            Constant.PREF_TIME_NOTATION_REMAIN_TIME -> secondToRemainTime(context, second, timeType = Constant.TIME_TYPE_MAX)
            Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> getSecondsLaterDate(context, second, true, timeType = Constant.TIME_TYPE_MAX)
            else -> ""
        }
    }

    fun expeditionSecondToTime(
        context: Context,
        second: String,
        timeNotation: Int
    ): String {
        return when (timeNotation) {
            Constant.PREF_TIME_NOTATION_DEFAULT,
            Constant.PREF_TIME_NOTATION_REMAIN_TIME -> secondToRemainTime(context, second, timeType = Constant.TIME_TYPE_DONE)
            Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> getSecondsLaterDate(context, second, false, timeType = Constant.TIME_TYPE_DONE)
            Constant.PREF_TIME_NOTATION_DISABLE -> {
                if (second == "0") context.getString(R.string.widget_ui_parameter_done)
                else context.getString(R.string.widget_format_under_expedition)
            }
            else -> ""
        }
    }

    fun transformerToTime(
        context: Context,
        transformer: Transformer?,
        timeNotation: Int
    ): String {
        return when (timeNotation) {
            Constant.PREF_TIME_NOTATION_DEFAULT,
            Constant.PREF_TIME_NOTATION_REMAIN_TIME ->  {
                // 1일 이상 남음
                if (transformer != null && transformer.recovery_time.Day > 0)
                    String.format(context.getString(R.string.widget_ui_remain_days), transformer.recovery_time.Day)

                // 1일 이내로 남음
                else if (transformer != null && transformer.recovery_time.Day == 0)
                    secondToRemainTime(context, transformerTimeToSecond(transformer.recovery_time), timeType = Constant.TIME_TYPE_DONE)

                // transformer == null
                else context.getString(R.string.widget_ui_unknown)
            }
            Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> {
                // 1일 이상 남음
                if (transformer != null && transformer.recovery_time.Day > 0)
                    String.format(context.getString(R.string.widget_ui_expect_date), getExpectDate(context, transformer.recovery_time.Day))

                // 1일 이내로 남음
                else if (transformer != null && transformer.recovery_time.Day == 0)
                    getSecondsLaterDate(context, transformerTimeToSecond(transformer.recovery_time), true, timeType = Constant.TIME_TYPE_DONE)

                // transformer == null
                else context.getString(R.string.widget_ui_unknown)
            }
            Constant.PREF_TIME_NOTATION_DISABLE -> {
                if (transformer != null && transformer.recovery_time == TransformerTime.REACHED)
                    context.getString(R.string.widget_ui_transformer_reached)

                else if (transformer != null && transformer.recovery_time != TransformerTime.REACHED)
                    context.getString(R.string.widget_ui_transformer_not_reached)

                // transformer == null
                else context.getString(R.string.widget_ui_unknown)
            }
            else -> ""
        }
    }

    fun secondToRemainTime(
        context: Context,
        second: String,
        timeType: Int = Constant.TIME_TYPE_MAX
    ): String {
        var hour: Int
        var minute: Int

        val timeOverString = when (timeType) {
            Constant.TIME_TYPE_MAX -> context.getString(R.string.widget_ui_parameter_max)
            Constant.TIME_TYPE_DONE -> context.getString(R.string.widget_ui_parameter_done)
            else -> context.getString(R.string.widget_format_max_time)
        }

        try {
            hour = second.toInt() / 3600
            minute = (second.toInt() - hour * 3600) / 60
        } catch (e: Exception) {
            hour = 0
            minute = 0
        }

        return if (second != "0") String.format(context.getString(R.string.widget_ui_remain_time), hour, minute) else timeOverString
    }

    fun getSecondsLaterTime(
        context: Context,
        second: String,
        timeType: Int = Constant.TIME_TYPE_MAX
    ): String {
        val cal = Calendar.getInstance()
        val date = Date()
        cal.time = date

        val format = when (timeType) {
            Constant.TIME_TYPE_MAX -> context.getString(R.string.widget_format_max_time)
            Constant.TIME_TYPE_DONE -> context.getString(R.string.widget_format_done_time)
            else -> context.getString(R.string.widget_format_max_time)
        }

        val timeOverString = when (timeType) {
            Constant.TIME_TYPE_MAX -> context.getString(R.string.widget_ui_parameter_max)
            Constant.TIME_TYPE_DONE -> context.getString(R.string.widget_ui_parameter_done)
            else -> context.getString(R.string.widget_format_max_time)
        }

        try {
            if (second.toInt() == 0)
                return timeOverString

            if (second.toInt() > 144000 || second.toInt() < -144000)
                return String.format(format, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))

            cal.add(Calendar.SECOND, second.toInt())

            val minute = cal.get(Calendar.MINUTE)

            return String.format(format, cal.get(Calendar.HOUR_OF_DAY), minute)
        } catch (e: NumberFormatException) {
            return timeOverString
        }
    }

    fun getSecondsLaterDate(
        context: Context,
        second: String,
        includeDate: Boolean,
        timeType: Int = Constant.TIME_TYPE_MAX
    ): String {
        val now = Calendar.getInstance()
        val date = Date()
        now.time = date

        val timeOverString = when (timeType) {
            Constant.TIME_TYPE_MAX -> context.getString(R.string.widget_ui_parameter_max)
            Constant.TIME_TYPE_DONE -> context.getString(R.string.widget_ui_parameter_done)
            else -> context.getString(R.string.widget_format_max_time)
        }

        try {
            if (second.toInt() == 0)
                return timeOverString

            val target: Calendar = Calendar.getInstance().apply {
                this.time = Date()
                this.add(Calendar.SECOND, second.toInt())
            }

            val minute = target.get(Calendar.MINUTE)

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
            return timeOverString
        }
    }

    fun getSyncTimeString(): String {
        return SimpleDateFormat(Constant.DATE_FORMAT_SYNC_TIME).format(Date())
    }

    fun getSyncDayString(): String {
        return SimpleDateFormat(Constant.DATE_FORMAT_SYNC_DAY).format(Date())
    }

    private fun transformerTimeToSecond(time: TransformerTime): String {
        return (time.Day*86400 +
                time.Hour*3600 +
                time.Minute*60 +
                time.Second).toString()
    }

    private fun getExpectDate(context: Context, days: Int): String {
        val target = Calendar.getInstance().apply {
            this.time = Date()
            this.add(Calendar.DAY_OF_MONTH, days)
        }

        return getDayWithMonthSuffix(context, target.get(Calendar.DAY_OF_MONTH))
    }

    private fun getDayWithMonthSuffix(context: Context, n: Int): String {
        Preconditions.checkArgument(n in 1..31, "illegal day of month: $n")
        return if (n in 11..13) {
            n.toString() + context.getString(R.string.date_th)
        } else when (n % 10) {
            1 -> n.toString() + context.getString(R.string.date_st)
            2 -> n.toString() + context.getString(R.string.date_nd)
            3 -> n.toString() + context.getString(R.string.date_rd)
            else -> n.toString() + context.getString(R.string.date_th)
        }
    }
}