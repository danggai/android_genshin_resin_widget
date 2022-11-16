package danggai.app.presentation.ui.cookie

import android.webkit.CookieManager
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.log
import danggai.domain.resource.repository.ResourceProviderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CookieWebViewViewModel @Inject constructor(
    private val resource: ResourceProviderRepository
) : BaseViewModel() {

    val sfGetCookieFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    private fun getCookie() {
        try {
            val cookieManager = CookieManager.getInstance()
            val cookie = cookieManager.getCookie("https://www.hoyolab.com/")
            log.e(cookie)

            if (cookie.contains("ltuid") && cookie.contains("ltoken")) {
                sfGetCookieFlow.value = cookie
            } else {
                makeToast(resource.getString(R.string.msg_toast_get_cookie_fail))
            }
        } catch (e: NullPointerException) {
            log.e(e.message.toString())
            makeToast(resource.getString(R.string.msg_toast_get_cookie_null_fail))
        } catch (e: Exception) {
            log.e(e.message.toString())
            makeToast(resource.getString(R.string.msg_toast_get_cookie_unknown_fail))
        }
    }

    fun autoGetCookieOnlyOnce() {
        if (sfGetCookieFlow.value == null) {
            getCookie()
        }
    }

    fun onClickGetCookie() {
        log.e()
        getCookie()
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