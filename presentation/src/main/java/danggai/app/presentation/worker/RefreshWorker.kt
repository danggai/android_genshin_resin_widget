package danggai.app.presentation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import danggai.app.presentation.R
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.TimeFunction
import danggai.app.presentation.util.log
import danggai.domain.core.ApiResult
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.usecase.AccountDaoUseCase
import danggai.domain.network.dailynote.entity.DailyNoteData
import danggai.domain.network.dailynote.usecase.DailyNoteUseCase
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.util.Constant
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@HiltWorker
class RefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val preference: PreferenceManagerRepository,
    private val accountDao: AccountDaoUseCase,
    private val dailyNote: DailyNoteUseCase
): Worker(context, workerParams) {

    companion object {
        fun startWorkerOneTime(context: Context) {
            log.e()

            val workManager = WorkManager.getInstance(context)
            val workRequest = OneTimeWorkRequestBuilder<RefreshWorker>()
                .addTag(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH)
                .build()
            workManager.enqueueUniqueWork(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH, ExistingWorkPolicy.REPLACE, workRequest)
        }

        fun startWorkerPeriodic(context: Context) {
            log.e()
            val period = PreferenceManager.getLong(context, Constant.PREF_AUTO_REFRESH_PERIOD, Constant.PREF_DEFAULT_REFRESH_PERIOD)

            val rx: PublishSubject<Boolean> = PublishSubject.create()

            rx.observeOn(Schedulers.io())
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

    private suspend fun refreshDailyNote(
        account: Account,
        server: String,
        ds: String,
    ) {
        return CoroutineScope(Dispatchers.IO).async {
            dailyNote(
                account.genshin_uid,
                server,
                account.cookie,
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
                                updateData(account, it.data.data!!)
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        log.e(it.message)
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, null)
                        CommonFunction.sendBroadcastAllWidgetRefreshUI(applicationContext)
                    }
                    is ApiResult.Error,
                    is ApiResult.Null -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, null, null)
                        CommonFunction.sendBroadcastAllWidgetRefreshUI(applicationContext)
                    }
                }
            }
        }.await()
    }

    private fun updateData(account: Account, dailyNote: DailyNoteData) {
        log.e()

        val prefDailyNote = preference.getDailyNoteData(account.genshin_uid)
        val settings = preference.getDailyNoteSettings()

        val prefResin: Int = prefDailyNote.current_resin
        val nowResin: Int = dailyNote.current_resin
        if (settings.notiEach40Resin) {
            if (200 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(account, Constant.NotiType.RESIN_EACH_40, 200)
            } else if (160 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(account, Constant.NotiType.RESIN_EACH_40, 160)
            } else if (120 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(account, Constant.NotiType.RESIN_EACH_40, 120)
            } else if (80 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(account, Constant.NotiType.RESIN_EACH_40, 80)
            } else if (40 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(account, Constant.NotiType.RESIN_EACH_40, 40)
            }
        }
        if (settings.notiEach40Resin) {
            if (140 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(account, Constant.NotiType.RESIN_140, 140)
            }
        }
        if (settings.notiCustomResin) {
            val targetResin: Int = settings.customResin
            if (targetResin in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(account, Constant.NotiType.RESIN_CUSTOM, targetResin)
            }
        }

        val prefExpeditionTime: Int = try { preference.getStringExpeditionTime(account.genshin_uid).toInt() } catch (e: Exception) { 0 }
        val nowExpeditionTime: Int = CommonFunction.getExpeditionTime(dailyNote).toInt()
        if (settings.notiExpedition) {
            if (1 in (nowExpeditionTime)..prefExpeditionTime
                && dailyNote.expeditions.isNotEmpty()
                && nowExpeditionTime == 0){
                log.e()
                sendNoti(account, Constant.NotiType.EXPEDITION_DONE, 0)
            }
        }

        val prefHomeCoinRecoveryTime: Int = try { prefDailyNote.home_coin_recovery_time.toInt() } catch (e: Exception) { 0 }
        val nowHomeCoinRecoveryTime: Int = try { (dailyNote.home_coin_recovery_time).toInt() } catch (e: Exception) { 0 }
        if (settings.notiHomeCoin) {
            if (1 in (nowHomeCoinRecoveryTime)..prefHomeCoinRecoveryTime
                && dailyNote.max_home_coin != 0
                && nowHomeCoinRecoveryTime == 0){
                log.e()
                sendNoti(account, Constant.NotiType.REALM_CURRENCY_FULL, 0)
            }
        }

        val prefParamTransState: Boolean = try { prefDailyNote.transformer!!.recovery_time.reached } catch (e: Exception) { false }
        val nowParamTransState: Boolean = try { dailyNote.transformer!!.recovery_time.reached } catch (e: Exception) { false }
        if (settings.notiParamTrans) {
            if (!prefParamTransState && nowParamTransState){
                log.e()
                sendNoti(account, Constant.NotiType.PARAMETRIC_TRANSFORMER_REACHED, 0)
            }
        }

        val calendar = Calendar.getInstance()
        val yymmdd = SimpleDateFormat(Constant.DATE_FORMAT_YEAR_MONTH_DATE).format(Date())

        if (settings.notiDailyYet &&
            yymmdd != preference.getStringRecentDailyCommissionNotiDate(account.genshin_uid) &&
            calendar.get(Calendar.HOUR) >= settings.notiDailyYetTime &&
            !dailyNote.is_extra_task_reward_received
        ) {
            log.e()
            preference.setStringRecentDailyCommissionNotiDate(account.genshin_uid, yymmdd)
            sendNoti(account, Constant.NotiType.DAILY_COMMISSION_YET, 0)
        }

        if (settings.notiDailyYet &&
            yymmdd != preference.getStringRecentWeeklyBossNotiDate(account.genshin_uid) &&
            calendar.get(Calendar.HOUR) >= settings.notiWeeklyYetTime &&
            calendar.get(Calendar.DAY_OF_WEEK) == settings.notiWeeklyYetDay &&
            dailyNote.remain_resin_discount_num != 0
        ) {
            log.e()
            preference.setStringRecentWeeklyBossNotiDate(account.genshin_uid, yymmdd)
            sendNoti(account, Constant.NotiType.WEEKLY_BOSS_YET, 0)
        }

        preference.setStringRecentSyncTime(account.genshin_uid, TimeFunction.getSyncDateTimeString())

        val expeditionTime: String = CommonFunction.getExpeditionTime(dailyNote)
        preference.setStringExpeditionTime(account.genshin_uid, expeditionTime)

        preference.setDailyNote(account.genshin_uid, dailyNote)
    }

    private fun sendNoti(account: Account, notiType: Constant.NotiType, target: Int) {
        log.e()

        val title = when (notiType) {
            Constant.NotiType.RESIN_EACH_40,
            Constant.NotiType.RESIN_140,
            Constant.NotiType.RESIN_CUSTOM,-> applicationContext.getString(R.string.push_resin_noti_title)
            Constant.NotiType.EXPEDITION_DONE -> applicationContext.getString(R.string.push_expedition_title)
            Constant.NotiType.REALM_CURRENCY_FULL -> applicationContext.getString(R.string.push_realm_currency_title)
            Constant.NotiType.PARAMETRIC_TRANSFORMER_REACHED -> applicationContext.getString(R.string.push_param_trans_title)
            Constant.NotiType.DAILY_COMMISSION_YET -> applicationContext.getString(R.string.push_daily_commission_title)
            Constant.NotiType.WEEKLY_BOSS_YET -> applicationContext.getString(R.string.push_weekly_boss_title)
            else -> ""
        }

        val msg = when (notiType) {
            Constant.NotiType.RESIN_EACH_40 ->
                when (target) {
                    200 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_200), account.nickname, target)
                    160 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_160), account.nickname, target)
                    120 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_120), account.nickname, target)
                    80 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_40), account.nickname, target)
                    else -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_40), account.nickname, target)
                }
            Constant.NotiType.RESIN_140 -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_over_140), account.nickname, target)
            Constant.NotiType.RESIN_CUSTOM -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_custom), account.nickname, target)
            Constant.NotiType.EXPEDITION_DONE -> String.format(applicationContext.getString(R.string.push_msg_expedition_done), account.nickname)
            Constant.NotiType.REALM_CURRENCY_FULL -> String.format(applicationContext.getString(R.string.push_msg_realm_currency_full), account.nickname)
            Constant.NotiType.PARAMETRIC_TRANSFORMER_REACHED -> String.format(applicationContext.getString(R.string.push_msg_param_trans_full), account.nickname)
            Constant.NotiType.DAILY_COMMISSION_YET -> String.format(applicationContext.getString(R.string.push_msg_daily_commission_yet), account.nickname)
            Constant.NotiType.WEEKLY_BOSS_YET -> String.format(applicationContext.getString(R.string.push_msg_weekly_boss_yet), account.nickname)
            else -> ""
        }

        CommonFunction.sendNotification(notiType, applicationContext, title, msg)
    }

    override fun doWork(): Result {
        log.e()

        try {
            CoroutineScope(Dispatchers.IO).launch {
                delay(300L)     // 마이그레이션 대비용 시간
                accountDao.selectAllAccount().collect { accountList ->
                    accountList.forEach { account ->
                        val server = when (account.server) {
                            Constant.PREF_SERVER_ASIA -> Constant.SERVER_OS_ASIA
                            Constant.PREF_SERVER_EUROPE -> Constant.SERVER_OS_EURO
                            Constant.PREF_SERVER_USA -> Constant.SERVER_OS_USA
                            Constant.PREF_SERVER_CHT -> Constant.SERVER_OS_CHT
                            else -> Constant.SERVER_OS_ASIA
                        }

                        if (account.genshin_uid != "-1")
                            refreshDailyNote(
                                account,
                                server,
                                CommonFunction.getGenshinDS()
                            )
                    }
                }

                CommonFunction.sendBroadcastAllWidgetRefreshUI(applicationContext)
            }

            log.e()
            return Result.success()
        } catch (e: java.lang.Exception) {
            when (e) {
                is UnknownHostException -> log.e("Unknown host!")
                is ConnectException -> log.e("No internet!")
                else -> log.e("Unknown exception!")
            }
            log.e(e.message.toString())

            CommonFunction.sendBroadcastAllWidgetRefreshUI(applicationContext)
            return Result.failure()
        }
    }

}