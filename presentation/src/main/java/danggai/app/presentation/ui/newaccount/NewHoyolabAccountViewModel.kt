package danggai.app.presentation.ui.newaccount

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.log
import danggai.domain.core.ApiResult
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.usecase.AccountDaoUseCase
import danggai.domain.network.changedataswitch.usecase.ChangeDataSwitchUseCase
import danggai.domain.network.dailynote.usecase.DailyNoteUseCase
import danggai.domain.network.getgamerecordcard.usecase.GetGameRecordCardUseCase
import danggai.domain.resource.repository.ResourceProviderRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewHoyolabAccountViewModel @Inject constructor(
    private val resource: ResourceProviderRepository,
    private val accountDao: AccountDaoUseCase,
    private val getGameRecordCard: GetGameRecordCardUseCase,
    private val changeDataSwitch: ChangeDataSwitchUseCase,
    private val dailyNote: DailyNoteUseCase,
) : BaseViewModel() {
    val sfProgress = MutableStateFlow(false)

    val sfGenshinServer = MutableStateFlow(0)
    val sfGenshinNickname = MutableStateFlow("")
    val sfGenshinUid = MutableStateFlow("")
    val sfNoGenshinAccount = MutableStateFlow(false)

    val sfHonkaiSrServer = MutableStateFlow(0)
    val sfHonkaiSrNickname = MutableStateFlow("")
    val sfHonkaiSrUid = MutableStateFlow("")
    val sfNoHonkaiSrAccount = MutableStateFlow(false)

    val sfHoyolabCookie = MutableStateFlow("")
    val sfEnableGenshinAutoCheckIn = MutableStateFlow(false)
    val sfEnableHonkai3rdAutoCheckIn = MutableStateFlow(false)
    val sfEnableHonkaiSrAutoCheckIn = MutableStateFlow(false)

    private var _dailyNotePrivateErrorCount = 0
    val dailyNotePrivateErrorCount
        get() = _dailyNotePrivateErrorCount

    private val mCookieData = mutableMapOf<String, String>()

    init {
        viewModelScope.launch {
            sfHoyolabCookie.collect {
                try {
                    it.split(";").onEach { item ->
                        if (item == "") return@onEach

                        val parsedKeyValue = item.trim().split("=")
                        mCookieData[parsedKeyValue[0]] = parsedKeyValue[1]
                    }
                } catch (e: Exception) {
                    log.e()
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
                                sfEnableGenshinAutoCheckIn.value = false
                                sfEnableHonkaiSrAutoCheckIn.value = false
                                sfEnableHonkai3rdAutoCheckIn.value = false

                                sfNoGenshinAccount.value =
                                    !it.data.data.list.any { recordCard -> recordCard.game_id == Constant.GAME_ID_GENSHIN_IMPACT }
                                sfNoHonkaiSrAccount.value =
                                    !it.data.data.list.any { recordCard -> recordCard.game_id == Constant.GAME_ID_HONKAI_SR }

                                initGenshinDataInputField()
                                initHonkaiSrDataInputField()

                                it.data.data.list.forEach { recordCard ->
                                    when (recordCard.game_id) {
                                        Constant.GAME_ID_GENSHIN_IMPACT -> {
                                            log.e()
                                            sfGenshinUid.value = recordCard.game_role_id
                                            sfGenshinNickname.value = recordCard.nickname

                                            when (recordCard.region) {
                                                Constant.SERVER_OS_ASIA -> sfGenshinServer.value = Constant.Server.ASIA.pref
                                                Constant.SERVER_OS_USA -> sfGenshinServer.value = Constant.Server.USA.pref
                                                Constant.SERVER_OS_EURO -> sfGenshinServer.value = Constant.Server.EUROPE.pref
                                                Constant.SERVER_OS_CHT -> sfGenshinServer.value = Constant.Server.CHT.pref
                                            }

                                            sfEnableGenshinAutoCheckIn.value = true
                                        }
                                        Constant.GAME_ID_HONKAI_SR -> {
                                            log.e()
                                            sfHonkaiSrUid.value = recordCard.game_role_id
                                            sfHonkaiSrNickname.value = recordCard.nickname

                                            when (recordCard.region) {
                                                Constant.SERVER_PO_ASIA -> sfHonkaiSrServer.value = Constant.Server.ASIA.pref
                                                Constant.SERVER_PO_USA -> sfHonkaiSrServer.value = Constant.Server.USA.pref
                                                Constant.SERVER_PO_EURO -> sfHonkaiSrServer.value = Constant.Server.EUROPE.pref
                                                Constant.SERVER_PO_CHT -> sfHonkaiSrServer.value = Constant.Server.CHT.pref
                                            }

                                            sfEnableHonkaiSrAutoCheckIn.value = true
                                        }
                                        Constant.GAME_ID_HONKAI_3RD -> {
                                            log.e()
                                            sfEnableHonkai3rdAutoCheckIn.value = true
                                        }
                                        else -> { }
                                    }
                                }

                                if (it.data.data.list.any { gameRecordCard -> gameRecordCard.game_id == 2 })
                                    makeToast(resource.getString(R.string.msg_toast_get_uid_success))
                                else if (it.data.data.list.isNotEmpty()) {
                                    initGenshinDataInputField()
                                    makeToast(resource.getString(R.string.msg_toast_get_uid_error_genshin_data_not_exists))
                                }
                                else
                                    makeToast(resource.getString(R.string.msg_toast_get_uid_error_card_list_empty))

                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, it.code, it.data.retcode)
                                makeToast(String.format(resource.getString(R.string.msg_toast_get_uid_error_include_error_code), it.data.retcode))
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, it.code, null)
                            makeToast(resource.getString(R.string.msg_toast_common_network_error))
                        }
                    }
                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, null, null)
                        makeToast(resource.getString(R.string.msg_toast_get_uid_error))
                    }
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_GET_GAME_RECORD_CARD, null, null)
                        makeToast(resource.getString(R.string.msg_toast_common_body_null_error))
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
                                makeToast(resource.getString(R.string.msg_toast_change_data_switch_success))
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, it.code, it.data.retcode)
                                makeToast(String.format(resource.getString(R.string.msg_toast_change_data_switch_error_include_error_code), it.data.retcode))
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, it.code, null)
                            makeToast(resource.getString(R.string.msg_toast_common_network_error))
                        }
                    }
                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, null, null)
                        makeToast(resource.getString(R.string.msg_toast_change_data_switch_error))
                    }
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHANGE_DATA_SWITCH, null, null)
                        makeToast(resource.getString(R.string.msg_toast_common_body_null_error))
                    }
                }
            }
        }
    }

    private fun dailyNote(
        uid: String,
        server: String,
        cookie: String,
        ds: String
    ) {
        viewModelScope.launch {
            dailyNote.genshin(
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
                                makeToast(resource.getString(R.string.msg_toast_dailynote_success))

                                val account = Account(
                                    sfGenshinNickname.value,
                                    sfHoyolabCookie.value,
                                    sfGenshinUid.value,
                                    sfGenshinServer.value,
                                    sfHonkaiSrNickname.value,
                                    sfHonkaiSrUid.value,
                                    sfHonkaiSrServer.value,
                                    sfEnableGenshinAutoCheckIn.value,
                                    sfEnableHonkai3rdAutoCheckIn.value,
                                    sfEnableHonkaiSrAutoCheckIn.value,
                                    false
                                )

                                insertAccount(account)
                            }
                            Constant.RETCODE_ERROR_CHARACTOR_INFO -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                makeToast(resource.getString(R.string.msg_toast_dailynote_error_charactor_info))
                            }
                            Constant.RETCODE_ERROR_INTERNAL_DATABASE_ERROR -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                makeToast(resource.getString(R.string.msg_toast_dailynote_error_internal_database_error))
                            }
                            Constant.RETCODE_ERROR_TOO_MANY_REQUESTS -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                makeToast(resource.getString(R.string.msg_toast_dailynote_error_too_many_requests))
                            }
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN,
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN_2-> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                makeToast(resource.getString(R.string.msg_toast_dailynote_error_not_logged_in))
                            }
                            Constant.RETCODE_ERROR_NOT_LOGGED_IN_3 -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                makeToast(resource.getString(R.string.msg_toast_dailynote_error_not_logged_in_3))
                            }
                            Constant.RETCODE_ERROR_WRONG_ACCOUNT -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                makeToast(resource.getString(R.string.msg_toast_dailynote_error_wrong_account))
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
                                makeToast(resource.getString(R.string.msg_toast_dailynote_error_account_not_found))
                            }
                            Constant.RETCODE_ERROR_INVALID_LANGUAGE -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                makeToast(resource.getString(R.string.msg_toast_dailynote_error_invalid_language))
                            }
                            Constant.RETCODE_ERROR_INVALID_INPUT_FORMAT -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                makeToast(resource.getString(R.string.msg_toast_dailynote_error_invalid_input))
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, it.data.retcode)
                                makeToast(String.format(resource.getString(R.string.msg_toast_dailynote_error_include_error_code), it.data.retcode))
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, it.code, null)
                            makeToast(resource.getString(R.string.msg_toast_common_network_error))
                        }
                    }
                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, null, null)
                        makeToast(resource.getString(R.string.msg_toast_dailynote_error))
                    }
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_DAILY_NOTE, null,null)
                        makeToast(resource.getString(R.string.msg_toast_common_body_null_error))
                    }
                }
            }
        }
    }

    private fun insertAccount(
        account: Account
    ) {
        viewModelScope.launch {
            accountDao.insertAccount(account).collect {
                log.e()
                sendEvent(Event.FinishThisActivity())
            }
        }
    }

    fun onClickSetGenshinServer(server: Constant.Server) {
        log.e("server -> $server")
        sfGenshinServer.value = server.pref
    }

    fun onClickSetHonkaiSrServer(server: Constant.Server) {
        log.e("server -> $server")
        sfHonkaiSrServer.value = server.pref
    }

    fun makeDailyNotePublic() {
        log.e()
        makeBattleChroniclePublic(
            gameId = 2,
            switchId = 3,
            isPublic = true,
            cookie = sfHoyolabCookie.value,
            ds = CommonFunction.getGenshinDS()
        )
    }

    fun onClickGetCookie() {
        log.e()
        sendEvent(Event.GetCookie())
    }

    fun initGenshinDataInputField() {
        if (sfNoGenshinAccount.value) {
            sfGenshinNickname.value = randomGuestName()
            sfGenshinUid.value = "-" + (mCookieData["ltuid"]?:CommonFunction.getRandomNumber(1000000, 9999999))
        } else {
            sfGenshinNickname.value = ""
            sfGenshinUid.value = ""
        }
    }

    fun initHonkaiSrDataInputField() {
        sfHonkaiSrNickname.value = ""
        sfHonkaiSrUid.value = ""
    }

    fun onClickGetUid() {
        log.e()
        if (!sfHoyolabCookie.value.contains(("ltuid="))) {
            makeToast(resource.getString(R.string.msg_toast_get_uid_error_no_ltuid))
            return
        }

        getUid(
            mCookieData["ltuid"]?:"",
            sfHoyolabCookie.value,
            CommonFunction.getGenshinDS()
        )
    }

    fun onClickSave() {
        log.e()

        if (sfNoGenshinAccount.value) {
            insertAccount(
                Account.GUEST.copy(
                    nickname = sfGenshinNickname.value,
                    cookie = sfHoyolabCookie.value,
                    genshin_uid = sfGenshinUid.value,
                    honkai_sr_nickname = sfHonkaiSrNickname.value,
                    honkai_sr_uid = sfHonkaiSrUid.value,
                    honkai_sr_server = sfHonkaiSrServer.value,
                    enable_genshin_checkin = sfEnableGenshinAutoCheckIn.value,
                    enable_honkai3rd_checkin = sfEnableHonkai3rdAutoCheckIn.value,
                    enable_honkai_sr_checkin = sfEnableHonkaiSrAutoCheckIn.value
                )
            )
        } else {
            sfGenshinUid.value = sfGenshinUid.value.trim()
            sfHonkaiSrUid.value = sfHonkaiSrUid.value.trim()
            sfHoyolabCookie.value = sfHoyolabCookie.value.trim()

            dailyNote(
                sfGenshinUid.value,
                when (sfGenshinServer.value) {
                    Constant.PREF_SERVER_ASIA -> Constant.SERVER_OS_ASIA
                    Constant.PREF_SERVER_EUROPE -> Constant.SERVER_OS_EURO
                    Constant.PREF_SERVER_USA -> Constant.SERVER_OS_USA
                    Constant.PREF_SERVER_CHT -> Constant.SERVER_OS_CHT
                    else -> Constant.SERVER_OS_ASIA
                },
                sfHoyolabCookie.value,
                CommonFunction.getGenshinDS()
            )
        }
    }

    fun selectAccountByUid(uid: String) {
        log.e()

        viewModelScope.launch {
            accountDao.selectAccountByUid(uid).collect { account ->
                log.e(account)
                sfHoyolabCookie.value = account.cookie
                sfGenshinUid.value = account.genshin_uid
                sfGenshinNickname.value = account.nickname
                sfGenshinServer.value = account.server
                sfNoGenshinAccount.value = account.genshin_uid.contains("-")

                sfHonkaiSrUid.value = account.honkai_sr_uid
                sfHonkaiSrNickname.value = account.honkai_sr_nickname
                sfHonkaiSrServer.value = account.honkai_sr_server
                sfNoHonkaiSrAccount.value = account.honkai_sr_uid.isEmpty()

                sfEnableGenshinAutoCheckIn.value = account.enable_genshin_checkin
                sfEnableHonkai3rdAutoCheckIn.value = account.enable_honkai3rd_checkin
                sfEnableHonkaiSrAutoCheckIn.value = account.enable_honkai_sr_checkin

                if (account.genshin_uid.contains("-")) sfNoGenshinAccount.value = true
            }
        }
    }

    private fun randomGuestName(): String {
        return listOf(
            resource.getString(R.string.traveler),
            resource.getString(R.string.captain),
            resource.getString(R.string.pioneer),
        ).random()
    }
}