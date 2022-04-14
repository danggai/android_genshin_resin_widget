package danggai.app.presentation.worker

import android.annotation.SuppressLint
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
import androidx.work.*
import androidx.work.impl.utils.futures.SettableFuture
import com.google.common.util.concurrent.ListenableFuture
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import danggai.app.presentation.R
import danggai.app.presentation.core.util.CommonFunction
import danggai.app.presentation.core.util.PreferenceManager
import danggai.app.presentation.core.util.log
import danggai.app.presentation.main.MainActivity
import danggai.domain.base.ApiResult
import danggai.domain.checkin.usecase.CheckInUseCase
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit

@HiltWorker
class CheckInWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val checkInUseCase: CheckInUseCase
): Worker(context, workerParams) {

    companion object {

        fun startWorkerOneTimeImmediately(context: Context) {
            log.e()
            startWorkerOneTime(context, 0L)
        }

        fun startWorkerOneTimeAtChinaMidnight(context: Context) {
            log.e()
            startWorkerOneTime(
                context,
                CommonFunction.getTimeLeftUntilChinaMidnight(Calendar.getInstance())
                )
        }

        fun startWorkerOneTimeRetry(context: Context) {
            log.e()
            startWorkerOneTime(context, 30L)
        }

        private fun startWorkerOneTime(context: Context, delay: Long) {
            log.e()

            if (!PreferenceManager.getBooleanIsValidUserData(context)) {
                log.e()
                return
            }

            log.e("delay -> $delay")

            val workManager = WorkManager.getInstance(context)
            val workRequest = OneTimeWorkRequestBuilder<CheckInWorker>()
                .setInitialDelay(delay, TimeUnit.MINUTES)
//                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag(Constant.WORKER_UNIQUE_NAME_AUTO_CHECK_IN)
                .build()

            workManager.enqueueUniqueWork(Constant.WORKER_UNIQUE_NAME_AUTO_CHECK_IN, ExistingWorkPolicy.REPLACE, workRequest)
        }

        fun shutdownWorker(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(Constant.WORKER_UNIQUE_NAME_AUTO_CHECK_IN)

            log.e()
        }
    }

    private fun checkIn(
        region: String,
        actId: String,
        cookie: String,
        ds: String
    ) = CoroutineScope(Dispatchers.IO).launch {
        checkInUseCase.checkIn(
            region,
            actId,
            cookie,
            ds,
            onStart = { log.e () },
            onComplete = { log.e () }
        ).collect {
            log.e(it)
            when (it) {
                is ApiResult.Success -> {
                    when (it.data.retcode) {
                        Constant.RETCODE_SUCCESS,
                        Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                        Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB, -> {
                            log.e()
                            if (PreferenceManager.getBooleanNotiCheckInSuccess(applicationContext)) {
                                log.e()

                                when (it.data.retcode) {
                                    Constant.RETCODE_SUCCESS -> sendNoti(Constant.NOTI_TYPE_CHECK_IN_SUCCESS)
                                    Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                                    Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB, ->
                                        sendNoti(Constant.NOTI_TYPE_CHECK_IN_ALREADY)
                                }
                            }

                            log.e()
                            startWorkerOneTimeAtChinaMidnight(applicationContext)
                        }
                        else -> {
                            log.e()
                            if (PreferenceManager.getBooleanNotiCheckInFailed(applicationContext)) {
                                log.e()
                                sendNoti(Constant.NOTI_TYPE_CHECK_IN_FAILED)
                            }
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, it.code, it.data.retcode)
                            startWorkerOneTimeRetry(applicationContext)
                        }
                    }
                }
                is ApiResult.Failure -> {
                    it.message.let { msg ->
                        log.e(msg)
                        if (PreferenceManager.getBooleanNotiCheckInFailed(applicationContext)) {
                            log.e()
                            sendNoti(Constant.NOTI_TYPE_CHECK_IN_FAILED)
                        }
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, it.code, null)
                        startWorkerOneTimeRetry(applicationContext)
                    }
                }
                is ApiResult.Error,
                is ApiResult.Null -> {
                    log.e()
                    if (PreferenceManager.getBooleanNotiCheckInFailed(applicationContext)) {
                        log.e()
                        sendNoti(Constant.NOTI_TYPE_CHECK_IN_FAILED)
                    }
                    CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, null, null)
                    startWorkerOneTimeRetry(applicationContext)
                }
            }
        }
    }

    private fun sendNoti(id: Int) {
        log.e()

        val title = applicationContext.getString(R.string.push_checkin_title)
        val msg = when (id) {
            Constant.NOTI_TYPE_CHECK_IN_SUCCESS -> applicationContext.getString(R.string.push_msg_checkin_success)
            Constant.NOTI_TYPE_CHECK_IN_ALREADY -> applicationContext.getString(R.string.push_msg_checkin_already)
            Constant.NOTI_TYPE_CHECK_IN_FAILED -> applicationContext.getString(R.string.push_msg_checkin_failed)
            else -> ""
        }

        CommonFunction.sendNotification(Constant.NOTI_TYPE_CHECK_IN_SUCCESS, applicationContext, title, msg)
    }

    @SuppressLint("RestrictedApi")
    override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> {
        val future = SettableFuture.create<ForegroundInfo>()

        var mNotificationManager: NotificationManager? = null
        val mNotificationId = 123

        val intentMainLanding = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intentMainLanding, PendingIntent.FLAG_IMMUTABLE)
        val iconNotification: Bitmap? = BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher)

        if (mNotificationManager == null) {
            mNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(Constant.PUSH_CHANNEL_CHECK_IN_PROGRESS_NOTI_ID, Constant.PUSH_CHANNEL_CHECK_IN_PROGRESS_NOTI_NAME, NotificationManager.IMPORTANCE_MIN)
            notificationChannel.enableLights(false)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

//        val stopIntent = Intent(applicationContext, CheckInForegroundService::class.java)
//        stopIntent.action = CheckInForegroundService.STOP_FOREGROUND
//        val stopPendingIntent = PendingIntent
//            .getService(applicationContext, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, Constant.PUSH_CHANNEL_CHECK_IN_PROGRESS_NOTI_ID)
            .setContentTitle(applicationContext.getString(R.string.foreground_genshin_check_in_progress))
            .setTicker(applicationContext.getString(R.string.foreground_genshin_check_in_progress))
            .setSmallIcon(R.drawable.resin)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setWhen(0)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
//            .addAction(NotificationCompat.Action(0, applicationContext.getString(R.string.force_stop), stopPendingIntent))

        if (iconNotification != null) {
            notification.setLargeIcon(Bitmap.createScaledBitmap(iconNotification, 128, 128, false))
        }

        future.set(ForegroundInfo(mNotificationId, notification.build()))

        return future
    }

    override fun doWork(): Result {
//        coroutineScope {
        log.e()

        return try {
            checkIn(
                region = Constant.SERVER_OS_ASIA,
                actId = Constant.OS_ACT_ID,
                cookie = PreferenceManager.getStringCookie(applicationContext),
                ds = CommonFunction.getGenshinDS()
            )

            log.e()
            Result.success()
        } catch (e: java.lang.Exception) {
            when (e) {
                is UnknownHostException -> log.e("Unknown host!")
                is ConnectException -> log.e("No internet!")
                else -> log.e("Unknown exception!")
            }
            log.e(e.message.toString())

            Result.failure()
        }
    }

}