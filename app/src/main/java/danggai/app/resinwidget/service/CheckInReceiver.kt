package danggai.app.resinwidget.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import java.util.*


class CheckInReceiver : BroadcastReceiver() {

    companion object {
        fun cancelAlarm(context: Context) {
            val receiverIntent = Intent(context, CheckInReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

            alarmManager.cancel(pendingIntent)

            if (PendingIntent.getBroadcast(context, 0, Intent(context, CheckInReceiver::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE) != null) { log.d("Alarm is already active") } else { log.d("Alarm is inactive") }

        }

        fun setAlarmRepeatly(context: Context) {
            val receiverIntent = Intent(context, CheckInReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

            val startCalendar = Calendar.getInstance()
            val targetCalendar = Calendar.getInstance()

            targetCalendar.timeZone = TimeZone.getTimeZone(Constant.CHINA_TIMEZONE)
            targetCalendar.set(Calendar.MINUTE, 1)
            targetCalendar.set(Calendar.HOUR, 0)
            targetCalendar.set(Calendar.AM_PM, Calendar.AM)

            if (startCalendar.get(Calendar.HOUR_OF_DAY) >= 1) targetCalendar.add(Calendar.DAY_OF_YEAR, 1)

            log.e(targetCalendar.time.toString())

            alarmManager.setInexactRepeating(AlarmManager.RTC, targetCalendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)

            if (pendingIntent != null) { log.d("Alarm is already active") } else { log.d("Alarm is inactive") }
        }

        fun setAlarmOneShot(context: Context) {
            val receiverIntent = Intent(context, CheckInReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

            val startCalendar = Calendar.getInstance()
            val targetCalendar = Calendar.getInstance()

            if (startCalendar.get(Calendar.HOUR_OF_DAY) >= 1) targetCalendar.add(Calendar.MINUTE, 30)

            log.e(targetCalendar.time.toString())

            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, targetCalendar.timeInMillis, pendingIntent);

            if (PendingIntent.getBroadcast(context, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE) != null) { log.d("Alarm is already active") } else { log.d("Alarm is inactive") }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (!PreferenceManager.getBooleanEnableAutoCheckIn(context)) return

        if (intent.action.equals("android.intent.action.BOOT_COMPLETED") ||
                intent.action.equals("android.intent.action.LOCKED_BOOT_COMPLETED")) {
            log.e()
            setAlarmRepeatly(context)
        }

        CheckInForegroundService.startService(context)
    }
}