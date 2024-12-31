package danggai.app.presentation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.NotificationMapper
import danggai.app.presentation.util.NotificationUtils
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.TimeFunction
import danggai.app.presentation.util.log
import danggai.domain.core.ApiResult
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.usecase.AccountDaoUseCase
import danggai.domain.local.NotiType
import danggai.domain.local.Server
import danggai.domain.network.dailynote.entity.GenshinDailyNoteData
import danggai.domain.network.dailynote.entity.HonkaiSrDataLocal
import danggai.domain.network.dailynote.entity.ZZZDailyNoteData
import danggai.domain.network.dailynote.usecase.DailyNoteUseCase
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.util.Constant
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

@HiltWorker
class RefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val preference: PreferenceManagerRepository,
    private val accountDao: AccountDaoUseCase,
    private val dailyNote: DailyNoteUseCase
) : Worker(context, workerParams) {

    companion object {
        fun startWorkerOneTime(context: Context) {
            log.e()

            val workManager = WorkManager.getInstance(context)
            val workRequest = OneTimeWorkRequestBuilder<RefreshWorker>()
                .addTag(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH)
                .build()
            workManager.enqueueUniqueWork(
                Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun startWorkerPeriodic(context: Context) {
            log.e()
            val period = PreferenceManager.getLong(
                context,
                Constant.PREF_AUTO_REFRESH_PERIOD,
                Constant.PREF_DEFAULT_REFRESH_PERIOD
            )

            val rx: PublishSubject<Boolean> = PublishSubject.create()

            rx.observeOn(Schedulers.io())
                .map {
                    shutdownWorker(context)
                }.subscribe({
                    log.e("period -> $period")

                    val workManager = WorkManager.getInstance(context)
                    val workRequest =
                        PeriodicWorkRequestBuilder<RefreshWorker>(period, TimeUnit.MINUTES)
                            .addTag(Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH)
                            .build()

                    workManager.enqueueUniquePeriodicWork(
                        Constant.WORKER_UNIQUE_NAME_AUTO_REFRESH,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        workRequest
                    )
                }, {}, {}).isDisposed

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
                                CommonFunction.sendCrashlyticsApiLog(
                                    Constant.API_NAME_DAILY_NOTE,
                                    it.code,
                                    it.data.retcode
                                )
                            }
                        }
                    }

                    is ApiResult.Failure -> {
                        log.e(it.message)
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_DAILY_NOTE,
                            it.code,
                            null
                        )
                        CommonFunction.sendBroadcastAllWidgetRefreshUI(applicationContext)
                    }

                    is ApiResult.Error,
                    is ApiResult.Null -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_DAILY_NOTE,
                            null,
                            null
                        )
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
                                    dailyNote = it.data.data
                                )
                            }

                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(
                                    Constant.API_NAME_DAILY_NOTE,
                                    it.code,
                                    it.data.retcode.toString()
                                )
                            }
                        }
                    }

                    is ApiResult.Failure -> {
                        log.e(it.message)
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_DAILY_NOTE,
                            it.code,
                            null
                        )
                        CommonFunction.sendBroadcastAllWidgetRefreshUI(applicationContext)
                    }

                    is ApiResult.Error,
                    is ApiResult.Null -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_DAILY_NOTE,
                            null,
                            null
                        )
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
                                    rogueClearCount = if (currentRecord.has_data) currentRecord.basic.finish_cnt else 0
                                )
                            }

                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(
                                    Constant.API_NAME_DAILY_NOTE,
                                    it.code,
                                    it.data.retcode.toString()
                                )
                            }
                        }
                    }

                    is ApiResult.Failure -> {
                        log.e(it.message)
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_DAILY_NOTE,
                            it.code,
                            null
                        )
                        CommonFunction.sendBroadcastAllWidgetRefreshUI(applicationContext)
                    }

                    is ApiResult.Error,
                    is ApiResult.Null -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_DAILY_NOTE,
                            null,
                            null
                        )
                        CommonFunction.sendBroadcastAllWidgetRefreshUI(applicationContext)
                    }
                }
            }
        }.await()
    }

    private suspend fun refreshZZZDailyNote(
        account: Account,
        server: String,
    ) {
        return CoroutineScope(Dispatchers.IO).async {
            dailyNote.ZZZ(
                account.zzz_uid,
                server,
                account.cookie,
                onStart = { log.e() },
                onComplete = { log.e() }
            ).collect {
                log.e(it)

                when (it) {
                    is ApiResult.Success -> {
                        when (it.data.retcode.toString()) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                updateData(account, it.data.data)
                            }

                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(
                                    Constant.API_NAME_DAILY_NOTE,
                                    it.code,
                                    it.data.retcode.toString()
                                )
                            }
                        }
                    }

                    is ApiResult.Failure -> {
                        log.e(it.message)
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_DAILY_NOTE,
                            it.code,
                            null
                        )
                        CommonFunction.sendBroadcastAllWidgetRefreshUI(applicationContext)
                    }

                    is ApiResult.Error,
                    is ApiResult.Null -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_DAILY_NOTE,
                            null,
                            null
                        )
                        CommonFunction.sendBroadcastAllWidgetRefreshUI(applicationContext)
                    }
                }
            }
        }.await()
    }

    private fun updateData(account: Account, dailyNote: GenshinDailyNoteData) {
        log.e()

        fun sendNotiActions() {
            val prefDailyNote = preference.getGenshinDailyNote(account.genshin_uid)
            val settings = preference.getDailyNoteSettings()

            val prefResin: Int = prefDailyNote.currentResin
            val nowResin: Int = dailyNote.currentResin

            if (settings.notiEach40Resin) {
                val resinLevels = (40 until Constant.MAX_RESIN + 40 step 40).toList().reversed()

                for (resinLevel in resinLevels) {
                    if (resinLevel in (prefResin + 1)..nowResin) {
                        log.e()
                        sendNoti(account, NotiType.Genshin.StaminaEach40, resinLevel)
                        break
                    }
                }
            }

            if (settings.noti140Resin) {
                if (Constant.MAX_RESIN - 20 in (prefResin + 1)..nowResin) {
                    log.e()
                    sendNoti(account, NotiType.Genshin.Stamina180, Constant.MAX_RESIN - 20)
                }
            }

            if (settings.notiCustomResin) {
                val targetResin: Int =
                    settings.customResin.takeUnless { it == 0 } ?: Constant.MAX_RESIN
                if (targetResin in (prefResin + 1)..nowResin) {
                    log.e()
                    sendNoti(account, NotiType.Genshin.StaminaCustom, targetResin)
                }
            }

            val prefExpeditionTime: Int = try {
                preference.getStringExpeditionTime(account.genshin_uid).toInt()
            } catch (e: Exception) {
                0
            }
            val nowExpeditionTime: Int = CommonFunction.getExpeditionTime(dailyNote).toInt()
            if (settings.notiExpedition) {
                if (1 in (nowExpeditionTime)..prefExpeditionTime
                    && dailyNote.expeditions.isNotEmpty()
                    && nowExpeditionTime == 0
                ) {
                    log.e()
                    sendNoti(account, NotiType.Genshin.ExpeditionDone, null)
                }
            }

            val prefHomeCoinRecoveryTime: Int = try {
                prefDailyNote.homeCoinRecoveryTime.toInt()
            } catch (e: Exception) {
                0
            }
            val nowHomeCoinRecoveryTime: Int = try {
                (dailyNote.homeCoinRecoveryTime).toInt()
            } catch (e: Exception) {
                0
            }
            if (settings.notiHomeCoin) {
                if (1 in (nowHomeCoinRecoveryTime)..prefHomeCoinRecoveryTime
                    && dailyNote.maxHomeCoin != 0
                    && nowHomeCoinRecoveryTime == 0
                ) {
                    log.e()
                    sendNoti(account, NotiType.Genshin.RealmCurrencyFull, null)
                }
            }

            val prefParamTransState: Boolean = try {
                prefDailyNote.transformer!!.recoveryTime.reached
            } catch (e: Exception) {
                false
            }
            val nowParamTransState: Boolean = try {
                dailyNote.transformer!!.recoveryTime.reached
            } catch (e: Exception) {
                false
            }
            if (settings.notiParamTrans) {
                if (!prefParamTransState && nowParamTransState) {
                    log.e()
                    sendNoti(account, NotiType.Genshin.ParametricTransformerReached, null)
                }
            }

            val calendar = Calendar.getInstance()
            val yymmdd = SimpleDateFormat(Constant.DATE_FORMAT_YEAR_MONTH_DATE).format(Date())

            if (settings.notiDailyYet &&
                yymmdd != preference.getStringRecentDailyCommissionNotiDate(account.genshin_uid) &&
                calendar.get(Calendar.HOUR) >= settings.notiDailyYetTime &&
                !dailyNote.isExtraTaskRewardReceived
            ) {
                log.e()
                preference.setStringRecentDailyCommissionNotiDate(account.genshin_uid, yymmdd)
                sendNoti(account, NotiType.Genshin.DailyCommissionNotDone, null)
            }

            if (settings.notiWeeklyYet &&
                yymmdd != preference.getStringRecentWeeklyBossNotiDate(account.genshin_uid) &&
                calendar.get(Calendar.HOUR) >= settings.notiWeeklyYetTime &&
                calendar.get(Calendar.DAY_OF_WEEK) == settings.notiWeeklyYetDay &&
                dailyNote.remainResinDiscountNum != 0
            ) {
                log.e()
                preference.setStringRecentWeeklyBossNotiDate(account.genshin_uid, yymmdd)
                sendNoti(account, NotiType.Genshin.WeeklyBossNotDone, null)
            }
        }

        try {
            sendNotiActions()
        } catch (e: NullPointerException) {
            log.e(e.message.toString())
        }

        preference.setStringRecentSyncTime(
            account.genshin_uid,
            TimeFunction.getSyncDateTimeString()
        )

        val expeditionTime: String = CommonFunction.getExpeditionTime(dailyNote)
        preference.setStringExpeditionTime(account.genshin_uid, expeditionTime)

        preference.setGenshinDailyNote(account.genshin_uid, dailyNote)
    }

    private fun updateData(account: Account, data: HonkaiSrDataLocal) {
        log.e()

        fun sendNotiActions() {
            val notiSettings = preference.getDailyNoteSettings()
            val prefData = preference.getHonkaiSrDailyNote(account.honkai_sr_uid)

            val prefStamina: Int = prefData.dailyNote.currentStamina
            val nowStamina: Int = data.dailyNote.currentStamina

            log.e("prefStamina = $prefStamina")
            log.e("nowStamina = $nowStamina")

            if (notiSettings.notiEach40TrailPower) {
                val staminaLevels =
                    (40 until Constant.MAX_TRAILBLAZE_POWER step 40).toList().reversed()

                for (staminaLevel in staminaLevels) {
                    if (staminaLevel in (prefStamina + 1)..nowStamina) {
                        log.e()
                        sendNoti(account, NotiType.StarRail.StaminaEach40, staminaLevel)
                        break
                    }
                }
            }

            if (notiSettings.noti170TrailPower) {
                if (Constant.MAX_TRAILBLAZE_POWER - 10 in (prefStamina + 1)..nowStamina) {
                    log.e()
                    sendNoti(
                        account,
                        NotiType.StarRail.Stamina230,
                        Constant.MAX_TRAILBLAZE_POWER - 10
                    )
                }
            }

            if (notiSettings.notiCustomTrailPower) {
                val targetTrailPower: Int = notiSettings.customTrailPower.takeUnless { it == 0 }
                    ?: (Constant.MAX_TRAILBLAZE_POWER - 20)
                if (targetTrailPower in (prefStamina + 1)..nowStamina) {
                    log.e()
                    sendNoti(account, NotiType.StarRail.StaminaCustom, targetTrailPower)
                }
            }

            val prefExpeditionTime: Int = try {
                preference.getStringHonkaiSrExpeditionTime(account.honkai_sr_uid).toInt()
            } catch (e: Exception) {
                0
            }
            val nowExpeditionTime: Int = CommonFunction.getExpeditionTime(data).toInt()
            if (notiSettings.notiExpeditionHonkaiSr) {
                if (1 in (nowExpeditionTime)..prefExpeditionTime
                    && data.dailyNote.expeditions.isNotEmpty()
                    && nowExpeditionTime == 0
                ) {
                    log.e()
                    sendNoti(account, NotiType.StarRail.ExpeditionDone, null)
                }
            }
        }

        try {
            sendNotiActions()
        } catch (e: NullPointerException) {
            log.e(e.message.toString())
        }

        preference.setStringRecentSyncTime(
            account.honkai_sr_uid,
            TimeFunction.getSyncDateTimeString()
        )

        val expeditionTime: String = CommonFunction.getExpeditionTime(data)
        preference.setStringHonkaiSrExpeditionTime(account.honkai_sr_uid, expeditionTime)

        preference.setHonkaiSrDailyNote(account.honkai_sr_uid, data)
    }

    private fun updateData(account: Account, dailyNote: ZZZDailyNoteData) {
        log.e()

        fun sendNotiActions() {
            val prefDailyNote = preference.getZZZDailyNote(account.zzz_uid)
            val settings = preference.getDailyNoteSettings()

            val prefBattery: Int = prefDailyNote.energy.progress.current
            val currentBattery: Int = dailyNote.energy.progress.current

            val maxEnergy = dailyNote.energy.progress.max

            if (settings.notiEach40Battery) {
                val gap = 40
                val batteryLevels = (gap until maxEnergy + gap step gap).toList().reversed()

                for (batteryLevel in batteryLevels) {
                    if (batteryLevel in (prefBattery + 1)..currentBattery) {
                        log.e()
                        sendNoti(account, NotiType.ZZZ.StaminaEach60, batteryLevel)
                        break
                    }
                }
            }

            if (settings.notiEach60Battery) {
                val gap = 60
                val batteryLevels = (gap until maxEnergy + gap step gap).toList().reversed()

                for (batteryLevel in batteryLevels) {
                    if (batteryLevel in (prefBattery + 1)..currentBattery) {
                        log.e()
                        if (batteryLevel % 120 == 0 && settings.notiEach40Battery) continue
                        sendNoti(account, NotiType.ZZZ.StaminaEach60, batteryLevel)
                        break
                    }
                }
            }

            if (settings.noti230Battery) {
                if (maxEnergy - 10 in (prefBattery + 1)..currentBattery) {
                    log.e()
                    sendNoti(account, NotiType.ZZZ.Stamina230, maxEnergy - 10)
                }
            }

            if (settings.notiCustomBattery) {
                val targetBattery: Int = settings.customBattery.takeUnless { it == 0 } ?: maxEnergy
                if (targetBattery in (prefBattery + 1)..currentBattery) {
                    log.e()
                    sendNoti(account, NotiType.ZZZ.StaminaCustom, targetBattery)
                }
            }

            // 일퀘알림
//        val calendar = Calendar.getInstance()
//        val yymmdd = SimpleDateFormat(Constant.DATE_FORMAT_YEAR_MONTH_DATE).format(Date())

//        if (settings.notiDailyYet &&
//            yymmdd != preference.getStringRecentDailyCommissionNotiDate(account.zzz_uid) &&
//            calendar.get(Calendar.HOUR) >= settings.notiDailyYetTime &&
//            !dailyNote.is_extra_task_reward_received
//        ) {
//            log.e()
//            preference.setStringRecentDailyCommissionNotiDate(account.zzz_uid, yymmdd)
//            sendNoti(account, Constant.NotiType.DAILY_COMMISSION_YET, 0)
//        }
        }

        try {
            sendNotiActions()
        } catch (e: NullPointerException) {
            log.e(e.message.toString())
        }

        preference.setStringRecentSyncTime(account.zzz_uid, TimeFunction.getSyncDateTimeString())

        preference.setZZZDailyNote(account.zzz_uid, dailyNote)
    }

    private fun sendNoti(account: Account, notiType: NotiType, target: Int?) {
        log.e()
        val context = applicationContext

        val title = NotificationMapper.getNotiTitle(context, notiType)
        val nickname = NotificationMapper.getNickname(notiType, account)
        val msg = NotificationMapper.getNotiMsg(context, notiType, nickname, target)

        log.e(notiType)
        log.e(title)
        log.e(msg)

        NotificationUtils.sendNotification(notiType, applicationContext, account, title, msg)
    }

    override fun doWork(): Result {
        log.e()

        try {
            CoroutineScope(Dispatchers.IO).launch {
                delay(300L)     // 마이그레이션 대비용 시간
                accountDao.selectAllAccount().collect { accountList ->
                    accountList.forEach { account ->

                        if (!account.genshin_uid.contains("-")) {
                            val server = when (Server.fromValue(account.server)) {
                                Server.ASIA -> Constant.SERVER_OS_ASIA
                                Server.EUROPE -> Constant.SERVER_OS_EURO
                                Server.USA -> Constant.SERVER_OS_USA
                                Server.CHT -> Constant.SERVER_OS_CHT
                                else -> Constant.SERVER_OS_ASIA
                            }

                            refreshGenshinDailyNote(
                                account,
                                server,
                                CommonFunction.getGenshinDS()
                            )
                        }

                        if (account.honkai_sr_uid.isNotBlank()) {
                            val server = when (Server.fromValue(account.honkai_sr_server)) {
                                Server.ASIA -> Constant.SERVER_PO_ASIA
                                Server.EUROPE -> Constant.SERVER_PO_EURO
                                Server.USA -> Constant.SERVER_PO_USA
                                Server.CHT -> Constant.SERVER_PO_CHT
                                else -> Constant.SERVER_PO_ASIA
                            }


                            val firstDeferred = async {
                                refreshHonkaiSrDailyNote(
                                    account,
                                    server,
                                    CommonFunction.getGenshinDS()
                                )
                            }

                            val secondDeferred = async {
                                refreshHonkaiSrRogue(
                                    account,
                                    server,
                                    CommonFunction.getGenshinDS()
                                )
                            }

                            awaitAll(firstDeferred, secondDeferred)
                            updateData(account, honkaiSrData)
                        }

                        if (account.zzz_uid.isNotEmpty()) {
                            val server = when (Server.fromValue(account.zzz_server)) {
                                Server.ASIA -> Constant.SERVER_GF_ASIA
                                Server.EUROPE -> Constant.SERVER_GF_EURO
                                Server.USA -> Constant.SERVER_GF_USA
                                Server.CHT -> Constant.SERVER_GF_CHT
                                else -> Constant.SERVER_PO_ASIA
                            }

                            refreshZZZDailyNote(account, server)
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