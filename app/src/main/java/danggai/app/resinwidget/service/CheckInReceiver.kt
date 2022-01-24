package danggai.app.resinwidget.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.util.log
import java.util.*


class CheckInReceiver : BroadcastReceiver() {

    companion object {
        fun cancelAlarm(context: Context) {
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

            val receiverIntent = Intent(context, CheckInReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT)

            alarmManager.cancel(pendingIntent)

            if (PendingIntent.getBroadcast(context, 0, Intent(context, CheckInReceiver::class.java), PendingIntent.FLAG_NO_CREATE) != null) { log.d("Alarm is already active") } else { log.d("Alarm is inactive") }

        }

        fun setAlarm(context: Context, isRetry: Boolean) {
            if (PendingIntent.getBroadcast(context, 0, Intent(context, CheckInReceiver::class.java), PendingIntent.FLAG_NO_CREATE) != null) { log.d("Alarm is already active") } else { log.d("Alarm is inactive") }

            cancelAlarm(context)

            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

            if (PendingIntent.getBroadcast(context, 0, Intent(context, CheckInReceiver::class.java), PendingIntent.FLAG_NO_CREATE) != null) { log.d("Alarm is already active") } else { log.d("Alarm is inactive") }

            val receiverIntent = Intent(context, CheckInReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, 0)

            val startCalendar = Calendar.getInstance()
            val targetCalendar = Calendar.getInstance()

            if (isRetry) {
                if (startCalendar.get(Calendar.HOUR_OF_DAY) >= 1) targetCalendar.add(Calendar.MINUTE, 30)

                log.e(targetCalendar.time.toString())
            } else {
                targetCalendar.timeZone = TimeZone.getTimeZone(Constant.CHINA_TIMEZONE)
                targetCalendar.set(Calendar.MINUTE, 1)
                targetCalendar.set(Calendar.HOUR, 0)
                targetCalendar.set(Calendar.AM_PM, Calendar.AM)

                if (startCalendar.get(Calendar.HOUR_OF_DAY) >= 1) targetCalendar.add(Calendar.DAY_OF_YEAR, 1)

                log.e(targetCalendar.time.toString())
            }

            alarmManager.set(AlarmManager.RTC, targetCalendar.timeInMillis, pendingIntent);
//            alarmManager.setRepeating(AlarmManager.RTC, targetCalendar.timeInMillis, 86400000, pendingIntent);

            if (PendingIntent.getBroadcast(context, 0, Intent(context, CheckInReceiver::class.java), PendingIntent.FLAG_NO_CREATE) != null) { log.d("Alarm is already active") } else { log.d("Alarm is inactive") }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        log.e()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, CheckInForegroundService::class.java))
        } else {
            context.startService(Intent(context, CheckInForegroundService::class.java))
        }
    }
}