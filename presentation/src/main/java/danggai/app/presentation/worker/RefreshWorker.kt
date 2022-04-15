package danggai.app.presentation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import danggai.app.presentation.R
import danggai.app.presentation.core.util.CommonFunction
import danggai.app.presentation.core.util.PreferenceManager
import danggai.app.presentation.core.util.log
import danggai.domain.base.ApiResult
import danggai.domain.dailynote.entity.DailyNote
import danggai.domain.dailynote.usecase.DailyNoteUseCase
import danggai.domain.util.Constant
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

@HiltWorker
class RefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dailyNote: DailyNoteUseCase
    ): Worker(context, workerParams) {

    companion object {
        fun startWorkerOneTime(context: Context) {
            log.e()

            if (!PreferenceManager.getBooleanIsValidUserData(context)) {
                log.e()
                return
            }

            val workManager = WorkManager.getInstance(context)
            val workRequest = OneTimeWorkRequestBuilder<RefreshWorker>()
                .addTag(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH)
                .build()
            workManager.enqueueUniqueWork(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH, ExistingWorkPolicy.REPLACE, workRequest)
        }

        fun startWorkerPeriodic(context: Context) {
            val period = PreferenceManager.getLongAutoRefreshPeriod(context)

            val rx: PublishSubject<Boolean> = PublishSubject.create()

            rx.observeOn(Schedulers.io())
                .filter {
                    !(PreferenceManager.getLongAutoRefreshPeriod(context) == -1L ||
                            !PreferenceManager.getBooleanIsValidUserData(context))
                }
                .map {
                    shutdownWorker(context)
                }.subscribe({
                    log.e("period -> $period")

                    val workManager = WorkManager.getInstance(context)
                    val workRequest = PeriodicWorkRequestBuilder<RefreshWorker>(period, TimeUnit.MINUTES)
                        .addTag(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH)
                        .build()

                    workManager.enqueueUniquePeriodicWork(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
                },{},{}).isDisposed

            rx.onNext(true)
        }

        fun shutdownWorker(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH)

            log.e()
        }
    }

    private fun refreshDailyNote(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            dailyNote(
                uid,
                server,
                cookie,
                ds,
                onStart = { log.e() },
                onComplete = { log.e() }
            ).collect {
                log.e(it)

                when (it) {
                    is ApiResult.Success -> {
                        log.e()
                        when (it.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                updateData(it.data.data!!)
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                CommonFunction.sendBroadcastResinWidgetRefreshUI(applicationContext)
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        log.e(it.message)
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, null)
                        CommonFunction.sendBroadcastResinWidgetRefreshUI(applicationContext)
                    }
                    is ApiResult.Error,
                    is ApiResult.Null -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, null, null)
                        CommonFunction.sendBroadcastResinWidgetRefreshUI(applicationContext)
                    }
                }
            }
        }
    }

    private fun updateData(dailyNote: DailyNote.Data) {
        log.e()
        val context = applicationContext

        val prefResin: Int = PreferenceManager.getIntCurrentResin(context)
        val nowResin: Int = dailyNote.current_resin
        if (PreferenceManager.getBooleanNotiEach40Resin(context)) {
            if (200 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NotiType.RESIN_EACH_40, 200)
            } else if (160 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NotiType.RESIN_EACH_40, 160)
            } else if (120 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NotiType.RESIN_EACH_40, 120)
            } else if (80 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NotiType.RESIN_EACH_40, 80)
            } else if (40 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NotiType.RESIN_EACH_40, 40)
            }
        }
        if (PreferenceManager.getBooleanNoti140Resin(context)) {
            if (140 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NotiType.RESIN_140, 140)
            }
        }
        if (PreferenceManager.getBooleanNotiCustomResin(context)) {
            val targetResin: Int = PreferenceManager.getIntCustomTargetResin(context)
            if (targetResin in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NotiType.RESIN_CUSTOM, targetResin)
            }
        }

        val prefExpeditionTime: Int = try { PreferenceManager.getStringExpeditionTime(context).toInt() } catch (e: Exception) { 0 }
        val nowExpeditionTime: Int = CommonFunction.getExpeditionTime(dailyNote).toInt()
        if (PreferenceManager.getBooleanNotiExpeditionDone(context)) {
            if (1 in (nowExpeditionTime)..prefExpeditionTime
                && !dailyNote.expeditions.isNullOrEmpty()
                && nowExpeditionTime == 0){
                log.e()
                sendNoti(Constant.NotiType.EXPEDITION_DONE, 0)
            }
        }

        val prefHomeCoinRecoveryTime: Int = try { PreferenceManager.getStringHomeCoinRecoveryTime(context).toInt() } catch (e: Exception) { 0 }
        val nowHomeCoinRecoveryTime: Int = try { (dailyNote.home_coin_recovery_time?:"0").toInt() } catch (e: Exception) { 0 }
        if (PreferenceManager.getBooleanNotiExpeditionDone(context)) {
            if (1 in (nowHomeCoinRecoveryTime)..prefHomeCoinRecoveryTime
                && dailyNote.max_home_coin != 0
                && nowHomeCoinRecoveryTime == 0){
                log.e()
                sendNoti(Constant.NotiType.REALM_CURRENCY_FULL, 0)
            }
        }

        CommonFunction.setDailyNoteData(context, dailyNote)

        CommonFunction.sendBroadcastResinWidgetRefreshUI(applicationContext)
    }

    private fun sendNoti(notiType: Constant.NotiType, target: Int) {
        log.e()

        val title = when (notiType) {
            Constant.NotiType.RESIN_EACH_40,
            Constant.NotiType.RESIN_140,
            Constant.NotiType.RESIN_CUSTOM,-> applicationContext.getString(R.string.push_resin_noti_title)
            Constant.NotiType.EXPEDITION_DONE -> applicationContext.getString(R.string.push_expedition_title)
            Constant.NotiType.REALM_CURRENCY_FULL -> applicationContext.getString(R.string.push_realm_currency_title)
            else -> ""
        }

        val msg = when (notiType) {
            Constant.NotiType.RESIN_EACH_40 ->
                when (target) {
                    200 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_200), target)
                    160 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_160), target)
                    120 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_120), target)
                    80 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_40), target)
                    else -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_40), target)
                }
            Constant.NotiType.RESIN_140 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_140), target)
            Constant.NotiType.RESIN_CUSTOM -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_custom), target)
            Constant.NotiType.EXPEDITION_DONE -> applicationContext.getString(R.string.push_msg_expedition_done)
            Constant.NotiType.REALM_CURRENCY_FULL -> applicationContext.getString(R.string.push_msg_realm_currency_full)
            else -> ""
        }

        CommonFunction.sendNotification(notiType, applicationContext, title, msg)
    }

    override fun doWork(): Result {
        log.e()
        val server =
            when (PreferenceManager.getIntServer(applicationContext)) {
                Constant.PREF_SERVER_ASIA -> Constant.SERVER_OS_ASIA
                Constant.PREF_SERVER_EUROPE -> Constant.SERVER_OS_EURO
                Constant.PREF_SERVER_USA -> Constant.SERVER_OS_USA
                Constant.PREF_SERVER_CHT -> Constant.SERVER_OS_CHT
                else -> Constant.SERVER_OS_ASIA
            }

        try {
            refreshDailyNote(
                PreferenceManager.getStringUid(applicationContext),
                server,
                PreferenceManager.getStringCookie(applicationContext),
                CommonFunction.getGenshinDS()
            )

            log.e()
            return Result.success()
        } catch (e: java.lang.Exception) {
            when (e) {
                is UnknownHostException -> log.e("Unknown host!")
                is ConnectException -> log.e("No internet!")
                else -> log.e("Unknown exception!")
            }
            log.e(e.message.toString())

            CommonFunction.sendBroadcastResinWidgetRefreshUI(applicationContext)
            return Result.failure()
        }
    }

}