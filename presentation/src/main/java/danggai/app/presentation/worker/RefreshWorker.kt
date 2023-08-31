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
import danggai.domain.network.dailynote.entity.GenshinDailyNoteData
import danggai.domain.network.dailynote.entity.HonkaiSrDataLocal
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

    private suspend fun refreshGenshinDailyNote(
        account: Account,
        server: String,
        ds: String,
    ) {
        return CoroutineScope(Dispatchers.IO).async {
            dailyNote.genshin(
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


    var honkaiSrData = HonkaiSrDataLocal.EMPTY

    private suspend fun refreshHonkaiSrDailyNote(
        account: Account,
        server: String,
        ds: String,
    ) {
        return CoroutineScope(Dispatchers.IO).async {
            dailyNote.honkaiSr(
                account.honkai_sr_uid,
                server,
                account.cookie,
                ds,
                onStart = { log.e() },
                onComplete = { log.e() }
            ).collect {
                log.e(it)

                when (it) {
                    is ApiResult.Success -> {
                        when (it.data.retcode.toString()) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                honkaiSrData = honkaiSrData.copy(
                                    accepted_epedition_num = it.data.data.accepted_epedition_num,
                                    current_stamina = it.data.data.current_stamina,
                                    expeditions = it.data.data.expeditions,
                                    max_stamina = it.data.data.max_stamina,
                                    stamina_recover_time = it.data.data.stamina_recover_time,
                                    total_expedition_num = it.data.data.total_expedition_num,
                                    current_train_score = it.data.data.current_train_score,
                                    max_train_score = it.data.data.max_train_score,
                                    current_rogue_score = it.data.data.current_rogue_score,
                                    max_rogue_score = it.data.data.max_rogue_score,
                                    weekly_cocoon_cnt = it.data.data.weekly_cocoon_cnt,
                                    weekly_cocoon_limit = it.data.data.weekly_cocoon_limit,
                                )
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode.toString())
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

    private suspend fun refreshHonkaiSrRogue(
        account: Account,
        server: String,
        ds: String,
    ) {
        return CoroutineScope(Dispatchers.IO).async {
            dailyNote.rogueHonkaiSr(
                account.honkai_sr_uid,
                server,
                account.cookie,
                ds,
                onStart = { log.e() },
                onComplete = { log.e() }
            ).collect {
                log.e(it)

                when (it) {
                    is ApiResult.Success -> {
                        when (it.data.retcode.toString()) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                val currentRecord = it.data.data.current_record
                                honkaiSrData = honkaiSrData.copy(
                                    rogue_clear_count = if (currentRecord.has_data) currentRecord.basic.finish_cnt else 0
                                )
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode.toString())
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

    private fun updateData(account: Account, dailyNote: GenshinDailyNoteData) {
        log.e()

        val prefDailyNote = preference.getGenshinDailyNote(account.genshin_uid)
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

        if (settings.noti140Resin) {
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

        if (settings.notiWeeklyYet &&
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

        preference.setGenshinDailyNote(account.genshin_uid, dailyNote)
    }

    private fun updateData(account: Account, dailyNote: HonkaiSrDataLocal) {
        log.e()

        val prefDailyNote = preference.getHonkaiSrDailyNote(account.honkai_sr_uid)
        val settings = preference.getDailyNoteSettings()

        val prefStamina: Int = prefDailyNote.current_stamina
        val nowStamina: Int = dailyNote.current_stamina

        if (settings.notiEach40TrailPower) {
            if (180 in (prefStamina + 1)..nowStamina){
                log.e()
                sendNoti(account, Constant.NotiType.TRAIL_POWER_EACH_40, 180)
            } else if (160 in (prefStamina + 1)..nowStamina){
                log.e()
                sendNoti(account, Constant.NotiType.TRAIL_POWER_EACH_40, 160)
            } else if (120 in (prefStamina + 1)..nowStamina){
                log.e()
                sendNoti(account, Constant.NotiType.TRAIL_POWER_EACH_40, 120)
            } else if (80 in (prefStamina + 1)..nowStamina){
                log.e()
                sendNoti(account, Constant.NotiType.TRAIL_POWER_EACH_40, 80)
            } else if (40 in (prefStamina + 1)..nowStamina){
                log.e()
                sendNoti(account, Constant.NotiType.TRAIL_POWER_EACH_40, 40)
            }
        }

        if (settings.noti170TrailPower) {
            if (230 in (prefStamina + 1)..nowStamina){
                log.e()
                sendNoti(account, Constant.NotiType.TRAIL_POWER_230, 230)
            }
        }

        if (settings.notiCustomTrailPower) {
            val targetTrailPower: Int = settings.customTrailPower
            if (targetTrailPower in (prefStamina + 1)..nowStamina){
                log.e()
                sendNoti(account, Constant.NotiType.TRAIL_POWER_CUSTOM, targetTrailPower)
            }
        }

        val prefExpeditionTime: Int = try { preference.getStringHonkaiSrExpeditionTime(account.honkai_sr_uid).toInt() } catch (e: Exception) { 0 }
        val nowExpeditionTime: Int = CommonFunction.getExpeditionTime(dailyNote).toInt()
        if (settings.notiExpeditionHonkaiSr) {
            if (1 in (nowExpeditionTime)..prefExpeditionTime
                && dailyNote.expeditions.isNotEmpty()
                && nowExpeditionTime == 0) {
                log.e()
                sendNoti(account, Constant.NotiType.HONKAI_SR_EXPEDITION_DONE, 0)
            }
        }

        preference.setStringRecentSyncTime(account.honkai_sr_uid, TimeFunction.getSyncDateTimeString())

        val expeditionTime: String = CommonFunction.getExpeditionTime(dailyNote)
        preference.setStringHonkaiSrExpeditionTime(account.honkai_sr_uid, expeditionTime)

        preference.setHonkaiSrDailyNote(account.honkai_sr_uid, dailyNote)
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
            Constant.NotiType.TRAIL_POWER_EACH_40,
            Constant.NotiType.TRAIL_POWER_230,
            Constant.NotiType.TRAIL_POWER_CUSTOM, -> applicationContext.getString(R.string.push_trail_power_noti_title)
            Constant.NotiType.HONKAI_SR_EXPEDITION_DONE -> applicationContext.getString(R.string.push_assignment_title)
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

            Constant.NotiType.TRAIL_POWER_EACH_40 ->
                when (target) {
                    240 -> String.format(applicationContext.getString(R.string.push_msg_trail_power_noti_over_240), account.honkai_sr_nickname, target)
                    40, 80, 120, 160, 200 -> String.format(applicationContext.getString(R.string.push_msg_trail_power_noti_over_40), account.honkai_sr_nickname, target)
                    else -> String.format(applicationContext.getString(R.string.push_msg_trail_power_noti_over_40), account.honkai_sr_nickname, target)
                }
            Constant.NotiType.TRAIL_POWER_230 -> String.format(applicationContext.getString(R.string.push_msg_trail_power_noti_over_230), account.honkai_sr_nickname, target)
            Constant.NotiType.TRAIL_POWER_CUSTOM -> String.format(applicationContext.getString(R.string.push_msg_resin_noti_custom), account.honkai_sr_nickname, target)
            Constant.NotiType.HONKAI_SR_EXPEDITION_DONE -> String.format(applicationContext.getString(R.string.push_msg_assignment_done), account.honkai_sr_nickname)
            else -> ""
        }

        CommonFunction.sendNotification(notiType, applicationContext, account, title, msg)
    }

    override fun doWork(): Result {
        log.e()

        try {
            CoroutineScope(Dispatchers.IO).launch {
                delay(300L)     // 마이그레이션 대비용 시간
                accountDao.selectAllAccount().collect { accountList ->
                    accountList.forEach { account ->

                        if (!account.genshin_uid.contains("-")) {
                            val server = when (account.server) {
                                Constant.PREF_SERVER_ASIA -> Constant.SERVER_OS_ASIA
                                Constant.PREF_SERVER_EUROPE -> Constant.SERVER_OS_EURO
                                Constant.PREF_SERVER_USA -> Constant.SERVER_OS_USA
                                Constant.PREF_SERVER_CHT -> Constant.SERVER_OS_CHT
                                else -> Constant.SERVER_OS_ASIA
                            }

                            refreshGenshinDailyNote(
                                account,
                                server,
                                CommonFunction.getGenshinDS()
                            )
                        }

                        if (account.honkai_sr_uid.isNotBlank()) {
                            val server = when (account.server) {
                                Constant.PREF_SERVER_ASIA -> Constant.SERVER_PO_ASIA
                                Constant.PREF_SERVER_EUROPE -> Constant.SERVER_PO_EURO
                                Constant.PREF_SERVER_USA -> Constant.SERVER_PO_USA
                                Constant.PREF_SERVER_CHT -> Constant.SERVER_PO_CHT
                                else -> Constant.SERVER_PO_ASIA
                            }


                            val firstDeferred = async { refreshHonkaiSrDailyNote(
                                account,
                                server,
                                CommonFunction.getGenshinDS()
                            ) }

                            val secondDeferred = async { refreshHonkaiSrRogue(
                                account,
                                server,
                                CommonFunction.getGenshinDS()
                            ) }

                            awaitAll(firstDeferred, secondDeferred)
                            updateData(account, honkaiSrData)
                        }
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