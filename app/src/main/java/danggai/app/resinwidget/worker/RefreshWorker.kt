package danggai.app.resinwidget.worker

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import kotlinx.coroutines.coroutineScope

class RefreshWorker (context: Context, workerParams: WorkerParameters, private val api: ApiRepository) :
    CoroutineWorker(context, workerParams) {

    private suspend fun apiDailyNote(uid: String, cookie: String): DailyNote {
        log.e()
        val emptyDailyNote = DailyNote( -1,-1,"",-1, -1, listOf())

        val res = api.suspendDailyNote(uid, cookie)

        log.e(res)
        when (res.meta.code) {
            Constant.META_CODE_SUCCESS -> {
                res.data.data?.let {
                    return it
                }
                return emptyDailyNote
            }
            Constant.META_CODE_BAD_REQUEST,
            Constant.META_CODE_SERVER_ERROR,
            Constant.META_CODE_NOT_FOUND -> {
                log.e()
                return emptyDailyNote
            }
            else -> {
                log.e()
                return emptyDailyNote
            }
        }
    }

    private fun updateData(dailyNote: DailyNote) {
        val context = applicationContext

        PreferenceManager.setIntCurrentResin(context, dailyNote.current_resin)
        PreferenceManager.setIntMaxResin(context, dailyNote.max_resin)
        PreferenceManager.setStringResinRecoveryTime(context, dailyNote.resin_recovery_time?:"-1")
        PreferenceManager.setStringRecentSyncTime(context, CommonFunction.getTimeSyncTimeFormat())

        val appWidgetManager = AppWidgetManager.getInstance(context)

        context.sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())

//        val intent = Intent(context, ResinWidget::class.java)
//        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//        val ids: IntArray = AppWidgetManager.getInstance(getApplication())
//            .getAppWidgetIds(ComponentName(getApplication(), ResinWidget::class.java))
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
    }

    override suspend fun doWork(): Result {
        log.e()
        return try {
            coroutineScope {
                val uid = PreferenceManager.getStringUid(applicationContext)
                val cookie = PreferenceManager.getStringCookie(applicationContext)

                val dailyNote = apiDailyNote(uid, cookie)

                updateData(dailyNote)
            }

            Result.success()
        } catch (e: java.lang.Exception) {
            log.e(e.message.toString())
            Result.failure()
        }
    }

}