package danggai.app.resinwidget.ui.design

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.ui.base.BaseViewModel
import danggai.app.resinwidget.util.*

class WidgetDesignViewModel(override val app: Application, private val api: ApiRepository) : BaseViewModel(app) {

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

    val lvTransparency: NonNullMutableLiveData<Int> = NonNullMutableLiveData(0)



    fun onClickSave() {
        log.e()
        /* 데이터 저장 */
        lvSaveData.value = Event(true)
    }

    fun onClickWidgetTheme(view: View) {
        log.e()
        lvWidgetTheme.value = when (view.id) {
            R.id.rb_theme_automatic -> Constant.PREF_WIDGET_THEME_AUTOMATIC
            R.id.rb_theme_light -> Constant.PREF_WIDGET_THEME_LIGHT
            R.id.rb_theme_dark -> Constant.PREF_WIDGET_THEME_DARK
            else -> Constant.PREF_WIDGET_THEME_AUTOMATIC
        }
    }

    fun onClickResinImageVisible(view: View) {
        log.e()
        lvResinImageVisibility.value = when (view.id) {
            R.id.rb_resin_image_visible -> Constant.PREF_WIDGET_RESIN_IMAGE_VISIBLE
            R.id.rb_resin_image_invisible -> Constant.PREF_WIDGET_RESIN_IMAGE_INVISIBLE
            else -> Constant.PREF_WIDGET_RESIN_IMAGE_VISIBLE
        }
    }

    fun onClickSetResinTimeNotation(view: View) {
        log.e()
        lvResinTimeNotation.value = when (view.id) {
            R.id.rb_remain_time -> Constant.PREF_TIME_NOTATION_REMAIN_TIME
            R.id.rb_full_charge_time -> Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME
            else -> Constant.PREF_TIME_NOTATION_REMAIN_TIME
        }
    }

    fun onClickSetDetailTimeNotation(view: View) {
        log.e()
        lvDetailTimeNotation.value = when (view.id) {
            R.id.rb_remain_time -> Constant.PREF_TIME_NOTATION_REMAIN_TIME
            R.id.rb_full_charge_time -> Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME
            else -> Constant.PREF_TIME_NOTATION_REMAIN_TIME
        }
    }

}