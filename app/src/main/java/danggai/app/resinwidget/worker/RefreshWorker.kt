package danggai.app.resinwidget.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.net.ConnectException
import java.net.UnknownHostException

class RefreshWorker (val context: Context, workerParams: WorkerParameters, private val api: ApiRepository) :
    Worker(context, workerParams) {

    companion object {
        val NOTI_TYPE_EACH_40_RESIN = 0
        val NOTI_TYPE_140_RESIN = 1
        val NOTI_TYPE_CUSTOM_RESIN = 2
    }

    private val rxApiDailyNote: PublishSubject<Boolean> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()
    private fun Disposable.addCompositeDisposable() {
        compositeDisposable.add(this)
    }

    init {
        initRx()
    }

    private fun initRx() {
        rxApiDailyNote
            .observeOn(Schedulers.io())
            .filter { it }
            .switchMap {
                val uid = PreferenceManager.getStringUid(applicationContext)
                val cookie = PreferenceManager.getStringCookie(applicationContext)

                api.dailyNote(uid, cookie)
            }
            .subscribe ({ res ->
                when (res.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        log.e()
                        when (res.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                updateData(res.data.data!!)
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                context.sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())
                            }
                        }
                    }
                    else -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, null)
                        context.sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())
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

    private fun updateData(dailyNote: DailyNote) {
        log.e()
        val context = applicationContext

        val prefResin: Int = PreferenceManager.getIntCurrentResin(context)
        val nowResin: Int = dailyNote.current_resin
        if (PreferenceManager.getBooleanNotiEach40Resin(context)) {
            if (200 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(NOTI_TYPE_EACH_40_RESIN, 200)
            } else if (160 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(NOTI_TYPE_EACH_40_RESIN, 160)
            } else if (120 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(NOTI_TYPE_EACH_40_RESIN, 120)
            } else if (80 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(NOTI_TYPE_EACH_40_RESIN, 80)
            } else if (40 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(NOTI_TYPE_EACH_40_RESIN, 40)
            }
        }
        if (PreferenceManager.getBooleanNoti140Resin(context)) {
            if (140 in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(NOTI_TYPE_140_RESIN, 140)
            }
        }
        if (PreferenceManager.getBooleanNotiCustomResin(context)) {
            val targetResin: Int = PreferenceManager.getIntCustomTargetResin(context)
            if (targetResin in (prefResin + 1)..nowResin){
                log.e()
                sendNoti(NOTI_TYPE_CUSTOM_RESIN, targetResin)
            }
        }
        /* 이전 레진 과 새 레진 비교해서 알림 발송 */

        PreferenceManager.setIntCurrentResin(context, dailyNote.current_resin)
        PreferenceManager.setIntMaxResin(context, dailyNote.max_resin)
        PreferenceManager.setStringResinRecoveryTime(context, dailyNote.resin_recovery_time?:"-1")
        PreferenceManager.setStringRecentSyncTime(context, CommonFunction.getTimeSyncTimeFormat())

        context.sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())

    }

    private fun sendNoti(id: Int, target: Int) {
        log.e()

        if (inputData.getBoolean(Constant.ARG_IS_ONE_TIME, false)) {
            log.e()
            return
        }

        val title = applicationContext.getString(R.string.push_resin_noti_title)
        val msg = when (id) {
            NOTI_TYPE_EACH_40_RESIN -> when (target) {
                200 -> "여행자! 현재 레진이 ${target}을 넘어섰어! 이렇게 많은 레진은 처음 봐!"
                160 -> "여행자! 현재 레진이 ${target}을 넘어섰어! 빨리 사용해야 해! 레손실 난다구!"
                120 -> "여행자! 현재 레진이 ${target}을 넘어섰어! 슬슬 사용하러 가자!"
                80 -> "여행자! 현재 레진이 ${target}을 넘어섰어!"
                else -> "여행자! 현재 레진이 ${target}을 넘어섰어!"
            }
            NOTI_TYPE_140_RESIN -> "여행자! 현재 레진이 ${target}을 넘어섰어! 빨리 사용하러 가자!"
            else ->  "여행자! 현재 레진이 ${target}을 넘어섰어! 지금 불러달라고 했었지?"
        }
        CommonFunction.sendNotification(id, applicationContext, title, msg)
    }

    override fun doWork(): Result {
        log.e()

        try {
            rxApiDailyNote.onNext(true)

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