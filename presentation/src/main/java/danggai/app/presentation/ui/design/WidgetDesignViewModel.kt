package danggai.app.presentation.ui.design

import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.log
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.resource.repository.ResourceProviderRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class WidgetDesignViewModel @Inject constructor(
    private val preference: PreferenceManagerRepository,
    private val resource: ResourceProviderRepository,
) : BaseViewModel() {

    val lvWidgetTheme = MutableStateFlow(Constant.PREF_WIDGET_THEME_AUTOMATIC)
    val lvTransparency = MutableStateFlow(Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY)

    val lvResinTimeNotation = MutableStateFlow(Constant.PREF_TIME_NOTATION_REMAIN_TIME)
    val lvResinImageVisibility = MutableStateFlow(Constant.PREF_WIDGET_RESIN_IMAGE_VISIBLE)
    val lvResinFontSize = MutableStateFlow(Constant.PREF_DEFAULT_WIDGET_RESIN_FONT_SIZE)

    val lvDetailTimeNotation = MutableStateFlow(Constant.PREF_TIME_NOTATION_REMAIN_TIME)
    val lvResinDataVisibility = MutableStateFlow(true)
    val lvDailyCommissionDataVisibility = MutableStateFlow(true)
    val lvWeeklyBossDataVisibility = MutableStateFlow(true)
    val lvRealmCurrencyDataVisibility = MutableStateFlow(true)
    val lvExpeditionDataVisibility = MutableStateFlow(true)
    val lvFontSizeDetail = MutableStateFlow(Constant.PREF_DEFAULT_WIDGET_DETAIL_FONT_SIZE)

    fun initUi() {
        preference.getResinWidgetDesignSettings().let {
            lvWidgetTheme.value = it.widgetTheme
            lvTransparency.value = it.backgroundTransparency
            lvResinTimeNotation.value = it.timeNotation
            lvResinFontSize.value = it.fontSize
            lvResinImageVisibility.value = it.resinImageVisibility
        }

        preference.getDetailWidgetDesignSettings().let {
            lvDetailTimeNotation.value = it.timeNotation
            lvFontSizeDetail.value = it.fontSize
            lvResinDataVisibility.value = it.resinDataVisibility
            lvDailyCommissionDataVisibility.value = it.dailyCommissinDataVisibility
            lvWeeklyBossDataVisibility.value = it.weeklyBossDataVisibility
            lvRealmCurrencyDataVisibility.value = it.realmCurrencyDataVisibility
            lvExpeditionDataVisibility.value = it.expeditionDataVisibility
        }
    }

    private fun saveData() {
        log.e()

        preference.setResinWidgetDesignSettings(
            ResinWidgetDesignSettings(
                lvWidgetTheme.value,
                lvResinTimeNotation.value,
                lvResinImageVisibility.value,
                lvResinFontSize.value,
                lvTransparency.value
            )
        )

        preference.setDetailWidgetDesignSettings(
            DetailWidgetDesignSettings(
                lvWidgetTheme.value,
                lvDetailTimeNotation.value,
                lvResinDataVisibility.value,
                lvDailyCommissionDataVisibility.value,
                lvWeeklyBossDataVisibility.value,
                lvRealmCurrencyDataVisibility.value,
                lvExpeditionDataVisibility.value,
                lvFontSizeDetail.value,
                lvTransparency.value
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
        lvWidgetTheme.value = widgetTheme.pref
    }

    fun onClickResinImageVisible(resinImageVisibility: Constant.ResinImageVisibility) {
        log.e("resinImageVisibility -> $resinImageVisibility")
        lvResinImageVisibility.value = resinImageVisibility.pref
    }

    fun onClickSetResinTimeNotation(timeNotation: Constant.TimeNotation) {
        log.e("timeNotation -> $timeNotation")
        lvResinTimeNotation.value = timeNotation.pref
    }

    fun onClickSetDetailTimeNotation(timeNotation: Constant.TimeNotation) {
        log.e("timeNotation -> $timeNotation")
        lvDetailTimeNotation.value = timeNotation.pref
    }

    fun onClickBackgroundTransparent() {
        log.e()
        lvTransparency.value = Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY
    }


    fun onClickDetailFontSize() {
        log.e()
        lvFontSizeDetail.value = Constant.PREF_DEFAULT_WIDGET_DETAIL_FONT_SIZE
    }


    fun onClickResinFontSize() {
        log.e()
        lvResinFontSize.value = Constant.PREF_DEFAULT_WIDGET_RESIN_FONT_SIZE
    }
}