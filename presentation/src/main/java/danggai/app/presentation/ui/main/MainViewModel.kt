package danggai.app.presentation.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.core.util.CommonFunction
import danggai.app.presentation.core.util.Event
import danggai.app.presentation.core.util.NonNullMutableLiveData
import danggai.app.presentation.core.util.log
import danggai.domain.core.ApiResult
import danggai.domain.network.changedataswitch.usecase.ChangeDataSwitchUseCase
import danggai.domain.network.dailynote.entity.DailyNote
import danggai.domain.network.dailynote.usecase.DailyNoteUseCase
import danggai.domain.network.getgamerecordcard.usecase.GetGameRecordCardUseCase
import danggai.domain.resource.repository.ResourceProviderRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val resource: ResourceProviderRepository,
    private val dailyNote: DailyNoteUseCase,
    private val changeDataSwitch: ChangeDataSwitchUseCase,
    private val getGameRecordCard: GetGameRecordCardUseCase,
) : BaseViewModel() {

    var lvSaveResinWidgetData = MutableLiveData<Event<Boolean>>()
    var lvSaveCheckInData = MutableLiveData<Event<Boolean>>()
    var lvSendWidgetSyncBroadcast = MutableLiveData<Event<DailyNote.Data>>()
    var lvWidgetRefreshNotWork = MutableLiveData<Event<Boolean>>()
    var lvGetCookie = MutableLiveData<Event<Boolean>>()
    var lvWhenDailyNotePrivate = MutableLiveData<Event<Boolean>>()
    var lvStartCheckInWorker = MutableLiveData<Event<Boolean>>()
    var lvStartWidgetDesignActivity = MutableLiveData<Event<Boolean>>()
    var lvChangeLanguage = MutableLiveData<Event<Boolean>>()

    var lvProgress: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)

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

    val lvEnableGenshinAutoCheckIn: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvEnableHonkai3rdAutoCheckIn: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvEnableNotiCheckinSuccess: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)
    val lvEnableNotiCheckinFailed: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(false)

    val lvDailyNotePrivateErrorCount: NonNullMutableLiveData<Int> = NonNullMutableLiveData(0)

    private fun refreshDailyNote(
        uid: String,
        server: String,
        cookie: String,
        ds: String
    ) {
        viewModelScope.launch {
            dailyNote(
                uid = uid,
                server = server,
                cookie = cookie,
                ds = ds,
                onStart = {
                    CoroutineScope(Dispatchers.Main).launch {
                        lvProgress.value = true
                    }
                },
                onComplete = {
                    CoroutineScope(Dispatchers.Main).launch {
                        lvProgress.value = false
                    }
                }
            ).collect {
                log.e(it)
                when (it) {
                    is ApiResult.Success -> {
                        when (it.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                lvSaveResinWidgetData.value = Event(true)
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_success))

                                it.data.data?.let { data ->
                                    lvSendWidgetSyncBroadcast.value = Event(data)
                                }
                            }
                            Constant.RETCODE_ERROR_CHARACTOR_INFO -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_error_charactor_info))
                            }
                            Constant.RETCODE_ERROR_INTERNAL_DATABASE_ERROR -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_error_internal_database_error))
                            }
                            Constant.RETCODE_ERROR_TOO_MANY_REQUESTS -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_error_too_many_requests))
                            }
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN,
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN_2-> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_error_not_logged_in))
                            }
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN_3 -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_error_not_logged_in_3))
                            }
                            Constant.RETCODE_ERROR_WRONG_ACCOUNT -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_error_wrong_account))
                            }
                            Constant.RETCODE_ERROR_DATA_NOT_PUBLIC -> {
                                log.e()
                                lvDailyNotePrivateErrorCount.value += 1
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvWhenDailyNotePrivate.value = Event(true)
                            }
                            Constant.RETCODE_ERROR_ACCOUNT_NOT_FOUND -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_error_account_not_found))
                            }
                            Constant.RETCODE_ERROR_INVALID_LANGUAGE -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_error_invalid_language))
                            }
                            Constant.RETCODE_ERROR_INVALID_INPUT_FORMAT -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_error_invalid_input))
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                lvMakeToast.value = Event(String.format(resource.getString(R.string.msg_toast_dailynote_error_include_error_code), it.data.retcode))
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, null)
                            lvMakeToast.value = Event(resource.getString(R.string.msg_toast_common_network_error))
                        }
                    }
                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, null, null)
                        lvMakeToast.value = Event(resource.getString(R.string.msg_toast_dailynote_error))
                    }
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, null,null)
                        lvMakeToast.value = Event(resource.getString(R.string.msg_toast_common_body_null_error))
                    }
                }
            }
        }
    }

    private fun makeBattleChroniclePublic(
        gameId: Int,
        switchId: Int,
        isPublic: Boolean,
        cookie: String,
        ds: String
    ) {
        viewModelScope.launch {
            changeDataSwitch(
                gameId = gameId,
                switchId = switchId,
                isPublic = isPublic,
                cookie = cookie,
                ds = ds,
                onStart = {
                    CoroutineScope(Dispatchers.Main).launch {
                        lvProgress.value = true
                    }
                },
                onComplete = {
                    CoroutineScope(Dispatchers.Main).launch {
                        lvProgress.value = false
                    }
                }
            ).collect {
                log.e(it)
                when (it) {
                    is ApiResult.Success -> {
                        when (it.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                lvMakeToast.value = Event(resource.getString(R.string.msg_toast_change_data_switch_success))
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, it.code, it.data.retcode)
                                lvMakeToast.value = Event(String.format(resource.getString(R.string.msg_toast_change_data_switch_error_include_error_code), it.data.retcode))
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, it.code, null)
                            lvMakeToast.value = Event(resource.getString(R.string.msg_toast_common_network_error))
                        }
                    }
                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, null, null)
                        lvMakeToast.value = Event(resource.getString(R.string.msg_toast_change_data_switch_error))
                    }
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, null, null)
                        lvMakeToast.value = Event(resource.getString(R.string.msg_toast_common_body_null_error))
                    }
                }
            }
        }
    }

    private fun getUid(
        hoyolabUid: String,
        cookie: String,
        ds: String
    ) {
        viewModelScope.launch {
            getGameRecordCard(
                hoyolabUid = hoyolabUid,
                cookie = cookie,
                ds = ds,
                onStart = {
                    CoroutineScope(Dispatchers.Main).launch {
                        lvProgress.value = true
                    }
                },
                onComplete = {
                    CoroutineScope(Dispatchers.Main).launch {
                        lvProgress.value = false
                    }
                }
            ).collect {
                log.e(it)
                when (it) {
                    is ApiResult.Success -> {
                        when (it.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                if (it.data.data.list.isNotEmpty()) {
                                    log.e()
                                    it.data.data.list.forEach { recordCard ->
                                        if (recordCard.game_id == Constant.GAME_ID_GENSHIN_IMPACT)
                                            lvUid.value = recordCard.game_role_id
                                    }
                                    lvMakeToast.value = Event(resource.getString(R.string.msg_toast_get_uid_success))
                                } else {
                                    log.e()
                                    lvMakeToast.value = Event(resource.getString(R.string.msg_toast_get_uid_error_card_list_empty))
                                }
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, it.code, it.data.retcode)
                                lvMakeToast.value = Event(String.format(resource.getString(R.string.msg_toast_get_uid_error_include_error_code), it.data.retcode))
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, it.code, null)
                            lvMakeToast.value = Event(resource.getString(R.string.msg_toast_common_network_error))
                        }
                    }
                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, null, null)
                        lvMakeToast.value = Event(resource.getString(R.string.msg_toast_get_uid_error))
                    }
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, null, null)
                        lvMakeToast.value = Event(resource.getString(R.string.msg_toast_common_body_null_error))
                    }
                }
            }
        }
    }

    fun initUI(uid: String, cookie: String) {
        log.e(uid)
        log.e(cookie)
        lvUid.value = uid
        lvCookie.value = cookie
    }

    fun onClickSave() {
        log.e()
        if (lvUid.value.isEmpty() || lvCookie.value.isEmpty())  {
            lvSaveResinWidgetData.value = Event(false)
        } else {
            lvUid.value = lvUid.value.trim()
            lvCookie.value = lvCookie.value.trim()

            refreshDailyNote(
                lvUid.value,
                when (lvServer.value) {
                    Constant.PREF_SERVER_ASIA -> Constant.SERVER_OS_ASIA
                    Constant.PREF_SERVER_EUROPE -> Constant.SERVER_OS_EURO
                    Constant.PREF_SERVER_USA -> Constant.SERVER_OS_USA
                    Constant.PREF_SERVER_CHT -> Constant.SERVER_OS_CHT
                    else -> Constant.SERVER_OS_ASIA
                },
                lvCookie.value,
                CommonFunction.getGenshinDS()
            )
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

    fun onClickSetServer(server: Constant.Server) {
        log.e("server -> $server")
        lvServer.value = server.pref
    }

    fun onClickSetAutoRefreshPeriod(period: Long) {
        log.e("period -> $period")
        lvAutoRefreshPeriod.value = period
    }

    fun onClickWidgetDesign() {
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

        val cookieData = mutableMapOf<String, String>()

        lvCookie.value.split(";").onEach { item ->
            if (item == "") return@onEach

            val parsedKeyValue = item.trim().split("=")
            cookieData[parsedKeyValue[0]] = parsedKeyValue[1]
        }

        getUid(
            cookieData["ltuid"]?:"",
            lvCookie.value,
            CommonFunction.getGenshinDS()
        )
    }

    fun makeDailyNotePublic() {
        log.e()
        makeBattleChroniclePublic(
            gameId = 2,
            switchId = 3,
            isPublic = true,
            cookie = lvCookie.value,
            ds = CommonFunction.getGenshinDS()
        )
    }

    fun onClickChangeLanguage() {
        log.e()
        lvChangeLanguage.value = Event(true)
    }

    /*개발용 함수*/

    fun makeDailyNotePrivate() {
        log.e()
//        rxApiChangeDataSwitchPrivate.onNext(false)
    }
}