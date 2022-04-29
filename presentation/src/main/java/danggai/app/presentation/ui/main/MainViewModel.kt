package danggai.app.presentation.ui.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.log
import danggai.domain.core.ApiResult
import danggai.domain.local.CheckInSettings
import danggai.domain.local.DailyNoteSettings
import danggai.domain.network.changedataswitch.usecase.ChangeDataSwitchUseCase
import danggai.domain.network.dailynote.entity.DailyNoteData
import danggai.domain.network.dailynote.usecase.DailyNoteUseCase
import danggai.domain.network.getgamerecordcard.usecase.GetGameRecordCardUseCase
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.resource.repository.ResourceProviderRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val resource: ResourceProviderRepository,
    private val preference: PreferenceManagerRepository,
    private val dailyNote: DailyNoteUseCase,
    private val changeDataSwitch: ChangeDataSwitchUseCase,
    private val getGameRecordCard: GetGameRecordCardUseCase,
) : BaseViewModel() {
    val sfProgress = MutableStateFlow(false)

    val sfServer = MutableStateFlow(0)
    val sfAutoRefreshPeriod = MutableStateFlow(15L)

    val sfUid = MutableStateFlow("")
    val sfCookie = MutableStateFlow("")

    val sfEnableNotiEach40Resin = MutableStateFlow(false)
    val sfEnableNoti140Resin = MutableStateFlow(false)
    val sfEnableNotiCustomResin = MutableStateFlow(false)
    val sfCustomNotiResin = MutableStateFlow("0")
    val sfEnableNotiExpeditionDone = MutableStateFlow(false)
    val sfEnableNotiHomeCoinFull = MutableStateFlow(false)

    val sfEnableGenshinAutoCheckIn = MutableStateFlow(false)
    val sfEnableHonkai3rdAutoCheckIn = MutableStateFlow(false)
    val sfEnableNotiCheckinSuccess = MutableStateFlow(false)
    val sfEnableNotiCheckinFailed = MutableStateFlow(false)

    private var _dailyNotePrivateErrorCount = 0
    val dailyNotePrivateErrorCount
        get() = _dailyNotePrivateErrorCount

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
                        sfProgress.value = true
                    }
                },
                onComplete = {
                    CoroutineScope(Dispatchers.Main).launch {
                        sfProgress.value = false
                    }
                }
            ).collect {
                log.e(it)
                when (it) {
                    is ApiResult.Success -> {
                        when (it.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                saveWidgetData(true, it.data.data)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_success)))

                                it.data.data?.let { data ->
                                    sendWidgetSyncBroadcast(data)
                                }
                            }
                            Constant.RETCODE_ERROR_CHARACTOR_INFO -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_error_charactor_info)))
                            }
                            Constant.RETCODE_ERROR_INTERNAL_DATABASE_ERROR -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_error_internal_database_error)))
                            }
                            Constant.RETCODE_ERROR_TOO_MANY_REQUESTS -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_error_too_many_requests)))
                            }
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN,
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN_2-> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_error_not_logged_in)))
                            }
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN_3 -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_error_not_logged_in_3)))
                            }
                            Constant.RETCODE_ERROR_WRONG_ACCOUNT -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_error_wrong_account)))
                            }
                            Constant.RETCODE_ERROR_DATA_NOT_PUBLIC -> {
                                log.e()
                                _dailyNotePrivateErrorCount += 1
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.WhenDailyNoteIsPrivate())
                            }
                            Constant.RETCODE_ERROR_ACCOUNT_NOT_FOUND -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_error_account_not_found)))
                            }
                            Constant.RETCODE_ERROR_INVALID_LANGUAGE -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_error_invalid_language)))
                            }
                            Constant.RETCODE_ERROR_INVALID_INPUT_FORMAT -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_error_invalid_input)))
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(String.format(resource.getString(R.string.msg_toast_dailynote_error_include_error_code), it.data.retcode)))
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, null)
                            sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_common_network_error)))
                        }
                    }
                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, null, null)
                        sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_dailynote_error)))
                    }
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, null,null)
                        sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_common_body_null_error)))
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
                        sfProgress.value = true
                    }
                },
                onComplete = {
                    CoroutineScope(Dispatchers.Main).launch {
                        sfProgress.value = false
                    }
                }
            ).collect {
                log.e(it)
                when (it) {
                    is ApiResult.Success -> {
                        when (it.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                log.e()
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_change_data_switch_success)))
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(String.format(resource.getString(R.string.msg_toast_change_data_switch_error_include_error_code), it.data.retcode)))
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, it.code, null)
                            sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_common_network_error)))
                        }
                    }
                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, null, null)
                        sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_change_data_switch_error)))
                    }
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, null, null)
                        sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_common_body_null_error)))
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
                        sfProgress.value = true
                    }
                },
                onComplete = {
                    CoroutineScope(Dispatchers.Main).launch {
                        sfProgress.value = false
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
                                            sfUid.value = recordCard.game_role_id
                                    }
                                    sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_get_uid_success)))
                                } else {
                                    log.e()
                                    sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_get_uid_error_card_list_empty)))
                                }
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, it.code, it.data.retcode)
                                sendEvent(Event.MakeToast(String.format(resource.getString(R.string.msg_toast_get_uid_error_include_error_code), it.data.retcode)))
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, it.code, null)
                            sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_common_network_error)))
                        }
                    }
                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, null, null)
                        sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_get_uid_error)))
                    }
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, null, null)
                        sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_common_body_null_error)))
                    }
                }
            }
        }
    }

    fun initUI() {
        preference.getStringUid().let {
            log.e(it)
            sfUid.value = it
        }

        preference.getStringCookie().let {
            log.e(it)
            sfCookie.value = it
        }

        preference.getDailyNoteSettings().let {
            sfServer.value = it.server
            sfAutoRefreshPeriod.value = it.autoRefreshPeriod
            sfEnableNotiEach40Resin.value = it.notiEach40Resin
            sfEnableNoti140Resin.value = it.noti140Resin
            sfEnableNotiCustomResin.value = it.notiCustomResin
            sfCustomNotiResin.value = it.customResin.toString()
            sfEnableNotiExpeditionDone.value = it.notiExpedition
            sfEnableNotiHomeCoinFull.value = it.notiHomeCoin
        }

        preference.getCheckInSettings().let {
            sfEnableGenshinAutoCheckIn.value = it.genshinCheckInEnable
            sfEnableHonkai3rdAutoCheckIn.value = it.honkai3rdCheckInEnable
            sfEnableNotiCheckinSuccess.value = it.notiCheckInSuccess
            sfEnableNotiCheckinFailed.value = it.notiCheckInFailed
        }
    }

    fun onClickSave() {
        log.e()
        if (sfUid.value.isEmpty() || sfCookie.value.isEmpty())  {
            saveWidgetData(false, null)
        } else {
            sfUid.value = sfUid.value.trim()
            sfCookie.value = sfCookie.value.trim()

            refreshDailyNote(
                sfUid.value,
                when (sfServer.value) {
                    Constant.PREF_SERVER_ASIA -> Constant.SERVER_OS_ASIA
                    Constant.PREF_SERVER_EUROPE -> Constant.SERVER_OS_EURO
                    Constant.PREF_SERVER_USA -> Constant.SERVER_OS_USA
                    Constant.PREF_SERVER_CHT -> Constant.SERVER_OS_CHT
                    else -> Constant.SERVER_OS_ASIA
                },
                sfCookie.value,
                CommonFunction.getGenshinDS()
            )
        }
    }
    
    private fun saveWidgetData(isDataValid: Boolean, dailyNote: DailyNoteData?) {
        if (isDataValid) {
            log.e()

            dailyNote?.let {
                preference.setStringUid(sfUid.value)
                preference.setStringCookie(sfCookie.value)
                preference.setBooleanIsValidUserData(true)

                val customNotiResin: Int = try {
                    if (sfCustomNotiResin.value.isEmpty()
                        || sfCustomNotiResin.value.toInt() < 0) 0
                    else if (sfCustomNotiResin.value.toInt() > dailyNote.max_resin) {
                        sfCustomNotiResin.value = dailyNote.max_resin.toString()
                        dailyNote.max_resin
                    } else sfCustomNotiResin.value.toInt()
                } catch (e:java.lang.Exception) {
                    0
                }

                preference.setDailyNoteSettings(
                    DailyNoteSettings(
                        sfServer.value,
                        sfAutoRefreshPeriod.value,
                        sfEnableNotiEach40Resin.value,
                        sfEnableNoti140Resin.value,
                        sfEnableNotiCustomResin.value,
                        customNotiResin,
                        sfEnableNotiExpeditionDone.value,
                        sfEnableNotiHomeCoinFull.value
                    )
                )
            }

            sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_save_done)))
        } else if (!isDataValid and sfUid.value.isEmpty()) {
            sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_uid_empty_error)))
        } else if (!isDataValid and sfCookie.value.isEmpty()) {
            sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_cookie_empty_error)))
        }
    }
    
    private fun saveCheckInData(isDataValid: Boolean) {
        if (isDataValid) {
            log.e()
            preference.setBooleanIsValidUserData(true)
            preference.setStringCookie(sfCookie.value)

            preference.setCheckInSettings(
                CheckInSettings(
                    sfEnableGenshinAutoCheckIn.value,
                    sfEnableHonkai3rdAutoCheckIn.value,
                    sfEnableNotiCheckinSuccess.value,
                    sfEnableNotiCheckinFailed.value
                )
            )

            if (!sfEnableGenshinAutoCheckIn.value &&
                !sfEnableHonkai3rdAutoCheckIn.value)
                sendEvent(Event.StartShutCheckInWorker(false))

            sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_save_done_check_in)))
        } else if (!isDataValid and sfCookie.value.isEmpty()) {
            sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_cookie_empty_error)))
        }
    }

    private fun sendWidgetSyncBroadcast(dailyNote: DailyNoteData) {
        log.e()

        preference.setStringRecentSyncTime(CommonFunction.getTimeSyncTimeFormat())

        val expeditionTime: String = CommonFunction.getExpeditionTime(dailyNote)
        preference.setStringExpeditionTime(expeditionTime)

        preference.setDailyNote(dailyNote)

        if (sfAutoRefreshPeriod.value == -1L) {
            sendEvent(Event.StartShutRefreshWorker(false))
        } else {
            sendEvent(Event.StartShutRefreshWorker(true))
        }
    }

    private fun startCheckIn() {
        val settings = preference.getCheckInSettings()
        if (settings.genshinCheckInEnable ||
            settings.honkai3rdCheckInEnable) {
            log.e()
            sendEvent(Event.StartShutCheckInWorker(true))
        }
    }

    fun onClickCheckInSave() {
        log.e()
        if (sfCookie.value.isEmpty())  {
            saveCheckInData(false)
        } else {
            sfCookie.value = sfCookie.value.trim()
            saveCheckInData(true)
            startCheckIn()
        }
    }

    fun onClickWidgetRefreshNotWork() {
        log.e()
        sendEvent(Event.WidgetRefreshNotWork())
    }

    fun onClickSetServer(server: Constant.Server) {
        log.e("server -> $server")
        sfServer.value = server.pref
    }

    fun onClickSetAutoRefreshPeriod(period: Long) {
        log.e("period -> $period")
        sfAutoRefreshPeriod.value = period
    }

    fun onClickWidgetDesign() {
        log.e()
        sendEvent(Event.StartWidgetDesignActivity())
    }

    fun onClickGetCookie() {
        log.e()
        sendEvent(Event.GetCookie())
    }

    fun onClickGetUid() {
        log.e()
        if (!sfCookie.value.contains(("ltuid="))) {
            sendEvent(Event.MakeToast("ltuid 없음"))
            return
        }

        val cookieData = mutableMapOf<String, String>()

        sfCookie.value.split(";").onEach { item ->
            if (item == "") return@onEach

            val parsedKeyValue = item.trim().split("=")
            cookieData[parsedKeyValue[0]] = parsedKeyValue[1]
        }

        getUid(
            cookieData["ltuid"]?:"",
            sfCookie.value,
            CommonFunction.getGenshinDS()
        )
    }

    fun makeDailyNotePublic() {
        log.e()
        makeBattleChroniclePublic(
            gameId = 2,
            switchId = 3,
            isPublic = true,
            cookie = sfCookie.value,
            ds = CommonFunction.getGenshinDS()
        )
    }

    fun onClickChangeLanguage() {
        log.e()
        sendEvent(Event.ChangeLanguage())
    }

    /*개발용 함수*/

    fun makeDailyNotePrivate() {
        log.e()
//        rxApiChangeDataSwitchPrivate.onNext(false)
    }
}