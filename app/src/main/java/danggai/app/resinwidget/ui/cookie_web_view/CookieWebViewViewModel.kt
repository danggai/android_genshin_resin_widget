package danggai.app.resinwidget.ui.cookie_web_view

import android.app.Application
import androidx.lifecycle.MutableLiveData
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.ui.base.BaseViewModel
import danggai.app.resinwidget.util.Event
import danggai.app.resinwidget.util.log

class CookieWebViewViewModel(override val app: Application, private val api: ApiRepository) : BaseViewModel(app) {

    var lvGetCookie = MutableLiveData<Event<Boolean>>()
    var lvRefresh = MutableLiveData<Event<Boolean>>()
    var lvFinish = MutableLiveData<Event<Boolean>>()

    fun onClickGetCookie() {
        log.e()
        lvGetCookie.value = Event(true)
    }
    fun onClickRefresh() {
        log.e()
        lvRefresh.value = Event(true)
    }
    fun onClickFinish() {
        log.e()
        lvFinish.value = Event(true)
    }



}