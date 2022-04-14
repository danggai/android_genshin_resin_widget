package danggai.app.presentation.design

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.core.util.Event
import danggai.app.presentation.core.util.NonNullMutableLiveData
import danggai.app.presentation.core.util.log
import danggai.domain.util.Constant
import danggai.domain.util.ResourceProvider
import javax.inject.Inject

@HiltViewModel
class WidgetDesignViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
) : BaseViewModel() {

    var lvSaveData = MutableLiveData<Event<Boolean>>()

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

    fun onClickSave() {
        log.e()
        lvSaveData.value = Event(true)
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