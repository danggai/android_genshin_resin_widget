package danggai.app.presentation.cookie

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.core.util.Event
import danggai.app.presentation.core.util.log
import javax.inject.Inject

@HiltViewModel
class CookieWebViewViewModel @Inject constructor() : BaseViewModel() {

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