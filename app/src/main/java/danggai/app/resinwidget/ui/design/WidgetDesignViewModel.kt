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

}