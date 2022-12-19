package danggai.app.presentation.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import danggai.app.presentation.ui.widget.TalentWidget
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.log
import danggai.domain.util.Constant
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

@HiltWorker
class TalentWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    companion object {

        fun startWorkerOneTimeImmediately(context: Context) {
            log.e()
            startWorkerOneTime(context, 0L)
        }

        fun startWorkerOneTimeAtChina4AM(context: Context) {
            log.e()
            startWorkerOneTime(
                context,
                CommonFunction.getTimeLeftUntilChinaTime(true, 4, Calendar.getInstance())
            )
        }

        private fun startWorkerOneTime(context: Context, delay: Long) {
            log.e("delay -> $delay")

            val workManager = WorkManager.getInstance(context)
            val workRequest = OneTimeWorkRequestBuilder<TalentWorker>()
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .addTag(Constant.WORKER_UNIQUE_NAME_TALENT_WIDGET_REFRESH)
                .build()

            workManager.enqueueUniqueWork(Constant.WORKER_UNIQUE_NAME_TALENT_WIDGET_REFRESH,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun shutdownWorker(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(Constant.WORKER_UNIQUE_NAME_TALENT_WIDGET_REFRESH)

            log.e()
        }
    }

    override suspend fun doWork(): Result {
        return try {
            log.e()
            applicationContext.sendBroadcast(
                Intent(applicationContext, TalentWidget::class.java)
                    .setAction(Constant.ACTION_TALENT_WIDGET_REFRESH)
            )

            startWorkerOneTimeAtChina4AM(applicationContext)

            Result.success()
        } catch (e: java.lang.Exception) {
            when (e) {
                is UnknownHostException -> log.e("Unknown host!")
                is ConnectException -> log.e("No internet!")
                is CancellationException -> log.e("Job cancelled!")
                else -> log.e(e.message.toString())
            }
            Result.failure()
        }
    }
}