package danggai.app.resinwidget.util

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.worker.RefreshWorker
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.streams.asSequence

object CommonFunction {

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

    fun startOneTimeRefreshWorker(context: Context) {
//        if (!PreferenceManager.getBooleanAutoRefresh(context)) return
        log.e()

        val workManager = WorkManager.getInstance(context)
        val workRequest = OneTimeWorkRequestBuilder<RefreshWorker>()
//            .setInputData(workDataOf(Constant.PREF_CURRENT_RESIN to PreferenceManager.getIntCurrentResin(context)))
//            .setInputData(workDataOf(Constant.PREF_MAX_RESIN to PreferenceManager.getIntMaxResin(context)))
//            .setInputData(workDataOf(Constant.PREF_RESIN_RECOVERY_TIME to PreferenceManager.getStringResinRecoveryTime(context)))
            .build()
        workManager.enqueue(workRequest)
    }

    private fun encodeToMD5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
            .lowercase(Locale.getDefault())
    }

    fun getIntentAppWidgetAllUpdate(): Intent {
        val broadcast= Intent()
        broadcast.action = Constant.ACTION_APPWIDGET_UPDATE
        broadcast.putExtra(Constant.REFRESH_DATA, true)
        broadcast.putExtra(Constant.REFRESH_UI, true)

        return broadcast
    }

    fun getIntentAppWidgetUiUpdate(): Intent {
        val broadcast= Intent()
        broadcast.action = Constant.ACTION_APPWIDGET_UPDATE
        broadcast.putExtra(Constant.REFRESH_DATA, false)
        broadcast.putExtra(Constant.REFRESH_UI, true)

        return broadcast
    }

    fun getTimeSyncTimeFormat(): String {
        return SimpleDateFormat(Constant.DATE_FORMAT_SYNC_TIME).format(Date())
    }

    fun secondToTime(second: String): String {
        val time = second.toInt() / 3600
        val minute = (second.toInt() - time * 3600) / 60
        val second = second.toInt() % 600

        return "${time}시간 ${minute}분 남음"
    }

}