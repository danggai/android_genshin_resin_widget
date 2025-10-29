package danggai.app.presentation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.WidgetUtils
import danggai.app.presentation.util.log
import danggai.domain.core.ApiResult
import danggai.domain.network.githubRaw.usecase.GithubRawUseCase
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.Calendar
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

@HiltWorker
class TalentWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val githubRaw: GithubRawUseCase,
    private val preference: PreferenceManagerRepository,
) : CoroutineWorker(context, workerParams) {

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

        fun startWorkerOneTimeRetry(context: Context) {
            log.e()
            startWorkerOneTime(context, 30L)
        }

        private fun startWorkerOneTime(context: Context, delay: Long) {
            log.e("delay -> $delay")

            val workManager = WorkManager.getInstance(context)
            val workRequest = OneTimeWorkRequestBuilder<TalentWorker>()
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .addTag(Constant.WORKER_UNIQUE_NAME_TALENT_WIDGET_REFRESH)
                .build()

            workManager.enqueueUniqueWork(
                Constant.WORKER_UNIQUE_NAME_TALENT_WIDGET_REFRESH,
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

    private suspend fun getRecentGenshinCharactersList() {
        return CoroutineScope(Dispatchers.IO).async {
            githubRaw.recentGenshinCharacters(
                onStart = { log.e() },
                onComplete = { log.e() }
            ).collect {
                when (it) {
                    is ApiResult.Success -> {
                        preference.setRecentCharacetrsList(it.data)
                    }

                    is ApiResult.Failure -> {
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_GITHUB_RECENT_CHARACTERS,
                            it.code,
                            null
                        )
                        startWorkerOneTimeRetry(applicationContext)
                    }

                    is ApiResult.Error,
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_GITHUB_RECENT_CHARACTERS,
                            null,
                            null
                        )
                        startWorkerOneTimeRetry(applicationContext)
                    }
                }
            }
        }.await()
    }

    override suspend fun doWork(): Result {
        val updateIntent = WidgetUtils.getTalentRefreshIntent(applicationContext)

        return try {
            log.e()
            CoroutineScope(Dispatchers.IO).launch {
                val deferred = async { getRecentGenshinCharactersList() }
                awaitAll(deferred)
                startWorkerOneTimeAtChina4AM(applicationContext)
            }
            Result.success()
        } catch (e: java.lang.Exception) {
            when (e) {
                is UnknownHostException -> log.e("Unknown host!")
                is ConnectException -> log.e("No internet!")
                is CancellationException -> log.e("Job cancelled!")
                else -> log.e(e.message.toString())
            }
            Result.failure()
        } finally {
            applicationContext.sendBroadcast(updateIntent)
        }
    }
}