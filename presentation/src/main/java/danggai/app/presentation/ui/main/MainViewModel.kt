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
import kotlinx.coroutines.FlowPreview
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

    val sfEnableNotiDailyYetHonkaiSr = MutableStateFlow(false)
    val sfNotiDailyYetTimeHonkaiSr = MutableStateFlow(21)
    val sfEnableNotiWeeklyYetHonkaiSr = MutableStateFlow(false)
    val sfNotiWeeklyYetDayHonkaiSr = MutableStateFlow(Calendar.SUNDAY)
    val sfNotiWeeklyYetTimeHonkaiSr = MutableStateFlow(21)

    val sfEnableNotiDailyYetZZZ = MutableStateFlow(false)
    val sfNotiDailyYetTimeZZZ = MutableStateFlow(21)
    val sfEnableNotiWeeklyYetZZZ = MutableStateFlow(false)
    val sfNotiWeeklyYetDayZZZ = MutableStateFlow(Calendar.SUNDAY)
    val sfNotiWeeklyYetTimeZZZ = MutableStateFlow(21)

    val sfEnableNotiReserveFullHonkaiSr = MutableStateFlow(false)
    val sfEnableNotiPeriodScoreHonkaiSr = MutableStateFlow(false)
    val sfNotiPeriodScoreDayHonkaiSr = MutableStateFlow(Calendar.SUNDAY)
    val sfNotiPeriodScoreTimeHonkaiSr = MutableStateFlow(21)
    val sfEnableNotiMemberClaimZZZ = MutableStateFlow(false)
    val sfNotiMemberClaimTimeZZZ = MutableStateFlow(21)
    val sfEnableNotiMemberExpireZZZ = MutableStateFlow(false)
    val sfNotiMemberExpireDaysZZZ = MutableStateFlow(3)
    val sfEnableNotiCafeZZZ = MutableStateFlow(false)
    val sfNotiCafeTimeZZZ = MutableStateFlow(21)

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
    val sfShowDialogCustomNoti = MutableSharedFlow<CustomNotiType>()

    val sfExpandGenshin = MutableStateFlow(true)
    val sfExpandHonkaiSr = MutableStateFlow(true)
    val sfExpandZZZ = MutableStateFlow(true)
    val sfExpandCheckIn = MutableStateFlow(true)

    private data class GameUsage(
        val genshin: Boolean,
        val honkaiSr: Boolean,
        val zzz: Boolean,
        val checkIn: Boolean
    )

    init {
        viewModelScope.launch {
            accountDao.selectAllAccountFlow()
                .map { getGameUsage(it) }
                .distinctUntilChanged()
                .collect {
                    sfExpandGenshin.value = it.genshin
                    sfExpandHonkaiSr.value = it.honkaiSr
                    sfExpandZZZ.value = it.zzz
                    sfExpandCheckIn.value = it.checkIn
                }
        }
    }

    private fun getGameUsage(accountList: List<Account>): GameUsage =
        GameUsage(
            genshin = accountList.any { !it.genshin_uid.contains("-") },
            honkaiSr = accountList.any { it.honkai_sr_uid.isNotEmpty() },
            zzz = accountList.any { it.zzz_uid.isNotEmpty() },
            checkIn = accountList.any {
                it.enable_genshin_checkin || it.enable_honkai3rd_checkin ||
                        it.enable_honkai_sr_checkin || it.enable_zzz_checkin
            }
        )

    fun onClickToggleGenshin() {
        sfExpandGenshin.value = !sfExpandGenshin.value
    }

    fun onClickToggleHonkaiSr() {
        sfExpandHonkaiSr.value = !sfExpandHonkaiSr.value
    }

    fun onClickToggleZZZ() {
        sfExpandZZZ.value = !sfExpandZZZ.value
    }

    fun onClickToggleCheckIn() {
        sfExpandCheckIn.value = !sfExpandCheckIn.value
    }
    init {
        observeAutoSave()
    }

    /* 이전 버전에서 저장된 설정에는 새 항목이 없어 0으로 읽히므로 기본값으로 대체 */
    private fun timeOrDefault(time: Int): Int = if (time == 0) 21 else time

    private fun dayOrDefault(day: Int): Int = if (day == 0) Calendar.SUNDAY else day

    private fun daysOrDefault(days: Int): Int = if (days == 0) 3 else days

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

            sfEnableNotiDailyYetHonkaiSr.value = it.notiDailyYetHonkaiSr
            sfNotiDailyYetTimeHonkaiSr.value = timeOrDefault(it.notiDailyYetTimeHonkaiSr)
            sfEnableNotiWeeklyYetHonkaiSr.value = it.notiWeeklyYetHonkaiSr
            sfNotiWeeklyYetDayHonkaiSr.value = dayOrDefault(it.notiWeeklyYetDayHonkaiSr)
            sfNotiWeeklyYetTimeHonkaiSr.value = timeOrDefault(it.notiWeeklyYetTimeHonkaiSr)

            sfEnableNotiDailyYetZZZ.value = it.notiDailyYetZZZ
            sfNotiDailyYetTimeZZZ.value = timeOrDefault(it.notiDailyYetTimeZZZ)
            sfEnableNotiWeeklyYetZZZ.value = it.notiWeeklyYetZZZ
            sfNotiWeeklyYetDayZZZ.value = dayOrDefault(it.notiWeeklyYetDayZZZ)
            sfNotiWeeklyYetTimeZZZ.value = timeOrDefault(it.notiWeeklyYetTimeZZZ)

            sfEnableNotiReserveFullHonkaiSr.value = it.notiReserveFullHonkaiSr
            sfEnableNotiPeriodScoreHonkaiSr.value = it.notiPeriodScoreHonkaiSr
            sfNotiPeriodScoreDayHonkaiSr.value = dayOrDefault(it.notiPeriodScoreDayHonkaiSr)
            sfNotiPeriodScoreTimeHonkaiSr.value = timeOrDefault(it.notiPeriodScoreTimeHonkaiSr)

            sfEnableNotiMemberClaimZZZ.value = it.notiMemberClaimZZZ
            sfNotiMemberClaimTimeZZZ.value = timeOrDefault(it.notiMemberClaimTimeZZZ)
            sfEnableNotiMemberExpireZZZ.value = it.notiMemberExpireZZZ
            sfNotiMemberExpireDaysZZZ.value = daysOrDefault(it.notiMemberExpireDaysZZZ)
            sfEnableNotiCafeZZZ.value = it.notiCafeZZZ
            sfNotiCafeTimeZZZ.value = timeOrDefault(it.notiCafeTimeZZZ)
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

    @OptIn(FlowPreview::class)
    private fun observeAutoSave() {
        viewModelScope.launch {
            merge(
                sfCustomNotiResin,
                sfCustomNotiTrailPower,
                sfCustomNotiBattery,
                sfEnableNotiDailyYetHonkaiSr,
                sfNotiDailyYetTimeHonkaiSr,
                sfEnableNotiWeeklyYetHonkaiSr,
                sfNotiWeeklyYetDayHonkaiSr,
                sfNotiWeeklyYetTimeHonkaiSr,
                sfEnableNotiDailyYetZZZ,
                sfNotiDailyYetTimeZZZ,
                sfEnableNotiWeeklyYetZZZ,
                sfNotiWeeklyYetDayZZZ,
                sfNotiWeeklyYetTimeZZZ,
                sfEnableNotiReserveFullHonkaiSr,
                sfEnableNotiPeriodScoreHonkaiSr,
                sfNotiPeriodScoreDayHonkaiSr,
                sfNotiPeriodScoreTimeHonkaiSr,
                sfEnableNotiMemberClaimZZZ,
                sfNotiMemberClaimTimeZZZ,
                sfEnableNotiMemberExpireZZZ,
                sfNotiMemberExpireDaysZZZ,
                sfEnableNotiCafeZZZ,
                sfNotiCafeTimeZZZ,
                sfAutoRefreshPeriod,
                sfEnableNotiEach40Resin,
                sfEnableNoti140Resin,
                sfEnableNotiCustomResin,
                sfEnableNotiExpeditionDone,
                sfEnableNotiHomeCoinFull,
                sfEnableNotiParamReach,
                sfEnableNotiDailyYet,
                sfNotiDailyYetTime,
                sfEnableNotiWeeklyYet,
                sfNotiWeeklyYetDay,
                sfNotiWeeklyYetTime,
                sfEnableNotiEach40TrailPower,
                sfEnableNoti230TrailPower,
                sfEnableNotiCustomTrailPower,
                sfEnableNotiHonkaiSrExpeditionDone,
                sfEnableNotiEach40Battery,
                sfEnableNotiEach60Battery,
                sfEnableNoti230Battery,
                sfEnableNotiCustomBattery,
                sfEnableNotiCheckinSuccess,
                sfEnableNotiCheckinFailed,
            )
                .debounce(500L)
                .collect { saveIfChanged() }
        }
    }

    /* 커스텀 알림에 사용 할 스테미나를 Int화 및 핸들링하는 함수 */
    private fun stringToIntCustomStamina(targetStringFlow: MutableStateFlow<String>, maxStamina: Int): Int {
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

    private fun makeDailyNoteSettings(): DailyNoteSettings {
        val customNotiResin: Int = stringToIntCustomStamina(sfCustomNotiResin, Constant.MAX_RESIN)
        val customNotiTrailPower: Int = stringToIntCustomStamina(sfCustomNotiTrailPower, Constant.MAX_TRAILBLAZE_POWER)
        val customNotiBattery: Int = stringToIntCustomStamina(sfCustomNotiBattery, Constant.MAX_BATTERY)

        return DailyNoteSettings(
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

            sfEnableNotiDailyYetHonkaiSr.value,
            sfNotiDailyYetTimeHonkaiSr.value,
            sfEnableNotiWeeklyYetHonkaiSr.value,
            sfNotiWeeklyYetDayHonkaiSr.value,
            sfNotiWeeklyYetTimeHonkaiSr.value,

            sfEnableNotiDailyYetZZZ.value,
            sfNotiDailyYetTimeZZZ.value,
            sfEnableNotiWeeklyYetZZZ.value,
            sfNotiWeeklyYetDayZZZ.value,
            sfNotiWeeklyYetTimeZZZ.value,

            sfEnableNotiReserveFullHonkaiSr.value,
            sfEnableNotiPeriodScoreHonkaiSr.value,
            sfNotiPeriodScoreDayHonkaiSr.value,
            sfNotiPeriodScoreTimeHonkaiSr.value,
            sfEnableNotiMemberClaimZZZ.value,
            sfNotiMemberClaimTimeZZZ.value,
            sfEnableNotiMemberExpireZZZ.value,
            sfNotiMemberExpireDaysZZZ.value,
            sfEnableNotiCafeZZZ.value,
            sfNotiCafeTimeZZZ.value,
        )
    }

    private fun makeCheckInSettings() =
        CheckInSettings(
            sfEnableNotiCheckinSuccess.value,
            sfEnableNotiCheckinFailed.value
        )

    fun saveIfChanged() {
        val dailyNoteSettings = makeDailyNoteSettings()
        val checkInSettings = makeCheckInSettings()
        val savedDailyNoteSettings = preference.getDailyNoteSettings()

        if (dailyNoteSettings == savedDailyNoteSettings &&
            checkInSettings == preference.getCheckInSettings()
        ) return

        log.e()
        preference.setDailyNoteSettings(dailyNoteSettings)
        preference.setCheckInSettings(checkInSettings)

        if (dailyNoteSettings.autoRefreshPeriod != savedDailyNoteSettings.autoRefreshPeriod) {
            sendEvent(Event.StartShutRefreshWorker(true))
        }
    }

    private fun getCustomNotiEnableFlow(type: CustomNotiType): MutableStateFlow<Boolean> =
        when (type) {
            CustomNotiType.RESIN -> sfEnableNotiCustomResin
            CustomNotiType.TRAIL_POWER -> sfEnableNotiCustomTrailPower
            CustomNotiType.BATTERY -> sfEnableNotiCustomBattery
        }

    private fun getCustomNotiValueFlow(type: CustomNotiType): MutableStateFlow<String> =
        when (type) {
            CustomNotiType.RESIN -> sfCustomNotiResin
            CustomNotiType.TRAIL_POWER -> sfCustomNotiTrailPower
            CustomNotiType.BATTERY -> sfCustomNotiBattery
        }

    fun getCustomNotiValue(type: CustomNotiType): String = getCustomNotiValueFlow(type).value

    fun onClickCustomNoti(type: CustomNotiType) {
        if (getCustomNotiEnableFlow(type).value) sfShowDialogCustomNoti.emitInVmScope(type)
    }

    fun confirmCustomNoti(type: CustomNotiType, input: String) {
        val value = input.toIntOrNull()?.coerceAtMost(type.max)

        if (value == null || value <= 0) {
            cancelCustomNoti(type)
            return
        }

        getCustomNotiValueFlow(type).value = value.toString()
        getCustomNotiEnableFlow(type).value = true
    }

    fun cancelCustomNoti(type: CustomNotiType) {
        getCustomNotiEnableFlow(type).value = false
    }
    fun onClickAddWidget() {
        log.e()
        sendEvent(Event.ShowAddWidgetDialog())
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

    fun timeStringToInt(time: String): Int = DayTimeMapper.timeStringToInt(resource, time)

    fun weekOfDayStringToInt(day: String): Int = DayTimeMapper.weekOfDayStringToInt(resource, day)

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
