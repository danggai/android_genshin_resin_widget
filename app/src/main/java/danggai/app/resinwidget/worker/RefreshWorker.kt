package danggai.app.resinwidget.worker

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.Event
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class RefreshWorker (val context: Context, workerParams: WorkerParameters, private val api: ApiRepository) :
    Worker(context, workerParams) {

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
            .observeOn(Schedulers.newThread())
            .filter { it }
            .switchMap {
                val uid = PreferenceManager.getStringUid(applicationContext)
                val cookie = PreferenceManager.getStringCookie(applicationContext)

                api.dailyNote(uid, cookie)
            }
            .observeOn(Schedulers.io())
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
                                context.sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())
                            }
                        }
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

        PreferenceManager.setIntCurrentResin(context, dailyNote.current_resin)
        PreferenceManager.setIntMaxResin(context, dailyNote.max_resin)
        PreferenceManager.setStringResinRecoveryTime(context, dailyNote.resin_recovery_time?:"-1")
        PreferenceManager.setStringRecentSyncTime(context, CommonFunction.getTimeSyncTimeFormat())

        context.sendBroadcast(CommonFunction.getIntentAppWidgetUiUpdate())
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