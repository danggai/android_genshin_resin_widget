package danggai.app.presentation.ui.cookie

import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.log
import javax.inject.Inject

@HiltViewModel
class CookieWebViewViewModel @Inject constructor() : BaseViewModel() {

    fun onClickGetCookie() {
        log.e()
        sendEvent(Event.GetCookie())
    }

    fun onClickRefresh() {
        log.e()
        sendEvent(Event.RefreshWebView())
    }

    fun onClickFinish() {
        log.e()
        sendEvent(Event.FinishThisActivity())
    }

}