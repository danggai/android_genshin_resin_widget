package danggai.app.presentation.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import danggai.app.presentation.R
import danggai.app.presentation.ui.main.MainActivity
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.NotificationMapper
import danggai.app.presentation.util.log
import danggai.domain.core.ApiResult
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.usecase.AccountDaoUseCase
import danggai.domain.local.NotiType
import danggai.domain.network.checkin.usecase.CheckInUseCase
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.Calendar
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

@HiltWorker
class CheckInWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val preference: PreferenceManagerRepository,
    private val accountDao: AccountDaoUseCase,
    private val checkIn: CheckInUseCase
) : CoroutineWorker(context, workerParams) {

    companion object {

        fun startWorkerOneTimeImmediately(context: Context) {
            log.e()
            startWorkerOneTime(context, 0L)
        }

        fun startWorkerOneTimeAtChinaMidnight(context: Context) {
            log.e()
            startWorkerOneTime(
                context,
                CommonFunction.getTimeLeftUntilChinaTime(true, 0, Calendar.getInstance())
            )
        }

        fun startWorkerOneTimeRetry(context: Context) {
            log.e()
            startWorkerOneTime(context, 30L)
        }

        private fun startWorkerOneTime(context: Context, delay: Long) {
            log.e()

            log.e("delay -> $delay")

            val workManager = WorkManager.getInstance(context)
            val workRequest = OneTimeWorkRequestBuilder<CheckInWorker>()
                .setInitialDelay(delay, TimeUnit.MINUTES)
//                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag(Constant.WORKER_UNIQUE_NAME_AUTO_CHECK_IN)
                .build()

            workManager.enqueueUniqueWork(
                Constant.WORKER_UNIQUE_NAME_AUTO_CHECK_IN,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        fun shutdownWorker(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(Constant.WORKER_UNIQUE_NAME_AUTO_CHECK_IN)

            log.e()
        }
    }

    private suspend fun checkInGenshin(
        account: Account,
        lang: String,
        actId: String,
        cookie: String
    ) = withContext(Dispatchers.IO) {
        checkIn.genshinImpact(
            lang,
            actId,
            cookie,
            onStart = { log.e() },
            onComplete = { log.e() }
        ).map {
            val settings = preference.getCheckInSettings()

            when (it) {
                is ApiResult.Success -> {
                    when (it.data.retcode) {
                        Constant.RETCODE_SUCCESS,
                        Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                        Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB,
                        -> {
                            log.e()
                            if (settings.notiCheckInSuccess) {
                                log.e()

                                when (it.data.retcode) {
                                    Constant.RETCODE_SUCCESS -> {
                                        when (it.data.data?.gt_result?.is_risk) {
                                            false -> sendNoti(
                                                account,
                                                NotiType.CheckIn._Genshin.Success
                                            )

                                            true -> {
                                                if (it.data.data?.gt_result?.challenge != null)
                                                    checkInGenshinRetry(
                                                        account,
                                                        lang,
                                                        actId,
                                                        cookie,
                                                        it.data.data!!.gt_result!!.challenge
                                                    )
                                                else
                                                    sendNoti(
                                                        account,
                                                        NotiType.CheckIn._Genshin.CaptchaOccured
                                                    )
                                            }

                                            else -> sendNoti(
                                                account,
                                                NotiType.CheckIn._Genshin.Already
                                            )
                                        }
                                    }

                                    Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                                    Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB,
                                    -> sendNoti(account, NotiType.CheckIn._Genshin.Already)
                                }
                            }
                        }

                        Constant.RETCODE_ERROR_ACCOUNT_NOT_FOUND -> {
                            log.e()
                            if (settings.notiCheckInFailed) {
                                log.e()
                                sendNoti(
                                    account,
                                    NotiType.CheckIn.NotFound.AccountGenshin
                                )
                            }
                            disableCheckIn(account, CHECKIN_TYPE_GENSHIN)
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHECK_IN,
                                it.code,
                                it.data.retcode
                            )
                        }

                        else -> {
                            log.e()
                            if (settings.notiCheckInFailed) {
                                log.e()
                                sendNoti(account, NotiType.CheckIn._Genshin.Failed)
                            }
                            startWorkerOneTimeRetry(applicationContext)
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHECK_IN,
                                it.code,
                                it.data.retcode
                            )
                        }
                    }
                }

                is ApiResult.Failure -> {
                    it.message.let { msg ->
                        log.e(msg)
                        if (settings.notiCheckInFailed) {
                            log.e()
                            sendNoti(account, NotiType.CheckIn._Genshin.Failed)
                        }
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_CHECK_IN,
                            it.code,
                            null
                        )
                        startWorkerOneTimeRetry(applicationContext)
                    }
                }

                is ApiResult.Error,
                is ApiResult.Null,
                -> {
                    log.e()
                    if (settings.notiCheckInFailed) {
                        log.e()
                        sendNoti(account, NotiType.CheckIn._Genshin.Failed)
                    }
                    CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, null, null)
                    startWorkerOneTimeRetry(applicationContext)
                }
            }
            it
        }.stateIn(CoroutineScope(Dispatchers.IO))
    }

    private suspend fun checkInGenshinRetry(
        account: Account,
        lang: String,
        actId: String,
        cookie: String,
        challenge: String,
    ) = withContext(Dispatchers.IO) {
        checkIn.genshinImpactRetry(
            lang,
            actId,
            cookie,
            challenge,
            onStart = { log.e() },
            onComplete = { log.e() }
        ).map {
            val settings = preference.getCheckInSettings()

            when (it) {
                is ApiResult.Success -> {
                    when (it.data.retcode) {
                        Constant.RETCODE_SUCCESS,
                        Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                        Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB,
                        -> {
                            log.e()
                            if (settings.notiCheckInSuccess) {
                                log.e()

                                when (it.data.retcode) {
                                    Constant.RETCODE_SUCCESS -> {
                                        when (it.data.data?.gt_result?.is_risk) {
                                            false -> sendNoti(
                                                account,
                                                NotiType.CheckIn._Genshin.Success
                                            )

                                            true -> sendNoti(
                                                account,
                                                NotiType.CheckIn._Genshin.CaptchaOccured
                                            )

                                            else -> sendNoti(
                                                account,
                                                NotiType.CheckIn._Genshin.Already
                                            )
                                        }
                                    }

                                    Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                                    Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB,
                                    -> sendNoti(account, NotiType.CheckIn._Genshin.Already)
                                }
                            }
                        }

                        Constant.RETCODE_ERROR_ACCOUNT_NOT_FOUND -> {
                            log.e()
                            if (settings.notiCheckInFailed) {
                                log.e()
                                sendNoti(
                                    account,
                                    NotiType.CheckIn.NotFound.AccountGenshin
                                )
                            }
                            disableCheckIn(account, CHECKIN_TYPE_GENSHIN)
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHECK_IN,
                                it.code,
                                it.data.retcode
                            )
                        }

                        else -> {
                            log.e()
                            if (settings.notiCheckInFailed) {
                                log.e()
                                sendNoti(account, NotiType.CheckIn._Genshin.Failed)
                            }
                            startWorkerOneTimeRetry(applicationContext)
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHECK_IN,
                                it.code,
                                it.data.retcode
                            )
                        }
                    }
                }

                is ApiResult.Failure -> {
                    it.message.let { msg ->
                        log.e(msg)
                        if (settings.notiCheckInFailed) {
                            log.e()
                            sendNoti(account, NotiType.CheckIn._Genshin.Failed)
                        }
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_CHECK_IN,
                            it.code,
                            null
                        )
                        startWorkerOneTimeRetry(applicationContext)
                    }
                }

                is ApiResult.Error,
                is ApiResult.Null,
                -> {
                    log.e()
                    if (settings.notiCheckInFailed) {
                        log.e()
                        sendNoti(account, NotiType.CheckIn._Genshin.Failed)
                    }
                    CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, null, null)
                    startWorkerOneTimeRetry(applicationContext)
                }
            }
            it
        }.stateIn(CoroutineScope(Dispatchers.IO))
    }

    private suspend fun checkInHonkai3rd(
        account: Account,
        lang: String,
        actId: String,
        cookie: String
    ) = withContext(Dispatchers.IO) {
        checkIn.honkai3rd(
            lang,
            actId,
            cookie,
            onStart = { log.e() },
            onComplete = { log.e() }
        ).map {
            val settings = preference.getCheckInSettings()

            when (it) {
                is ApiResult.Success -> {
                    when (it.data.retcode) {
                        Constant.RETCODE_SUCCESS,
                        Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                        Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB,
                        -> {
                            log.e()
                            if (settings.notiCheckInSuccess) {
                                log.e()

                                when (it.data.retcode) {
                                    Constant.RETCODE_SUCCESS -> sendNoti(
                                        account,
                                        NotiType.CheckIn._Honkai3rd.Success
                                    )

                                    Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                                    Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB,
                                    -> sendNoti(
                                        account,
                                        NotiType.CheckIn._Honkai3rd.Already
                                    )
                                }
                            }

                            log.e()
                        }

                        Constant.RETCODE_ERROR_ACCOUNT_NOT_FOUND -> {
                            log.e()
                            if (settings.notiCheckInFailed) {
                                log.e()
                                sendNoti(
                                    account,
                                    NotiType.CheckIn.NotFound.AccountHonkai3rd
                                )
                            }
                            disableCheckIn(account, CHECKIN_TYPE_HONKAI_3RD)
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHECK_IN,
                                it.code,
                                it.data.retcode
                            )
                        }

                        else -> {
                            log.e()
                            if (settings.notiCheckInFailed) {
                                log.e()
                                sendNoti(account, NotiType.CheckIn._Honkai3rd.Failed)
                            }
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHECK_IN,
                                it.code,
                                it.data.retcode
                            )
                            startWorkerOneTimeRetry(applicationContext)
                        }
                    }
                }

                is ApiResult.Failure -> {
                    it.message.let { msg ->
                        log.e(msg)
                        if (settings.notiCheckInFailed) {
                            log.e()
                            sendNoti(account, NotiType.CheckIn._Honkai3rd.Failed)
                        }
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_CHECK_IN,
                            it.code,
                            null
                        )
                        startWorkerOneTimeRetry(applicationContext)
                    }
                }

                is ApiResult.Error,
                is ApiResult.Null,
                -> {
                    log.e()
                    if (settings.notiCheckInFailed) {
                        log.e()
                        sendNoti(account, NotiType.CheckIn._Honkai3rd.Failed)
                    }
                    CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, null, null)
                    startWorkerOneTimeRetry(applicationContext)
                }
            }
            it
        }.stateIn(CoroutineScope(Dispatchers.IO))
    }

    private suspend fun checkInHonkaiSR(
        account: Account,
        lang: String,
        actId: String,
        cookie: String
    ) = withContext(Dispatchers.IO) {
        checkIn.honkaiSR(
            lang,
            actId,
            cookie,
            onStart = { log.e() },
            onComplete = { log.e() }
        ).map {
            val settings = preference.getCheckInSettings()

            when (it) {
                is ApiResult.Success -> {
                    when (it.data.retcode) {
                        Constant.RETCODE_SUCCESS,
                        Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                        Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB,
                        -> {
                            log.e()
                            if (settings.notiCheckInSuccess) {
                                log.e()

                                when (it.data.retcode) {
                                    Constant.RETCODE_SUCCESS -> sendNoti(
                                        account,
                                        NotiType.CheckIn._StarRail.Success
                                    )

                                    Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                                    Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB,
                                    -> sendNoti(
                                        account,
                                        NotiType.CheckIn._StarRail.Already
                                    )
                                }
                            }

                            log.e()
                        }

                        Constant.RETCODE_ERROR_ACCOUNT_NOT_FOUND -> {
                            log.e()
                            if (settings.notiCheckInFailed) {
                                log.e()
                                sendNoti(
                                    account,
                                    NotiType.CheckIn.NotFound.AccountStarRail
                                )
                            }
                            disableCheckIn(account, CHECKIN_TYPE_HONKAI_SR)
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHECK_IN,
                                it.code,
                                it.data.retcode
                            )
                        }

                        else -> {
                            log.e()
                            if (settings.notiCheckInFailed) {
                                log.e()
                                sendNoti(account, NotiType.CheckIn._StarRail.Failed)
                            }
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHECK_IN,
                                it.code,
                                it.data.retcode
                            )
                            startWorkerOneTimeRetry(applicationContext)
                        }
                    }
                }

                is ApiResult.Failure -> {
                    it.message.let { msg ->
                        log.e(msg)
                        if (settings.notiCheckInFailed) {
                            log.e()
                            sendNoti(account, NotiType.CheckIn._StarRail.Failed)
                        }
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_CHECK_IN,
                            it.code,
                            null
                        )
                        startWorkerOneTimeRetry(applicationContext)
                    }
                }

                is ApiResult.Error,
                is ApiResult.Null,
                -> {
                    log.e()
                    if (settings.notiCheckInFailed) {
                        log.e()
                        sendNoti(account, NotiType.CheckIn._StarRail.Failed)
                    }
                    CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, null, null)
                    startWorkerOneTimeRetry(applicationContext)
                }
            }
            it
        }.stateIn(CoroutineScope(Dispatchers.IO))
    }

    private suspend fun checkInZZZ(
        account: Account,
        lang: String,
        actId: String,
        cookie: String
    ) = withContext(Dispatchers.IO) {
        checkIn.zenlessZoneZero(
            lang,
            actId,
            cookie,
            onStart = { log.e() },
            onComplete = { log.e() }
        ).map {
            val settings = preference.getCheckInSettings()

            when (it) {
                is ApiResult.Success -> {
                    when (it.data.retcode) {
                        Constant.RETCODE_SUCCESS,
                        Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                        Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB,
                        -> {
                            log.e()
                            if (settings.notiCheckInSuccess) {
                                log.e()

                                when (it.data.retcode) {
                                    Constant.RETCODE_SUCCESS -> sendNoti(
                                        account,
                                        NotiType.CheckIn._ZZZ.Success
                                    )

                                    Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                                    Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB,
                                    -> sendNoti(
                                        account,
                                        NotiType.CheckIn._ZZZ.Already
                                    )
                                }
                            }

                            log.e()
                        }

                        Constant.RETCODE_ERROR_ACCOUNT_NOT_FOUND -> {
                            log.e()
                            if (settings.notiCheckInFailed) {
                                log.e()
                                sendNoti(
                                    account,
                                    NotiType.CheckIn.NotFound.AccountZZZ
                                )
                            }
                            disableCheckIn(account, CHECKIN_TYPE_ZZZ)
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHECK_IN,
                                it.code,
                                it.data.retcode
                            )
                        }

                        else -> {
                            log.e()
                            if (settings.notiCheckInFailed) {
                                log.e()
                                sendNoti(account, NotiType.CheckIn._ZZZ.Failed)
                            }
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHECK_IN,
                                it.code,
                                it.data.retcode
                            )
                            startWorkerOneTimeRetry(applicationContext)
                        }
                    }
                }

                is ApiResult.Failure -> {
                    it.message.let { msg ->
                        log.e(msg)
                        if (settings.notiCheckInFailed) {
                            log.e()
                            sendNoti(account, NotiType.CheckIn._ZZZ.Failed)
                        }
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_CHECK_IN,
                            it.code,
                            null
                        )
                        startWorkerOneTimeRetry(applicationContext)
                    }
                }

                is ApiResult.Error,
                is ApiResult.Null,
                -> {
                    log.e()
                    if (settings.notiCheckInFailed) {
                        log.e()
                        sendNoti(account, NotiType.CheckIn._ZZZ.Failed)
                    }
                    CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, null, null)
                    startWorkerOneTimeRetry(applicationContext)
                }
            }
            it
        }.stateIn(CoroutineScope(Dispatchers.IO))
    }

    private fun sendNoti(account: Account, notiType: NotiType.CheckIn) {
        log.e()
        val context = applicationContext

        val title = NotificationMapper.getNotiTitle(context, notiType)
        val nickname = NotificationMapper.getNickname(notiType, account)
        val msg = NotificationMapper.getNotiMsg(context, notiType, nickname, null)

        CommonFunction.sendNotification(notiType, applicationContext, account, title, msg)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
//        val future = SettableFuture.create<ForegroundInfo>()      // use it when this worker is Worker class, not CoroutineWorker

        var mNotificationManager: NotificationManager? = null
        val mNotificationId = 123

        val intentMainLanding = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intentMainLanding,
            PendingIntent.FLAG_IMMUTABLE
        )
        val iconNotification: Bitmap? =
            BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher)

        if (mNotificationManager == null) {
            mNotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    Constant.PUSH_CHANNEL_CHECK_IN_PROGRESS_NOTI_ID,
                    Constant.PUSH_CHANNEL_CHECK_IN_PROGRESS_NOTI_NAME,
                    NotificationManager.IMPORTANCE_MIN
                )
            notificationChannel.enableLights(false)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(
            applicationContext,
            Constant.PUSH_CHANNEL_CHECK_IN_PROGRESS_NOTI_ID
        )
            .setContentTitle(applicationContext.getString(R.string.foreground_check_in_inprogress))
            .setTicker(applicationContext.getString(R.string.foreground_check_in_inprogress))
            .setSmallIcon(R.drawable.resin)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setWhen(0)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)

        if (iconNotification != null) {
            notification.setLargeIcon(Bitmap.createScaledBitmap(iconNotification, 128, 128, false))
        }

