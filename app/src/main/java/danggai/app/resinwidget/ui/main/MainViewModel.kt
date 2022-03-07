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
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.Event
import danggai.app.resinwidget.util.NonNullMutableLiveData
import danggai.app.resinwidget.util.log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MainViewModel(override val app: Application, private val api: ApiRepository) : BaseViewModel(app) {

    private val rxApiDailyNote: PublishSubject<Boolean> = PublishSubject.create()
    private val rxGetUidFromHoyolabUid: PublishSubject<Boolean> = PublishSubject.create()
    private val rxApiChangeDataSwitchPublic: PublishSubject<Boolean> = PublishSubject.create()
    private val rxApiChangeDataSwitchPrivate: PublishSubject<Boolean> = PublishSubject.create()

    var lvSaveResinWidgetData = MutableLiveData<Event<Boolean>>()
    var lvSaveCheckInData = MutableLiveData<Event<Boolean>>()
    var lvSendWidgetSyncBroadcast = MutableLiveData<Event<DailyNote>>()
    var lvWidgetRefreshNotWork = MutableLiveData<Event<Boolean>>()
    var lvGetCookie = MutableLiveData<Event<Boolean>>()
    var lvWhenDailyNotePrivate = MutableLiveData<Event<Boolean>>()
    var lvSetProgress = MutableLiveData<Event<Boolean>>()
    var lvStartCheckInWorker = MutableLiveData<Event<Boolean>>()
    var lvStartWidgetDesignActivity = MutableLiveData<Event<Boolean>>()
    var lvChangeLanguage = MutableLiveData<Event<Boolean>>()

    var lvServer: NonNullMutableLiveData<Int> = NonNullMutableLiveData(0)
    var lvAutoRefreshPeriod: NonNullMutableLiveData<Long> = NonNullMutableLiveData(15L)
    val lvUid: NonNullMutableLiveData<String> = NonNullMutableLiveData("")
    val lvCookie: NonNullMutableLiveData<String> = NonNullMutableLiveData("")

    val lvEnableNotiEach40Resin: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvEnableNoti140Resin: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvEnableNotiCustomResin: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvCustomNotiResin: NonNullMutableLiveData<String> = NonNullMutableLiveData("0")
    val lvEnableNotiExpeditionDone: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvEnableNotiHomeCoinFull: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)

    val lvEnableAutoCheckIn: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvEnableNotiCheckinSuccess: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvEnableNotiCheckinFailed: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)

    val lvDailyNotePrivateErrorCount: NonNullMutableLiveData<Int> = NonNullMutableLiveData(0)

    private fun initRx() {
        initRxDailyNote()
        initRxChangeDataSwitchPublic()
        initRxGetUidFromHoyolabUid()

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
            .debounce(250, TimeUnit.MILLISECONDS)
            .filter { it }
            .switchMap {
                val server = when (lvServer.value) {
                    Constant.PREF_SERVER_ASIA -> Constant.SERVER_OS_ASIA
                    Constant.PREF_SERVER_EUROPE -> Constant.SERVER_OS_EURO
                    Constant.PREF_SERVER_USA -> Constant.SERVER_OS_USA
                    Constant.PREF_SERVER_CHT -> Constant.SERVER_OS_CHT
                    else -> Constant.SERVER_OS_ASIA
                }
                api.dailyNote(lvUid.value, server, lvCookie.value)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ res ->
                setProgress(false)
                when (res.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        when (res.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                lvSaveResinWidgetData.value = Event(true)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_success))

                                res.data.data?.let {
                                    lvSendWidgetSyncBroadcast.value = Event(it)
                                }
                            }
                            Constant.RETCODE_ERROR_CHARACTOR_INFO -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_charactor_info))
                            }
                            Constant.RETCODE_ERROR_INTERNAL_DATABASE_ERROR -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_internal_database_error))
                            }
                            Constant.RETCODE_ERROR_TOO_MANY_REQUESTS -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_too_many_requests))
                            }
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN,
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN_2-> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_not_logged_in))
                            }
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN_3 -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_not_logged_in_3))
                            }
                            Constant.RETCODE_ERROR_WRONG_ACCOUNT -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_wrong_account))
                            }
                            Constant.RETCODE_ERROR_DATA_NOT_PUBLIC -> {
                                log.e()
                                lvDailyNotePrivateErrorCount.value += 1
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvWhenDailyNotePrivate.value = Event(true)
                            }
                            Constant.RETCODE_ERROR_ACCOUNT_NOT_FOUND -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_account_not_found))
                            }
                            Constant.RETCODE_ERROR_INVALID_LANGUAGE -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_invalid_language))
                            }
                            Constant.RETCODE_ERROR_INVALID_INPUT_FORMAT -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_invalid_input))
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_dailynote_error_include_error_code), res.data.retcode))
                            }
                        }
                    } else -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, res.meta.code, null)
                        lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_api_error_include_code), res.meta.code))
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
            .debounce(250, TimeUnit.MILLISECONDS)
            .switchMap {
                api.changeDataSwitch(2,1, true, lvCookie.value)
            }
            .switchMap {
                api.changeDataSwitch(2,2, true, lvCookie.value)
            }
            .switchMap {
                api.changeDataSwitch(2,4, true, lvCookie.value)
            }
            .switchMap {
                api.changeDataSwitch(2,3, true, lvCookie.value)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ res ->
                setProgress(false)
                log.e(res)
                when (res.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        when (res.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                lvMakeToast.value = Event(getString(R.string.msg_toast_change_data_switch_success))
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_change_data_switch_error_include_error_code), res.data.retcode))
                            }
                        }
                    }
                    else -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, res.meta.code, null)
                        lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_api_error_include_code), res.meta.code))
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
            .debounce(250, TimeUnit.MILLISECONDS)
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
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_change_data_switch_error_include_error_code), res.data.retcode))
                            }
                        }
                    } else -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, res.meta.code, null)
                        lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_api_error_include_code), res.meta.code))
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

    private fun initRxGetUidFromHoyolabUid() {
        rxGetUidFromHoyolabUid
            .map {
                setProgress(true)
                it
            }
            .observeOn(Schedulers.newThread())
            .debounce(250, TimeUnit.MILLISECONDS)
            .switchMap {
                val list = mutableMapOf<String, String>()
                lvCookie.value.split(";").onEach { item ->
                    if (item == "") return@onEach

                    val _list = item.trim().split("=")
                    list[_list[0]] = _list[1]
                }

                api.getCameRecordCard(list["ltuid"]?:"", lvCookie.value)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ res ->
                setProgress(false)
                log.e(res)
                when (res.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        when (res.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                if (res.data.data.list.isNotEmpty()) {
                                    log.e()
                                    res.data.data.list.forEach {
                                        if (it.game_id == Constant.GAME_ID_GENSHIN_IMPACT)
                                            lvUid.value = it.game_role_id
                                    }
                                    lvMakeToast.value = Event(getString(R.string.msg_toast_get_uid_success))
                                } else {
                                    log.e()
                                    lvMakeToast.value = Event(getString(R.string.msg_toast_get_uid_error_card_list_empty))
                                }
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, res.meta.code, res.data.retcode)
                                lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_get_uid_error_include_error_code), res.data.retcode))
                            }
                        }
                    } else -> {
                        log.e()
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, res.meta.code, null)
                        lvMakeToast.value = Event(String.format(getString(R.string.msg_toast_api_error_include_code), res.meta.code))
                    }
                }
            }, {
                setProgress(false)
                it.message?.let { msg ->
                    log.e(msg)
                    lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error))
                    initRxGetUidFromHoyolabUid()
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
            lvSaveResinWidgetData.value = Event(false)
        } else {
            lvUid.value = lvUid.value.trim()
            lvCookie.value = lvCookie.value.trim()
            rxApiDailyNote.onNext(true)
        }
    }

    fun onClickCheckInSave() {
        log.e()
        if (lvCookie.value.isEmpty())  {
            lvSaveCheckInData.value = Event(false)
        } else {
            lvCookie.value = lvCookie.value.trim()
            lvSaveCheckInData.value = Event(true)
            lvStartCheckInWorker.value = Event(true)
        }
    }

    fun onClickWidgetRefreshNotWork() {
        log.e()
        lvWidgetRefreshNotWork.value = Event(true)
    }

    fun onClickSetServer(view: View) {
        log.e()
        lvServer.value = when (view.id) {
            R.id.rb_asia -> Constant.PREF_SERVER_ASIA
            R.id.rb_usa -> Constant.PREF_SERVER_USA
            R.id.rb_euro -> Constant.PREF_SERVER_EUROPE
            R.id.rb_cht -> Constant.PREF_SERVER_CHT
            else -> Constant.PREF_SERVER_ASIA
        }
    }

    fun onClickSetAutoRefreshPeriod(view: View) {
        log.e()
        lvAutoRefreshPeriod.value = when (view.id) {
            R.id.rb_15m -> 15L
            R.id.rb_30m -> 30L
            R.id.rb_1h -> 60L
            R.id.rb_2h -> 120L
            else -> -1L
        }
    }

    fun onClickWidgetDesign(view: View) {
        log.e()
        lvStartWidgetDesignActivity.value = Event(true)
    }

    fun onClickGetCookie() {
        log.e()
        lvGetCookie.value = Event(true)
    }

    fun onClickGetUid() {
        log.e()
        if (!lvCookie.value.contains(("ltuid="))) {
            lvMakeToast.value = Event("ltuid 없음")
            return
        }
        rxGetUidFromHoyolabUid.onNext(true)
    }

    fun makeDailyNotePublic() {
        log.e()
        rxApiChangeDataSwitchPublic.onNext(true)
    }

    private fun setProgress(boolean: Boolean) {
        log.e()
        lvSetProgress.value = Event(boolean)
    }

    fun onClickChangeLanguage() {
        log.e()
        lvChangeLanguage.value = Event(true)
    }


    /*개발용 함수*/

    fun makeDailyNotePrivate() {
        log.e()
        rxApiChangeDataSwitchPrivate.onNext(false)
    }
}