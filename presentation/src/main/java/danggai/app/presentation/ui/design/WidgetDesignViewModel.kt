package danggai.app.presentation.ui.design

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.log
import danggai.domain.core.ApiResult
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.network.character.entity.Avatar
import danggai.domain.network.character.usecase.CharacterUseCase
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.resource.repository.ResourceProviderRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetDesignViewModel @Inject constructor(
    private val preference: PreferenceManagerRepository,
    private val resource: ResourceProviderRepository,
    private val characters: CharacterUseCase
) : BaseViewModel() {
    val sfProgress = MutableStateFlow(false)

    val sfWidgetTheme = MutableStateFlow(Constant.PREF_WIDGET_THEME_AUTOMATIC)
    val sfTransparency = MutableStateFlow(Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY)

    val sfResinTimeNotation = MutableStateFlow(Constant.PREF_TIME_NOTATION_REMAIN_TIME)
    val sfResinImageVisibility = MutableStateFlow(Constant.PREF_WIDGET_RESIN_IMAGE_VISIBLE)
    val sfResinFontSize = MutableStateFlow(Constant.PREF_DEFAULT_WIDGET_RESIN_FONT_SIZE)

    val sfDetailTimeNotation = MutableStateFlow(Constant.PREF_TIME_NOTATION_REMAIN_TIME)
    val sfResinDataVisibility = MutableStateFlow(true)
    val sfDailyCommissionDataVisibility = MutableStateFlow(true)
    val sfWeeklyBossDataVisibility = MutableStateFlow(true)
    val sfRealmCurrencyDataVisibility = MutableStateFlow(true)
    val sfExpeditionDataVisibility = MutableStateFlow(true)
    val sfTransformerDataVisibility = MutableStateFlow(true)
    val sfFontSizeDetail = MutableStateFlow(Constant.PREF_DEFAULT_WIDGET_DETAIL_FONT_SIZE)
    val sfRefreshSwitch = MutableStateFlow(false)

    private var _characterList: MutableList<Avatar> = mutableListOf()
    val characterList: MutableList<Avatar>
       get() = _characterList

    fun initUi() {
        preference.getResinWidgetDesignSettings().let {
            sfWidgetTheme.value = it.widgetTheme
            sfTransparency.value = it.backgroundTransparency
            sfResinTimeNotation.value = it.timeNotation
            sfResinFontSize.value = it.fontSize
            sfResinImageVisibility.value = it.resinImageVisibility
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
        }
    }

    fun refreshCharacters(
        roleId: String,
        server: String,
        cookie: String,
        ds: String
    ) {
        viewModelScope.launch {
            characters(
                roleId = roleId,
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
                                _characterList = it.data.data.avatars as MutableList<Avatar>
                                sfRefreshSwitch.value = !sfRefreshSwitch.value
                            }
                            else -> {
                                log.e()
                                CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHARACTERS, null, null)
                                sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_get_characters_error)))
                            }
                        }
                    }
                    is ApiResult.Failure -> {
                        it.message.let { msg ->
                            log.e(msg)
                            CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHARACTERS, it.code, null)
                            sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_common_network_error)))
                        }
                    }
                    is ApiResult.Error -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHARACTERS, null, null)
                        sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_get_characters_error)))
                    }
                    is ApiResult.Null -> {
                        CommonFunction.sendCrashlyticsApiLog(Constant.API_NAME_CHARACTERS, null, null)
                        sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_common_body_null_error)))
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
                sfFontSizeDetail.value,
                sfTransparency.value
            )
        )

        sendEvent(Event.MakeToast(resource.getString(R.string.msg_toast_save_done)))

        sendEvent(Event.FinishThisActivity())
    }
    
    fun onClickSave() {
        log.e()
        saveData()
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

    fun onClickRefreshCharacterInfo() {
        log.e()
        refreshCharacters(
            preference.getStringUid(),
            when (preference.getIntServer()) {
                Constant.PREF_SERVER_ASIA -> Constant.SERVER_OS_ASIA
                Constant.PREF_SERVER_EUROPE -> Constant.SERVER_OS_EURO
                Constant.PREF_SERVER_USA -> Constant.SERVER_OS_USA
                Constant.PREF_SERVER_CHT -> Constant.SERVER_OS_CHT
                else -> Constant.SERVER_OS_ASIA
            },
            preference.getStringCookie(),
            CommonFunction.getGenshinDS()
        )
    }
}