//        future.set(ForegroundInfo(mNotificationId, notification.build()))
//        return future

        return ForegroundInfo(mNotificationId, notification.build())
    }


    val CHECKIN_TYPE_GENSHIN = "GENSHIN"
    val CHECKIN_TYPE_HONKAI_3RD = "HONKAI_3RD"
    val CHECKIN_TYPE_HONKAI_SR = "HONKAI_SR"
    val CHECKIN_TYPE_ZZZ = "ZZZ"
    private fun disableCheckIn(account: Account, gameType: String) {
        log.e()
        val _account =
            when (gameType) {
                CHECKIN_TYPE_GENSHIN -> {
                    account.copy(enable_genshin_checkin = false)
                }

                CHECKIN_TYPE_HONKAI_3RD -> {
                    account.copy(enable_honkai3rd_checkin = false)
                }

                CHECKIN_TYPE_HONKAI_SR -> {
                    account.copy(enable_honkai_sr_checkin = false)
                }

                CHECKIN_TYPE_ZZZ -> {
                    account.copy(enable_zzz_checkin = false)
                }

                else -> account
            }

        CoroutineScope(Dispatchers.IO).launch {
            accountDao.insertAccount(_account)
                .collect { log.e(it) }
        }
    }

    override suspend fun doWork(): Result {
        return try {
            log.e()

            val lang = when (preference.getStringLocale()) {
                Constant.Locale.ENGLISH.locale -> Constant.Locale.ENGLISH.lang
                Constant.Locale.KOREAN.locale -> Constant.Locale.KOREAN.lang
                else -> Constant.Locale.ENGLISH.locale
            }

            accountDao.selectAllAccount().collect { accountList ->
                accountList.forEach { account ->
                    if (account.enable_genshin_checkin)
                        checkInGenshin(
                            account = account,
                            lang = lang,
                            actId = Constant.OS_GENSHIN_ACT_ID,
                            cookie = account.cookie,
                        )
                    if (account.enable_honkai3rd_checkin)
                        checkInHonkai3rd(
                            account = account,
                            lang = lang,
                            actId = Constant.OS_HONKAI_3RD_ACT_ID,
                            cookie = account.cookie
                        )
                    if (account.enable_honkai_sr_checkin)
                        checkInHonkaiSR(
                            account = account,
                            lang = lang,
                            actId = Constant.OS_HONKAI_SR_ACT_ID,
                            cookie = account.cookie
                        )
                    if (account.enable_zzz_checkin)
                        checkInZZZ(
                            account = account,
                            lang = lang,
                            actId = Constant.OS_ZZZ_ACT_ID,
                            cookie = account.cookie
                        )
                }

                delay(2500L)
                startWorkerOneTimeAtChinaMidnight(applicationContext)
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
        }
    }
}