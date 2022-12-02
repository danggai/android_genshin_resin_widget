package danggai.app.presentation.ui.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.DayTimeMapper
import danggai.app.presentation.util.Event
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
import java.util.*
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val resource: ResourceProviderRepository,
    private val accountDao: AccountDaoUseCase,
    private val preference: PreferenceManagerRepository,
): BaseViewModel() {
    val dao = accountDao

    val sfAutoRefreshPeriod = MutableStateFlow(15L)

    val sfAccountList: StateFlow<List<Account>> =
        accountDao.selectAllAccountFlow()
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
    val sfEnableNotiDailyYet = MutableStateFlow(false)
    val sfEnableNotiWeeklyYet = MutableStateFlow(false)
    var sfNotiDailyYetTime = MutableStateFlow(21)
    var sfNotiWeeklyYetDay = MutableStateFlow(Calendar.SUNDAY)
    var sfNotiWeeklyYetTime = MutableStateFlow(21)

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
            sfEnableNotiDailyYet.value = it.notiDailyYet
            sfEnableNotiWeeklyYet.value = it.notiWeeklyYet
            sfNotiDailyYetTime.value = it.notiDailyYetTime
            sfNotiWeeklyYetTime.value = it.notiWeeklyYetTime
            sfNotiWeeklyYetDay.value = it.notiWeeklyYetDay
        }

        preference.getCheckInSettings().let {
            sfEnableNotiCheckinSuccess.value = it.notiCheckInSuccess
            sfEnableNotiCheckinFailed.value = it.notiCheckInFailed
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            accountDao.deleteAccount(account.genshin_uid).collect {
                log.e()
                makeToast(account.nickname + " " + resource.getString(R.string.msg_toast_hoyolab_account_deleted))
            }
        }
    }

    fun onClickSave() {
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
                sfEnableNotiHomeCoinFull.value,
                sfEnableNotiDailyYet.value,
                sfNotiDailyYetTime.value,
                sfEnableNotiWeeklyYet.value,
                sfNotiWeeklyYetDay.value,
                sfNotiWeeklyYetTime.value,
            )
        )

        preference.setCheckInSettings(
            CheckInSettings(
                true,     // deprecated
                true,   // deprecated
                sfEnableNotiCheckinSuccess.value,
                sfEnableNotiCheckinFailed.value
            )
        )

        makeToast(resource.getString(R.string.msg_toast_save_done))
    }

    fun onClickCheckIn() {
        startCheckIn()
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

    fun setDailyCommissionNotiTime(time: String) {
        log.e(time)
        sfNotiDailyYetTime.value = DayTimeMapper.timeStringToInt(resource, time)
    }

    fun setWeeklyCommissionNotiDay(day: String) {
        log.e(day)
        sfNotiWeeklyYetDay.value = DayTimeMapper.weekOfDayStringToInt(resource, day)
    }

    fun setWeeklyCommissionNotiTime(time: String) {
        log.e(time)
        sfNotiWeeklyYetTime.value = DayTimeMapper.timeStringToInt(resource, time)
    }



    private fun sendWidgetSyncBroadcast(dailyNote: DailyNoteData) {
        log.e()

//        preference.setStringRecentSyncTime(TimeFunction.getSyncTimeString())
//
//        val expeditionTime: String = CommonFunction.getExpeditionTime(dailyNote)
//        preference.setStringExpeditionTime(expeditionTime)
//
//        preference.setDailyNote(dailyNote)

        if (sfAutoRefreshPeriod.value == -1L) {
            sendEvent(Event.StartShutRefreshWorker(false))
        } else {
            sendEvent(Event.StartShutRefreshWorker(true))
        }
    }

    private fun startCheckIn() {
        log.e()
        sendEvent(Event.StartShutCheckInWorker(true))

        makeToast(resource.getString(R.string.msg_toast_save_done_check_in))
    }

}