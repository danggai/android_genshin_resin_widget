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
    val sfCustomNotiResin = MutableStateFlow("")
    val sfEnableNotiExpeditionDone = MutableStateFlow(false)
    val sfEnableNotiHomeCoinFull = MutableStateFlow(false)
    val sfEnableNotiParamReach = MutableStateFlow(false)

    val sfEnableNotiCheckinSuccess = MutableStateFlow(false)
    val sfEnableNotiCheckinFailed = MutableStateFlow(false)
    val sfEnableNotiDailyYet = MutableStateFlow(false)
    val sfEnableNotiWeeklyYet = MutableStateFlow(false)
    var sfNotiDailyYetTime = MutableStateFlow(21)
    var sfNotiWeeklyYetDay = MutableStateFlow(Calendar.SUNDAY)
    var sfNotiWeeklyYetTime = MutableStateFlow(21)

    val sfEnableNotiEach40TrailPower = MutableStateFlow(false)
    val sfEnableNoti230TrailPower = MutableStateFlow(false)
    val sfEnableNotiCustomTrailPower = MutableStateFlow(false)
    val sfCustomNotiTrailPower = MutableStateFlow("")
    val sfEnableNotiHonkaiSrExpeditionDone = MutableStateFlow(false)

    val sfEnableNotiEach40Battery = MutableStateFlow(false)
    val sfEnableNotiEach60Battery = MutableStateFlow(false)
    val sfEnableNoti230Battery = MutableStateFlow(false)
    val sfEnableNotiCustomBattery = MutableStateFlow(false)
    val sfCustomNotiBattery = MutableStateFlow("")

    val sfAccountListRefreshSwitch = MutableStateFlow(false)

    val sfDeleteAccount = MutableSharedFlow<Account>()
    val sfShowDialogDailyWeeklyYet = MutableSharedFlow<Boolean>()

    fun initUI() {
        preference.getDailyNoteSettings().let {
            sfAutoRefreshPeriod.value = it.autoRefreshPeriod
            sfEnableNotiEach40Resin.value = it.notiEach40Resin
            sfEnableNoti140Resin.value = it.noti140Resin
            sfEnableNotiCustomResin.value = it.notiCustomResin
            sfCustomNotiResin.value = if (it.customResin != 0) it.customResin.toString() else ""
            sfEnableNotiExpeditionDone.value = it.notiExpedition
            sfEnableNotiHomeCoinFull.value = it.notiHomeCoin
            sfEnableNotiParamReach.value = it.notiParamTrans
            sfEnableNotiDailyYet.value = it.notiDailyYet
            sfEnableNotiWeeklyYet.value = it.notiWeeklyYet
            sfNotiDailyYetTime.value = it.notiDailyYetTime
            sfNotiWeeklyYetTime.value = it.notiWeeklyYetTime
            sfNotiWeeklyYetDay.value = it.notiWeeklyYetDay

            sfEnableNotiEach40TrailPower.value = it.notiEach40TrailPower
            sfEnableNoti230TrailPower.value = it.noti170TrailPower
            sfEnableNotiCustomTrailPower.value = it.notiCustomTrailPower
            sfCustomNotiTrailPower.value = if (it.customTrailPower != 0) it.customTrailPower.toString() else ""
            sfEnableNotiHonkaiSrExpeditionDone.value = it.notiExpeditionHonkaiSr

            sfEnableNotiEach40Battery.value = it.notiEach40Battery
            sfEnableNotiEach60Battery.value = it.notiEach60Battery
            sfEnableNoti230Battery.value = it.noti230Battery
            sfEnableNotiCustomBattery.value = it.notiCustomBattery
            sfCustomNotiBattery.value = if (it.customBattery != 0) it.customBattery.toString() else ""
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

        /* 커스텀 알림에 사용 할 스테미나를 Int화 및 핸들링하는 함수 */
        fun stringToIntCustomStamina(targetStringFlow: MutableStateFlow<String>, maxStamina: Int): Int {
            return try {
                val stamina = targetStringFlow.value.toIntOrNull()

                when {
                    stamina == null || stamina < 0 -> 0
                    stamina > maxStamina -> {
                        targetStringFlow.value = maxStamina.toString()
                        maxStamina
                    }
                    else -> stamina
                }
            } catch (e: Exception) { 0 }
        }

        val customNotiResin: Int = stringToIntCustomStamina(sfCustomNotiResin, Constant.MAX_RESIN)
        val customNotiTrailPower: Int = stringToIntCustomStamina(sfCustomNotiTrailPower, Constant.MAX_TRAILBLAZE_POWER)
        val customNotiBattery: Int = stringToIntCustomStamina(sfCustomNotiBattery, Constant.MAX_BATTERY)

        preference.setDailyNoteSettings(
            DailyNoteSettings(
                sfAutoRefreshPeriod.value,

                sfEnableNotiEach40Resin.value,
                sfEnableNoti140Resin.value,
                sfEnableNotiCustomResin.value,
                customNotiResin,
                sfEnableNotiExpeditionDone.value,
                sfEnableNotiHomeCoinFull.value,
                sfEnableNotiParamReach.value,
                sfEnableNotiDailyYet.value,
                sfNotiDailyYetTime.value,
                sfEnableNotiWeeklyYet.value,
                sfNotiWeeklyYetDay.value,
                sfNotiWeeklyYetTime.value,

                sfEnableNotiEach40TrailPower.value,
                sfEnableNoti230TrailPower.value,
                sfEnableNotiCustomTrailPower.value,
                customNotiTrailPower,
                sfEnableNotiHonkaiSrExpeditionDone.value,

                sfEnableNotiEach40Battery.value,
                sfEnableNotiEach60Battery.value,
                sfEnableNoti230Battery.value,
                sfEnableNotiCustomBattery.value,
                customNotiBattery,
            )
        )

        preference.setCheckInSettings(
            CheckInSettings(
                sfEnableNotiCheckinSuccess.value,
                sfEnableNotiCheckinFailed.value
            )
        )

        makeToast(resource.getString(R.string.msg_toast_save_done))
    }

    fun onClickCheckIn() {
        log.e()
        sendEvent(Event.StartShutCheckInWorker(true))

        makeToast(resource.getString(R.string.msg_toast_save_done_check_in))
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

    fun onClickDailyCommissionYetNoti() {
        if (sfEnableNotiDailyYet.value) {
            log.e()
            sfShowDialogDailyWeeklyYet.emitInVmScope(true)
        }
    }

    fun onClickWeeklyBossYetNoti() {
        if (sfEnableNotiWeeklyYet.value) {
            log.e()
            sfShowDialogDailyWeeklyYet.emitInVmScope(false)
        }
    }
}