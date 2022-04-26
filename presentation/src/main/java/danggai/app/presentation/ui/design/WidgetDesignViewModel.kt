package danggai.app.presentation.ui.design

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.core.util.Event
import danggai.app.presentation.core.util.NonNullMutableLiveData
import danggai.app.presentation.core.util.log
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.preference.repository.PreferenceManagerRepository
import danggai.domain.resource.repository.ResourceProviderRepository
import danggai.domain.util.Constant
import javax.inject.Inject

@HiltViewModel
class WidgetDesignViewModel @Inject constructor(
    private val preference: PreferenceManagerRepository,
    private val resource: ResourceProviderRepository,
) : BaseViewModel() {

    var lvFinishActivity = MutableLiveData<Event<Boolean>>()

    var lvWidgetTheme: NonNullMutableLiveData<Int> = NonNullMutableLiveData(Constant.PREF_WIDGET_THEME_AUTOMATIC)
    var lvResinImageVisibility: NonNullMutableLiveData<Int> = NonNullMutableLiveData(Constant.PREF_WIDGET_RESIN_IMAGE_VISIBLE)
    var lvResinTimeNotation: NonNullMutableLiveData<Int> = NonNullMutableLiveData(Constant.PREF_TIME_NOTATION_REMAIN_TIME)
    var lvDetailTimeNotation: NonNullMutableLiveData<Int> = NonNullMutableLiveData(Constant.PREF_TIME_NOTATION_REMAIN_TIME)

    var lvResinDataVisibility: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(true)
    var lvDailyCommissionDataVisibility: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(true)
    var lvWeeklyBossDataVisibility: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(true)
    var lvRealmCurrencyDataVisibility: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(true)
    var lvExpeditionDataVisibility: NonNullMutableLiveData<Boolean> = NonNullMutableLiveData(true)

    val lvTransparency: NonNullMutableLiveData<Int> = NonNullMutableLiveData(Constant.PREF_DEFAULT_WIDGET_BACKGROUND_TRANSPARENCY)
    val lvFontSizeResin: NonNullMutableLiveData<Int> = NonNullMutableLiveData(Constant.PREF_DEFAULT_WIDGET_FONT_SIZE_RESIN)
    val lvFontSizeDetail: NonNullMutableLiveData<Int> = NonNullMutableLiveData(Constant.PREF_DEFAULT_WIDGET_FONT_SIZE_DETAIL)

    fun initUi() {
        preference.getResinWidgetDesignSettings().let {
            lvWidgetTheme.value = it.widgetTheme
            lvTransparency.value = it.backgroundTransparency
            lvResinTimeNotation.value = it.timeNotation
            lvFontSizeResin.value = it.fontSize
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
                lvFontSizeResin.value,
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

        lvMakeToast.value = Event(resource.getString(R.string.msg_toast_save_done))

        lvFinishActivity.value = Event(true)
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
        lvFontSizeDetail.value = Constant.PREF_DEFAULT_WIDGET_FONT_SIZE_DETAIL
    }


    fun onClickResinFontSize() {
        log.e()
        lvFontSizeResin.value = Constant.PREF_DEFAULT_WIDGET_FONT_SIZE_RESIN
    }
}