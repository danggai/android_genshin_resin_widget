package danggai.app.presentation.ui.design

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.PlayableCharacters
import danggai.app.presentation.util.log
import danggai.domain.core.ApiResult
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.LocalCharacter
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.network.character.usecase.CharacterUseCase
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.resource.repository.ResourceProviderRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetDesignViewModel @Inject constructor(
    private val preference: PreferenceManagerRepository,
    private val resource: ResourceProviderRepository,
    private val characters: CharacterUseCase
) : BaseViewModel() {
    val sfApplySavedData = MutableSharedFlow<Boolean>()
    val sfStartSelectFragment = MutableSharedFlow<Boolean>()
    val sfFinishSelectFragment = MutableSharedFlow<Boolean>()

    val sfProgress = MutableStateFlow(false)

    val sfSelectedPreview = MutableStateFlow(0)

    val sfWidgetTheme = MutableStateFlow(Constant.PREF_WIDGET_THEME_AUTOMATIC)
    val sfTransparency = MutableStateFlow(Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY)

    val sfResinTimeNotation = MutableStateFlow(Constant.PREF_TIME_NOTATION_REMAIN_TIME)
    val sfResinImageVisibility = MutableStateFlow(Constant.PREF_WIDGET_RESIN_IMAGE_VISIBLE)
    val sfResinFontSize = MutableStateFlow(Constant.PREF_DEFAULT_WIDGET_RESIN_FONT_SIZE)
    val sfResinUidVisibility = MutableStateFlow(false)
    val sfResinNameVisibility = MutableStateFlow(false)

    val sfDetailTimeNotation = MutableStateFlow(Constant.PREF_TIME_NOTATION_REMAIN_TIME)
    val sfResinDataVisibility = MutableStateFlow(true)
    val sfDailyCommissionDataVisibility = MutableStateFlow(true)
    val sfWeeklyBossDataVisibility = MutableStateFlow(true)
    val sfRealmCurrencyDataVisibility = MutableStateFlow(true)
    val sfExpeditionDataVisibility = MutableStateFlow(true)
    val sfTransformerDataVisibility = MutableStateFlow(true)

    val sfTrailBlazepowerDataVisibility = MutableStateFlow(true)
    val sfDailyTrainingDataVisibility = MutableStateFlow(true)
    val sfEchoOfWarDataVisibility = MutableStateFlow(true)
    val sfSimulatedUniverseDataVisibility = MutableStateFlow(true)
    val sfSimulatedUniverseClearTimeVisibility = MutableStateFlow(true)
    val sfAssignmentTimeDataVisibility = MutableStateFlow(true)

    val sfBatteryDataVisibility = MutableStateFlow(true)
    val sfEngagementTodayDataVisibility = MutableStateFlow(true)
    val sfScratchCardDataVisibility = MutableStateFlow(true)
    val sfVideoStoreManagementDataVisibility = MutableStateFlow(true)

    val sfFontSizeDetail = MutableStateFlow(Constant.PREF_DEFAULT_WIDGET_DETAIL_FONT_SIZE)
    val sfDetailUidVisibility = MutableStateFlow(false)
    val sfDetailNameVisibility = MutableStateFlow(false)

    val sfCharacterListRefreshSwitch = MutableStateFlow(false)

    private var _sfCharacterList: MutableStateFlow<MutableList<LocalCharacter>> =
        MutableStateFlow(mutableListOf())
    val sfCharacterList: MutableStateFlow<MutableList<LocalCharacter>>
        get() = _sfCharacterList

    private var _sfSelectedCharacterList: MutableStateFlow<MutableList<LocalCharacter>> =
        MutableStateFlow(mutableListOf())
    val sfSelectedCharacterList: MutableStateFlow<MutableList<LocalCharacter>>
        get() = _sfSelectedCharacterList

    private var _selectedCharacterIdList: MutableList<Int> = mutableListOf()
    val selectedCharacterIdList: MutableList<Int>
        get() = _selectedCharacterIdList

    fun initUi() {
        preference.getResinWidgetDesignSettings().let {
            sfWidgetTheme.value = it.widgetTheme
            sfTransparency.value = it.backgroundTransparency
            sfResinTimeNotation.value = it.timeNotation
            sfResinFontSize.value = it.fontSize
            sfResinImageVisibility.value = it.resinImageVisibility
            sfResinUidVisibility.value = it.uidVisibility
            sfResinNameVisibility.value = it.nameVisibility
        }

        preference.getDetailWidgetDesignSettings().let {
            sfDetailTimeNotation.value = it.timeNotation
            sfFontSizeDetail.value = it.fontSize
            sfResinDataVisibility.value = it.resinDataVisibility
            sfDailyCommissionDataVisibility.value = it.dailyCommissinDataVisibility
            sfWeeklyBossDataVisibility.value = it.weeklyBossDataVisibility
            sfRealmCurrencyDataVisibility.value = it.realmCurrencyDataVisibility
            sfExpeditionDataVisibility.value = it.expeditionDataVisibility
            sfTransformerDataVisibility.value = it.transformerDataVisibility

            sfTrailBlazepowerDataVisibility.value = it.trailBlazepowerDataVisibility
            sfDailyTrainingDataVisibility.value = it.dailyTrainingDataVisibility
            sfEchoOfWarDataVisibility.value = it.echoOfWarDataVisibility
            sfSimulatedUniverseDataVisibility.value = it.simulatedUniverseDataVisibility
            sfSimulatedUniverseClearTimeVisibility.value = it.simulatedUniverseClearTimeVisibility
            sfAssignmentTimeDataVisibility.value = it.assignmentTimeDataVisibility

            sfBatteryDataVisibility.value = it.batteryDataVisibility
            sfEngagementTodayDataVisibility.value = it.engagementTodayDataVisibility
            sfScratchCardDataVisibility.value = it.scratchCardDataVisibility
            sfVideoStoreManagementDataVisibility.value = it.videoStoreManagementDataVisibility

            sfDetailUidVisibility.value = it.uidVisibility
            sfDetailNameVisibility.value = it.nameVisibility

        }

        preference.getSelectedCharacterIdList().let {
            log.e(it)

            for (pbc in PlayableCharacters) {
                if (pbc.id in it) {
                    _sfSelectedCharacterList.value.add(pbc)
                }
            }

            _selectedCharacterIdList = it.toMutableList()
            sfCharacterListRefreshSwitch.value = !sfCharacterListRefreshSwitch.value
        }
    }

    private fun refreshCharacters(
        roleId: String,
        server: String,
        lang: String,
        cookie: String,
        ds: String
    ) {
        viewModelScope.launch {
            characters(
                roleId = roleId,
                server = server,
                lang = lang,
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
                                val list = mutableListOf<LocalCharacter>()

                                for (avatar in it.data.data.avatars) {
                                    for (pbc in PlayableCharacters) {
                                        if (avatar.id == pbc.id
                                            && pbc.id != Constant.ID_LUMINE
                                            && pbc.id != Constant.ID_AITHER
                                        ) list.add(pbc)
                                    }
                                }

                                _sfCharacterList.value = list

                                sfCharacterListRefreshSwitch.value =
                                    !sfCharacterListRefreshSwitch.value

                                sfStartSelectFragment.emitInVmScope(true)
                            }

                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(
                                    Constant.API_NAME_CHARACTERS,
                                    null,
                                    null
                                )
                                makeToast(resource.getString(R.string.msg_toast_get_characters_error))
                            }
                        }
                    }

                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(
                                Constant.API_NAME_CHARACTERS,
                                it.code,
                                null
                            )
                            makeToast(resource.getString(R.string.msg_toast_common_network_error))
                        }
                    }

                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_CHARACTERS,
                            null,
                            null
                        )
                        makeToast(resource.getString(R.string.msg_toast_get_characters_error))
                    }

                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(
                            Constant.API_NAME_CHARACTERS,
                            null,
                            null
                        )
                        makeToast(resource.getString(R.string.msg_toast_common_body_null_error))
                    }
                }
            }
        }
    }

    private fun saveData() {
        log.e()

        preference.setResinWidgetDesignSettings(
            ResinWidgetDesignSettings(
                sfWidgetTheme.value,
                sfResinTimeNotation.value,
                sfResinImageVisibility.value,
                sfResinUidVisibility.value,
                sfResinNameVisibility.value,
                sfResinFontSize.value,
                sfTransparency.value
            )
        )

        preference.setDetailWidgetDesignSettings(
            DetailWidgetDesignSettings(
                sfWidgetTheme.value,
                sfDetailTimeNotation.value,

                sfResinDataVisibility.value,
                sfDailyCommissionDataVisibility.value,
                sfWeeklyBossDataVisibility.value,
                sfRealmCurrencyDataVisibility.value,
                sfExpeditionDataVisibility.value,
                sfTransformerDataVisibility.value,

                sfTrailBlazepowerDataVisibility.value,
                sfDailyTrainingDataVisibility.value,
                sfEchoOfWarDataVisibility.value,
                sfSimulatedUniverseDataVisibility.value,
                sfSimulatedUniverseClearTimeVisibility.value,
                sfAssignmentTimeDataVisibility.value,

                sfBatteryDataVisibility.value,
                sfEngagementTodayDataVisibility.value,
                sfScratchCardDataVisibility.value,
                sfVideoStoreManagementDataVisibility.value,

                sfDetailUidVisibility.value,
                sfDetailNameVisibility.value,
                sfFontSizeDetail.value,
                sfTransparency.value
            )
        )

        sfApplySavedData.emitInVmScope(true)

        makeToast(resource.getString(R.string.msg_toast_save_done))

        sendEvent(Event.FinishThisActivity())
    }

    fun onClickSave() {
        log.e()
        saveData()
    }

    fun onClickPreiew(index: Int) {
        log.e("index -> $index")
        sfSelectedPreview.value = index
    }

    fun onClickWidgetTheme(widgetTheme: Constant.WidgetTheme) {
        log.e("widgetTheme -> $widgetTheme")
        sfWidgetTheme.value = widgetTheme.pref
    }

    fun onClickResinImageVisible(resinImageVisibility: Constant.ResinImageVisibility) {
        log.e("resinImageVisibility -> $resinImageVisibility")
        sfResinImageVisibility.value = resinImageVisibility.pref
    }

    fun onClickSetResinTimeNotation(timeNotation: Constant.TimeNotation) {
        log.e("timeNotation -> $timeNotation")
        sfResinTimeNotation.value = timeNotation.pref
    }

    fun onClickSetDetailTimeNotation(timeNotation: Constant.TimeNotation) {
        log.e("timeNotation -> $timeNotation")
        sfDetailTimeNotation.value = timeNotation.pref
    }

    fun onClickBackgroundTransparent() {
        log.e()
        sfTransparency.value = Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY
    }

    fun onClickDetailFontSize() {
        log.e()
        sfFontSizeDetail.value = Constant.PREF_DEFAULT_WIDGET_DETAIL_FONT_SIZE
    }

    fun onClickResinFontSize() {
        log.e()
        sfResinFontSize.value = Constant.PREF_DEFAULT_WIDGET_RESIN_FONT_SIZE
    }

    fun onClickCharacterItem(item: LocalCharacter) {
        log.e(item.name_ko)

        if (_selectedCharacterIdList.filter { it == item.id }.count() > 0) {
            log.e()
            for (character in _selectedCharacterIdList) {
                if (character == item.id) {
                    _selectedCharacterIdList.remove(character)
                    _sfSelectedCharacterList.value.remove(item)
                    break
                }
            }
        } else {
            log.e()
            _selectedCharacterIdList.add(item.id)
            _sfSelectedCharacterList.value.add(item)
        }

        sfCharacterListRefreshSwitch.value = !sfCharacterListRefreshSwitch.value

        preference.setSelectedCharacterIdList(_selectedCharacterIdList)

        log.e(_selectedCharacterIdList)
    }

    fun onClickSetAllCharacters() {
        _sfCharacterList.value = PlayableCharacters.toMutableList()

        sfStartSelectFragment.emitInVmScope(true)
    }

    fun onClickFinishSelectChara() {
        log.e()
        sfFinishSelectFragment.emitInVmScope(true)
    }
}