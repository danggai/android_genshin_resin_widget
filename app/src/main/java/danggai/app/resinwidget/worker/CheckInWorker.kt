package danggai.app.resinwidget.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.log
import java.net.ConnectException
import java.net.UnknownHostException

class CheckInWorker (val context: Context, workerParams: WorkerParameters, private val api: ApiRepository) :
    Worker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        log.e()

        try {
            log.e()
            return Result.success()
        } catch (e: java.lang.Exception) {
            when (e) {
                is UnknownHostException -> log.e("Unknown host!")
                is ConnectException -> log.e("No internet!")
                else -> log.e("Unknown exception!")
            }
            log.e(e.message.toString())
            context.sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())
            return Result.failure()
        }
    }

}