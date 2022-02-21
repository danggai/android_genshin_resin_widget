package danggai.app.resinwidget.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.ui.main.MainActivity
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject

class CheckInForegroundService() : Service() {

    companion object {
        private const val prefix = "danggai.app.resinwidget.service.checkin."
        const val START_FOREGROUND = prefix + "startforeground"
        const val STOP_FOREGROUND = prefix + "stopforeground"

        fun startService(context: Context) {
            val intent = Intent(context, CheckInForegroundService::class.java)
            intent.action = START_FOREGROUND

            if (!PreferenceManager.getBooleanEnableAutoCheckIn(context)) {
                log.e()
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    private val rxApiCheckIn: PublishSubject<Boolean> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()
    private fun Disposable.addCompositeDisposable() {
        compositeDisposable.add(this)
    }

    private val api: ApiRepository by inject()

    init {
        initRx()
    }

    override fun onBind(intent: Intent?): IBinder? {
        log.e()
        return null
    }

    override fun onCreate() {
        super.onCreate()

        rxApiCheckIn.onNext(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            generateForegroundNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START_FOREGROUND -> {
                log.e("Start Foreground intent received")
                generateForegroundNotification()
            }
            STOP_FOREGROUND -> {
                log.e("Stop Foreground intent received")
                stopForegroundService()
            }
        }
        return START_NOT_STICKY
    }

    private fun initRx() {
        rxApiCheckIn
            .observeOn(Schedulers.io())
            .filter { it }
            .switchMap {
                val cookie = PreferenceManager.getStringCookie(applicationContext)

                api.checkIn(Constant.SERVER_OS_ASIA, cookie)
            }
            .subscribe ({ res ->
                log.e(res)
                when (res.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        log.e()
                        when (res.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                if (PreferenceManager.getBooleanNotiCheckInSuccess(applicationContext)) {
                                    log.e()
                                    sendNoti(Constant.NOTI_TYPE_CHECK_IN_SUCCESS)
                                }
                                CheckInReceiver.setAlarmRepeatly(applicationContext)
                            }
                            Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                            Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB, -> {
                                log.e()
                                if (PreferenceManager.getBooleanNotiCheckInSuccess(applicationContext)) {
                                    log.e()
                                    sendNoti(Constant.NOTI_TYPE_CHECK_IN_ALREADY)
                                }
                                CheckInReceiver.setAlarmRepeatly(applicationContext)
                            }
                            else -> {
                                log.e()
                                if (PreferenceManager.getBooleanNotiCheckInFailed(applicationContext)) {
                                    log.e()
                                    sendNoti(Constant.NOTI_TYPE_CHECK_IN_FAILED)
                                }
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, res.meta.code, res.data.retcode)
                                CheckInReceiver.setAlarmOneShot(applicationContext)
                            }
                        }
                    }
                    else -> {
                        log.e()
                        if (PreferenceManager.getBooleanNotiCheckInFailed(applicationContext)) {
                            log.e()
                            sendNoti(Constant.NOTI_TYPE_CHECK_IN_FAILED)
                        }
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, res.meta.code, null)
                        CheckInReceiver.setAlarmOneShot(applicationContext)
                    }
                }
                stopForegroundService()
            }, {
                it.message?.let { msg ->
                    log.e(msg)
                }
                stopForegroundService()
            })
            .addCompositeDisposable()
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

    private var iconNotification: Bitmap? = null
    private var notification: Notification? = null
    var mNotificationManager: NotificationManager? = null
    private val mNotificationId = 123

    private fun generateForegroundNotification() {
        val intentMainLanding = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intentMainLanding, PendingIntent.FLAG_IMMUTABLE)
        iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        if (mNotificationManager == null) {
            mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert(mNotificationManager != null)
            val notificationChannel =
                NotificationChannel(Constant.PUSH_CHANNEL_CHECK_IN_PROGRESS_NOTI_ID, Constant.PUSH_CHANNEL_CHECK_IN_PROGRESS_NOTI_NAME,
                    NotificationManager.IMPORTANCE_MIN)
            notificationChannel.enableLights(false)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
            mNotificationManager?.createNotificationChannel(notificationChannel)
        }

        val stopIntent = Intent(applicationContext, CheckInForegroundService::class.java)
        stopIntent.action = STOP_FOREGROUND
        val stopPendingIntent = PendingIntent
            .getService(applicationContext, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, Constant.PUSH_CHANNEL_CHECK_IN_PROGRESS_NOTI_ID)

        builder.setContentTitle(applicationContext.getString(R.string.foreground_genshin_check_in_progress))
            .setTicker(applicationContext.getString(R.string.foreground_genshin_check_in_progress))
            .setSmallIcon(R.drawable.resin)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setWhen(0)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(NotificationCompat.Action(0, applicationContext.getString(R.string.force_stop), stopPendingIntent))
        if (iconNotification != null) {
            builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
        }
        notification = builder.build()
        startForeground(mNotificationId, notification)
    }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }
}