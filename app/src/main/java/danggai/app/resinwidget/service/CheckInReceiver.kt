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
        fun setAlarm(context: Context, isRetry: Boolean) {
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

            val receiverIntent = Intent(context, CheckInReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, 0)

            val startCalendar = Calendar.getInstance()
            val targetCalendar = Calendar.getInstance()

            if (isRetry) {
                if (startCalendar.get(Calendar.HOUR_OF_DAY) >= 1) targetCalendar.add(Calendar.MINUTE, 15)

                log.e(targetCalendar.time.toString())
            } else {
                targetCalendar.timeZone = TimeZone.getTimeZone(Constant.CHINA_TIMEZONE)
                targetCalendar.set(Calendar.MINUTE, 1)
                targetCalendar.set(Calendar.HOUR, 0)
                targetCalendar.set(Calendar.AM_PM, Calendar.AM)

                if (startCalendar.get(Calendar.HOUR_OF_DAY) >= 1) targetCalendar.add(Calendar.DAY_OF_YEAR, 1)

                log.e(targetCalendar.time.toString())
            }

//            targetCalendar.add(Calendar.MINUTE, 2)

            alarmManager.set(AlarmManager.RTC, targetCalendar.timeInMillis, pendingIntent);
        }

        fun cancelAlarm(context: Context) {
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val receiverIntent = Intent(context, CheckInReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, 0)

            alarmManager.cancel(pendingIntent)
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