package danggai.app.resinwidget.ui.main

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import danggai.app.resinwidget.BuildConfig
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.ui.base.BaseViewModel
import danggai.app.resinwidget.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MainViewModel(override val app: Application, private val api: ApiRepository) : BaseViewModel(app) {

    private val rxApiDailyNote: PublishSubject<Boolean> = PublishSubject.create()
    private val rxApiChangeDataSwitchPublic: PublishSubject<Boolean> = PublishSubject.create()
    private val rxApiChangeDataSwitchPrivate: PublishSubject<Boolean> = PublishSubject.create()

    var lvSaveUserInfo = MutableLiveData<Event<Boolean>>()
    var lvSendWidgetSyncBroadcast = MutableLiveData<Event<DailyNote>>()
    var lvSetAutoRefreshPeriod = MutableLiveData<Event<Long>>()
    var lvWidgetRefreshNotWork = MutableLiveData<Event<Boolean>>()
    var lvHowCanIGetCookie = MutableLiveData<Event<Boolean>>()
    var lvWhenDailyNotePrivate = MutableLiveData<Event<Boolean>>()
    var lvSetProgress = MutableLiveData<Event<Boolean>>()

    var lvAutoRefreshPeriod: NonNullMutableLiveData<Long> = NonNullMutableLiveData(15L)
    val lvUid: NonNullMutableLiveData<String> = NonNullMutableLiveData("")
    val lvCookie: NonNullMutableLiveData<String> = NonNullMutableLiveData("")
    val lvEnableNotiEach40Resin: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvEnableNoti140Resin: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvEnableNotiCustomResin: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvCustomNotiResin: NonNullMutableLiveData<String> = NonNullMutableLiveData("0")

    private fun initRx() {
        initRxDailyNote()
        initRxChangeDataSwitchPublic()

        if (BuildConfig.DEBUG){
            initRxChangeDataSwitchPrivate()
        }
    }

    private fun initRxDailyNote() {
        rxApiDailyNote
            .map {
                setProgress(true)
                it
            }
            .throttleFirst(1, TimeUnit.SECONDS)
            .observeOn(Schedulers.newThread())
            .filter { it }
            .switchMap {
                api.dailyNote(lvUid.value, lvCookie.value)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ res ->
                setProgress(false)
                when (res.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        log.e()
                        when (res.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                lvSaveUserInfo.value = Event(true)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_success))

                                res.data.data?.let {
                                    lvSendWidgetSyncBroadcast.value = Event(it)
                                }
                            }
                            Constant.RETCODE_ERROR_CHARACTOR_INFO -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_charactor_info))
                            }
                            Constant.RETCODE_ERROR_INTERNAL_DATABASE_ERROR -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_internal_database_error))
                            }
                            Constant.RETCODE_ERROR_TOO_MANY_REQUESTS -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_too_many_requests))
                            }
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN,
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN_2-> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_not_logged_in))
                            }
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN_3 -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_not_logged_in_3))
                            }
                            Constant.RETCODE_ERROR_DATA_NOT_PUBLIC -> {
                                lvWhenDailyNotePrivate.value = Event(true)
                            }
                            Constant.RETCODE_ERROR_ACCOUNT_NOT_FOUND -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_account_not_found))
                            }
                            Constant.RETCODE_ERROR_INVALID_LANGUAGE -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_invalid_language))
                            }
                            else -> {
                                lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_dailynote_error_include_error_code), res.data.retcode))
                            }
                        }
                    }
                }
            }, {
                setProgress(false)
                it.message?.let { msg ->
                    log.e(msg)
                    lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error))
                    initRxDailyNote()
                }
            }).addCompositeDisposable()
    }

    private fun initRxChangeDataSwitchPublic() {
        rxApiChangeDataSwitchPublic
            .map {
                setProgress(true)
                it
            }
            .observeOn(Schedulers.newThread())
            .switchMap {
                api.changeDataSwitch(2,1, true, lvCookie.value)
            }
            .switchMap {
                api.changeDataSwitch(2,2, true, lvCookie.value)
            }
            .switchMap {
                api.changeDataSwitch(2,3, true, lvCookie.value)
            }
            .switchMap {
                api.changeDataSwitch(2,4, true, lvCookie.value)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ res ->
                setProgress(false)
                log.e(res)
                when (res.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        log.e()
                        when (res.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_change_data_switch_success))
                            }
                            else -> {
                                lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_change_data_switch_error_include_error_code), res.data.retcode))
                            }
                        }
                    }
                }
            }, {
                setProgress(false)
                it.message?.let { msg ->
                    log.e(msg)
                    lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error))
                    initRxChangeDataSwitchPublic()
                }
            }).addCompositeDisposable()
    }

    private fun initRxChangeDataSwitchPrivate() {
        rxApiChangeDataSwitchPrivate
            .map {
                setProgress(true)
                it
            }
            .observeOn(Schedulers.newThread())
            .switchMap {
                api.changeDataSwitch(2,1, false, lvCookie.value)
            }
            .switchMap {
                api.changeDataSwitch(2,2, false, lvCookie.value)
            }
            .switchMap {
                api.changeDataSwitch(2,3, false, lvCookie.value)
            }
            .switchMap {
                api.changeDataSwitch(2,4, false, lvCookie.value)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ res ->
                setProgress(false)
                log.e(res)
                when (res.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        log.e()
                        when (res.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_change_data_switch_success))
                            }
                            else -> {
                                lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_change_data_switch_error_include_error_code), res.data.retcode))
                            }
                        }
                    }
                }
            }, {
                setProgress(false)
                it.message?.let { msg ->
                    log.e(msg)
                    lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error))
                    initRxChangeDataSwitchPrivate()
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

    fun makeDailyNotePublic() {
        log.e()
        rxApiChangeDataSwitchPublic.onNext(true)
    }

    fun makeDailyNotePrivate() {
        log.e()
        rxApiChangeDataSwitchPrivate.onNext(false)
    }

    private fun setProgress(boolean: Boolean) {
        log.e()
        lvSetProgress.value = Event(boolean)
    }

}