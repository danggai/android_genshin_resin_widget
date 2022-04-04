package danggai.app.resinwidget.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.data.req.ReqDailyNote
import danggai.app.resinwidget.repository.DailyNoteRepository
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit


@HiltWorker
class RefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dailyNoteRepository: DailyNoteRepository
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

    private fun refreshDailyNote(reqDailyNote: ReqDailyNote) {
        CoroutineScope(Dispatchers.IO).launch {
            dailyNoteRepository.dailyNote(
                reqDailyNote,
                onStart = { log.e() },
                onComplete = { log.e() }
            ).collect {
                log.e(it)

                when (it.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        log.e()
                        when (it.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                updateData(it.data.data!!)
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.meta.code, it.data.retcode)
                                CommonFunction.sendBroadcastResinWidgetRefreshUI(applicationContext)
                            }
                        }
                    }
                    Constant.META_CODE_CLIENT_ERROR -> {
                        it.meta.message.let { msg ->
                            CommonFunction.sendBroadcastResinWidgetRefreshUI(applicationContext)
                            log.e(msg)
                        }
                    }
                    else -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.meta.code, null)
                        CommonFunction.sendBroadcastResinWidgetRefreshUI(applicationContext)
                    }
                }
            }
        }
    }

    private fun updateData(dailyNote: DailyNote) {
        log.e()
        val context = applicationContext

        val prefResin: Int = PreferenceManager.getIntCurrentResin(context)
        val nowResin: Int = dailyNote.current_resin
        if (PreferenceManager.getBooleanNotiEach40Resin(context)) {
            if (200 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NOTI_TYPE_EACH_40_RESIN, 200)
            } else if (160 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NOTI_TYPE_EACH_40_RESIN, 160)
            } else if (120 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NOTI_TYPE_EACH_40_RESIN, 120)
            } else if (80 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NOTI_TYPE_EACH_40_RESIN, 80)
            } else if (40 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NOTI_TYPE_EACH_40_RESIN, 40)
            }
        }
        if (PreferenceManager.getBooleanNoti140Resin(context)) {
            if (140 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NOTI_TYPE_140_RESIN, 140)
            }
        }
        if (PreferenceManager.getBooleanNotiCustomResin(context)) {
            val targetResin: Int = PreferenceManager.getIntCustomTargetResin(context)
            if (targetResin in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(Constant.NOTI_TYPE_CUSTOM_RESIN, targetResin)
            }
        }

        val prefExpeditionTime: Int = try { PreferenceManager.getStringExpeditionTime(context).toInt() } catch (e: Exception) { 0 }
        val nowExpeditionTime: Int = CommonFunction.getExpeditionTime(dailyNote).toInt()
        if (PreferenceManager.getBooleanNotiExpeditionDone(context)) {
            if (1 in (nowExpeditionTime)..prefExpeditionTime
                && !dailyNote.expeditions.isNullOrEmpty()
                && nowExpeditionTime == 0){
                log.e()
                sendNoti(Constant.NOTI_TYPE_EXPEDITION_DONE, 0)
            }
        }

        val prefHomeCoinRecoveryTime: Int = try { PreferenceManager.getStringHomeCoinRecoveryTime(context).toInt() } catch (e: Exception) { 0 }
        val nowHomeCoinRecoveryTime: Int = try { (dailyNote.home_coin_recovery_time?:"0").toInt() } catch (e: Exception) { 0 }
        if (PreferenceManager.getBooleanNotiExpeditionDone(context)) {
            if (1 in (nowHomeCoinRecoveryTime)..prefHomeCoinRecoveryTime
                && dailyNote.max_home_coin != 0
                && nowHomeCoinRecoveryTime == 0){
                log.e()
                sendNoti(Constant.NOTI_TYPE_REALM_CURRENCY_FULL, 0)
            }
        }

        CommonFunction.setDailyNoteData(context, dailyNote)

        CommonFunction.sendBroadcastResinWidgetRefreshUI(applicationContext)
    }

    private fun sendNoti(id: Int, target: Int) {
        log.e()

        val title = when (id) {
            Constant.NOTI_TYPE_EACH_40_RESIN,
            Constant.NOTI_TYPE_140_RESIN,
            Constant.NOTI_TYPE_CUSTOM_RESIN -> applicationContext.getString(R.string.push_resin_noti_title)
            Constant.NOTI_TYPE_EXPEDITION_DONE -> applicationContext.getString(R.string.push_expedition_title)
            Constant.NOTI_TYPE_REALM_CURRENCY_FULL -> applicationContext.getString(R.string.push_realm_currency_title)
            else -> ""
        }

        val msg = when (id) {
            Constant.NOTI_TYPE_EACH_40_RESIN ->
                when (target) {
                    200 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_200), target)
                    160 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_160), target)
                    120 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_120), target)
                    80 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_40), target)
                    else -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_40), target)
                }
            Constant.NOTI_TYPE_140_RESIN -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_140), target)
            Constant.NOTI_TYPE_CUSTOM_RESIN -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_custom), target)
            Constant.NOTI_TYPE_EXPEDITION_DONE -> applicationContext.getString(R.string.push_msg_expedition_done)
            Constant.NOTI_TYPE_REALM_CURRENCY_FULL -> applicationContext.getString(R.string.push_msg_realm_currency_full)
            else -> ""
        }

        CommonFunction.sendNotification(id, applicationContext, title, msg)
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
                ReqDailyNote(
                    PreferenceManager.getStringUid(applicationContext),
                    server,
                    PreferenceManager.getStringCookie(applicationContext),
                )
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