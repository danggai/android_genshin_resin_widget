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
}