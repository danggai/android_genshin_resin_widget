package danggai.app.presentation.ui.main

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.TimeFunction
import danggai.app.presentation.util.log
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.usecase.AccountDaoUseCase
import danggai.domain.local.CheckInSettings
import danggai.domain.local.DailyNoteSettings
import danggai.domain.network.dailynote.entity.DailyNoteData
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.resource.repository.ResourceProviderRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val resource: ResourceProviderRepository,
    private val accountDao: AccountDaoUseCase,
    private val preference: PreferenceManagerRepository,
): BaseViewModel() {
    val sfAutoRefreshPeriod = MutableStateFlow(15L)

    val sfAccountList: StateFlow<List<Account>> =
        accountDao.getAllAccount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )

    val sfEnableNotiEach40Resin = MutableStateFlow(false)
    val sfEnableNoti140Resin = MutableStateFlow(false)
    val sfEnableNotiCustomResin = MutableStateFlow(false)
    val sfCustomNotiResin = MutableStateFlow("0")
    val sfEnableNotiExpeditionDone = MutableStateFlow(false)
    val sfEnableNotiHomeCoinFull = MutableStateFlow(false)

    val sfEnableNotiCheckinSuccess = MutableStateFlow(false)
    val sfEnableNotiCheckinFailed = MutableStateFlow(false)

    val sfAccountListRefreshSwitch = MutableStateFlow(false)

    val sfDeleteAccount = MutableSharedFlow<Account>()

    fun initUI() {
        preference.getDailyNoteSettings().let {
            sfAutoRefreshPeriod.value = it.autoRefreshPeriod
            sfEnableNotiEach40Resin.value = it.notiEach40Resin
            sfEnableNoti140Resin.value = it.noti140Resin
            sfEnableNotiCustomResin.value = it.notiCustomResin
            sfCustomNotiResin.value = it.customResin.toString()
            sfEnableNotiExpeditionDone.value = it.notiExpedition
            sfEnableNotiHomeCoinFull.value = it.notiHomeCoin
        }

        preference.getCheckInSettings().let {
            sfEnableNotiCheckinSuccess.value = it.notiCheckInSuccess
            sfEnableNotiCheckinFailed.value = it.notiCheckInFailed
        }
    }

    fun deleteAccount(uid: String) {
        viewModelScope.launch {
            accountDao.deleteAccount(uid).collect {
                log.e()
                makeToast(resource.getString(R.string.msg_toast_hoyolab_account_deleted))
            }
        }
    }

    fun onClickSave() {
        log.e()
        saveWidgetData()

        // 수동출석으로 전환
        log.e()
//        if (sfCookie.value.isEmpty())  {
//            saveCheckInData(false)
//        } else {
//            sfCookie.value = sfCookie.value.trim()
//            saveCheckInData(true)
//            startCheckIn()
//        }
    }

    private fun saveWidgetData() {
        log.e()

        val customNotiResin: Int = try {
            if (sfCustomNotiResin.value.isEmpty()
                || sfCustomNotiResin.value.toInt() < 0) 0
            else if (sfCustomNotiResin.value.toInt() > Constant.MAX_RESIN) {
                sfCustomNotiResin.value = Constant.MAX_RESIN.toString()
                Constant.MAX_RESIN
            } else sfCustomNotiResin.value.toInt()
        } catch (e:java.lang.Exception) {
            0
        }

        preference.setDailyNoteSettings(
            DailyNoteSettings(
                0,      // deprecated
                sfAutoRefreshPeriod.value,
                sfEnableNotiEach40Resin.value,
                sfEnableNoti140Resin.value,
                sfEnableNotiCustomResin.value,
                customNotiResin,
                sfEnableNotiExpeditionDone.value,
                sfEnableNotiHomeCoinFull.value
            )
        )

        makeToast(resource.getString(R.string.msg_toast_save_done))
    }

    fun onClickWidgetRefreshNotWork() {
        log.e()
        sendEvent(Event.WidgetRefreshNotWork())
    }

    fun onClickSetAutoRefreshPeriod(period: Long) {
        log.e("period -> $period")
        sfAutoRefreshPeriod.value = period
    }

    fun onClickDeleteAccount(account: Account) {
        log.e()

        sfDeleteAccount.emitInVmScope(account)
    }

    fun onClickWidgetDesign() {
        log.e()
        sendEvent(Event.StartWidgetDesignActivity())
    }

    fun onClickNewHoyolabAccount() {
        log.e()
        sendEvent(Event.StartNewHoyolabAccountActivity())
    }

    fun onClickManageAccount(account: Account) {
        log.e()
        sendEvent(Event.StartManageAccount(account))
    }

    fun onClickChangeLanguage() {
        log.e()
        sendEvent(Event.ChangeLanguage())
    }






    private fun saveCheckInData(isDataValid: Boolean) {
        if (isDataValid) {
            log.e()
            preference.setBooleanIsValidUserData(true)

            preference.setCheckInSettings(
                CheckInSettings(
                    false,
                    false,
                    sfEnableNotiCheckinSuccess.value,
                    sfEnableNotiCheckinFailed.value
                )
            )

//            if (!sfEnableGenshinAutoCheckIn.value &&
//                !sfEnableHonkai3rdAutoCheckIn.value)
//                sendEvent(Event.StartShutCheckInWorker(false))

            makeToast(resource.getString(R.string.msg_toast_save_done_check_in))
        }
    }

    private fun sendWidgetSyncBroadcast(dailyNote: DailyNoteData) {
        log.e()

        preference.setStringRecentSyncTime(TimeFunction.getSyncTimeString())

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

}