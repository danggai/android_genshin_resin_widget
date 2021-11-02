package danggai.app.resinwidget.ui.main

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.ui.base.BaseViewModel
import danggai.app.resinwidget.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MainViewModel(override val app: Application, private val api: ApiRepository) : BaseViewModel(app) {

    private val rxApiDailyNote: PublishSubject<Boolean> = PublishSubject.create()

    var lvSaveUserInfo = MutableLiveData<Event<Boolean>>()
    var lvSendWidgetSyncBroadcast = MutableLiveData<Event<DailyNote>>()
    var lvSetAutoRefreshPeriod = MutableLiveData<Event<Long>>()
    var lvWidgetRefreshNotWork = MutableLiveData<Event<Boolean>>()
    var lvHowCanIGetCookie = MutableLiveData<Event<Boolean>>()

    var lvAutoRefreshPeriod: NonNullMutableLiveData<Long> = NonNullMutableLiveData(15L)
    val lvUid: NonNullMutableLiveData<String> = NonNullMutableLiveData("")
    val lvCookie: NonNullMutableLiveData<String> = NonNullMutableLiveData("")

    private fun initRx() {
        rxApiDailyNote
            .observeOn(Schedulers.newThread())
            .filter { it }
            .switchMap {
                api.dailyNote(lvUid.value, lvCookie.value)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ res ->
                when (res.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        log.e()
                        when (res.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                lvSaveUserInfo.value = Event(true)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_success))

                                /*res 저장 후 fragment 에서 값 연동*/
                                res.data.data?.let {
                                    lvSendWidgetSyncBroadcast.value = Event(it)
                                }
                            }
                            Constant.RETCODE_ERROR_CHARACTOR_INFO -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_charactor_info))
                            }
                            Constant.RETCODE_ERROR_INVALID_REQUEST -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_invalid_request))
                            }
                            else -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error))
                            }
                        }
                    }
                }
            }, {
                it.message?.let { msg ->
                    lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error))
                    initRx()
                    log.e(msg)
                }
            }).addCompositeDisposable()
    }

    fun initUI(uid: String, cookie: String) {
        log.e()
        lvUid.value = uid
        lvCookie.value = cookie

        initRx()
    }

    fun onClickSave() {
        log.e()
        if (lvUid.value.isEmpty() || lvCookie.value.isEmpty())  {
            lvSaveUserInfo.value = Event(false)
        } else {
            lvUid.value = lvUid.value.trim()
            lvCookie.value = lvCookie.value.trim()
            rxApiDailyNote.onNext(true)
        }
    }

    fun onClickWidgetRefreshNotWork() {
        log.e()
        lvWidgetRefreshNotWork.value = Event(true)
    }

    fun onClickSetAutoRefreshPeriod(view: View) {
        log.e()
        val period = when (view.id) {
            R.id.rb_15m -> 15L
            R.id.rb_30m -> 30L
            R.id.rb_1h -> 60L
            R.id.rb_2h -> 120L
            else -> -1L
        }

        lvSetAutoRefreshPeriod.value = Event(period)
    }

    fun onClickHowCanIGetCookie() {
        log.e()
        lvHowCanIGetCookie.value = Event(true)
    }

}