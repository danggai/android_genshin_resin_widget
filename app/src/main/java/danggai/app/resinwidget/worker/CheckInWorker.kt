package danggai.app.resinwidget.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.net.ConnectException
import java.net.UnknownHostException

class CheckInWorker (val context: Context, workerParams: WorkerParameters, private val api: ApiRepository) :
    Worker(context, workerParams) {

    companion object {
    }

    private val rxApiCheckIn: PublishSubject<Boolean> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()
    private fun Disposable.addCompositeDisposable() {
        compositeDisposable.add(this)
    }

    init {
        initRx()
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
                                if (PreferenceManager.getBooleanNotiCheckInSuccess(context)) {
                                    log.e()
                                    sendNoti(Constant.NOTI_TYPE_CHECK_IN_SUCCESS)
                                }
                                CommonFunction.startUniquePeriodicCheckInWorker(context, false)
                            }
                            Constant.RETCODE_ERROR_CLAIMED_DAILY_REWARD,
                            Constant.RETCODE_ERROR_CHECKED_INTO_HOYOLAB, -> {
                                log.e()
                                if (PreferenceManager.getBooleanNotiCheckInSuccess(context)) {
                                    log.e()
                                    sendNoti(Constant.NOTI_TYPE_CHECK_IN_ALREADY)
                                }
                                CommonFunction.startUniquePeriodicCheckInWorker(context, false)
                            }
                            else -> {
                                log.e()
                                if (PreferenceManager.getBooleanNotiCheckInFailed(context)) {
                                    log.e()
                                    sendNoti(Constant.NOTI_TYPE_CHECK_IN_FAILED)
                                }
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, res.meta.code, res.data.retcode)
                                CommonFunction.startUniquePeriodicCheckInWorker(context, true)
                            }
                        }
                    }
                    else -> {
                        log.e()
                        if (PreferenceManager.getBooleanNotiCheckInFailed(context)) {
                            log.e()
                            sendNoti(Constant.NOTI_TYPE_CHECK_IN_FAILED)
                        }
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHECK_IN, res.meta.code, null)
                        context.sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())
                        CommonFunction.startUniquePeriodicCheckInWorker(context, true)
                    }
                }
            }, {
                it.message?.let { msg ->
                    initRx()
                    context.sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())
                    log.e(msg)
                }
            })
            .addCompositeDisposable()
    }

    private fun sendNoti(id: Int) {
        log.e()

        if (inputData.getBoolean(Constant.ARG_IS_ONE_TIME, false)) {
            log.e()
            return
        }

        val title = applicationContext.getString(R.string.push_checkin_title)
        val msg = when (id) {
            Constant.NOTI_TYPE_CHECK_IN_SUCCESS -> applicationContext.getString(R.string.push_msg_checkin_success)
            Constant.NOTI_TYPE_CHECK_IN_ALREADY -> applicationContext.getString(R.string.push_msg_checkin_already)
            Constant.NOTI_TYPE_CHECK_IN_FAILED -> applicationContext.getString(R.string.push_msg_checkin_failed)
            else -> ""
        }
        CommonFunction.sendNotification(Constant.NOTI_TYPE_CHECK_IN_SUCCESS, applicationContext, title, msg)
    }

    override fun doWork(): Result {
        log.e()

        try {
            rxApiCheckIn.onNext(true)

            log.e()
            return Result.success()
        } catch (e: java.lang.Exception) {
            when (e) {
                is UnknownHostException -> log.e("Unknown host!")
                is ConnectException -> log.e("No internet!")
                else -> log.e("Unknown exception!")
            }
            log.e(e.message.toString())
            context.sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())
            return Result.failure()
        }
    }

}