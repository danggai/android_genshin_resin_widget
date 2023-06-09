package danggai.app.presentation.ui.cookie

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentCookieWebviewBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.newaccount.NewHoyolabAccountActivity
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.Event
import danggai.app.presentation.util.log
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CookieWebViewFragment : BindingFragment<FragmentCookieWebviewBinding, CookieWebViewViewModel>() {

    companion object {
        val TAG: String = CookieWebViewFragment::class.java.simpleName
        fun newInstance() = CookieWebViewFragment()
    }

    private val mVM: CookieWebViewViewModel by activityViewModels()


    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_cookie_webview

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM
        binding.vm?.setCommonFun()

        initUi()
        initSf()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    log.e("parentFragmentManager.backStackEntryCount = ${parentFragmentManager.backStackEntryCount}")
                    when {
                        binding.wvBody.canGoBack() -> {
                            log.e()
                            binding.wvBody.goBack()
                        }
                        else -> {
                            log.e()
                            activity?.finish()
                        }
                    }
                } catch (e:Exception) {
                    log.e(e.toString())
                    activity?.finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun initUi() {
        fun getReplaceUserAgent(defaultAgent: String): String {
            return defaultAgent.replace("; wv", "")
        }
        log.e()

        binding.wvBody.apply {
            clearCache(true)
            clearFormData()
            clearHistory()
            clearMatches()
            clearSslPreferences()

            settings.apply {
                loadWithOverviewMode = true  // WebView 화면크기에 맞추도록 설정 - setUseWideViewPort 와 같이 써야함
                useWideViewPort = true  // wide viewport 설정 - setLoadWithOverviewMode 와 같이 써야함

                setSupportZoom(false)  // 줌 설정 여부
                builtInZoomControls = false  // 줌 확대/축소 버튼 여부

                javaScriptEnabled = true // 자바스크립트 사용여부
                javaScriptCanOpenWindowsAutomatically = true // javascript가 window.open()을 사용할 수 있도록 설정
                setSupportMultipleWindows(true) // 멀티 윈도우 사용 여부

                domStorageEnabled = true  // 로컬 스토리지 (localStorage) 사용여부

                databaseEnabled = true

                cacheMode = WebSettings.LOAD_NO_CACHE

                loadWithOverviewMode = false

                userAgentString = getReplaceUserAgent(userAgentString)
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    log.e(url?:"")
                    binding.pgBar.visibility = View.GONE
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?,
                ): WebResourceResponse? {
                    val cookie = CookieManager.getInstance().getCookie("https://www.hoyolab.com/")

                    if (!cookie.isNullOrEmpty()
                        && cookie.contains("ltuid")
                        && cookie.contains("ltoken")
                    ) mVM.autoGetCookieOnlyOnce()

                    return super.shouldInterceptRequest(view, request)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    binding.pgBar.visibility = View.VISIBLE
                    binding.pgBar.progress = newProgress

                    if (newProgress >= 100) {
                        binding.pgBar.visibility = View.GONE
                    }
                }

                override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                    activity?.let { activity ->
                        val popUpWebView = WebView(activity).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                setSupportMultipleWindows(true)
                                javaScriptCanOpenWindowsAutomatically = true
                                userAgentString = getReplaceUserAgent(userAgentString)
                            }
                        }

                        CookieManager.getInstance().apply {
                            acceptCookie()
                            setAcceptThirdPartyCookies(popUpWebView, true)
                        }

                        val dialog = Dialog(activity).apply {
                            setContentView(popUpWebView)
                        }.apply {
                            setOnDismissListener {
                                mVM.autoGetCookieOnlyOnce()
                                popUpWebView.destroy()
                            }

                            window?.run {
                                WindowCompat.setDecorFitsSystemWindows(this, true)

                                attributes = attributes?.apply {
                                    width = (CommonFunction.getDisplayMetrics(activity).widthPixels * 0.9).toInt()
                                    height = (CommonFunction.getDisplayMetrics(activity).heightPixels * 0.8).toInt()
                                }
                            }
                        }

                        popUpWebView.apply {
                            webChromeClient = object : WebChromeClient() {
                                override fun onCloseWindow(window: WebView?) {
                                    dialog.dismiss()
                                }
                            }
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(
                                    view: WebView?,
                                    url: String?,
                                    favicon: Bitmap?
                                ) {
                                    if (listOf(
                                            "https://account.hoyolab.com/security.html?origin=hoyolab",
                                            "https://m.hoyolab.com/account-system-sea/security.html?origin=hoyolab",
                                            "about:blank"
                                        ).contains(url)
                                    ) dialog.dismiss()
                                    else {
                                        if(!(context as Activity).isFinishing) dialog.show()
                                    }

                                    super.onPageStarted(view, url, favicon)
                                }
                            }
                        }

                        log.e(resultMsg?.arg1.toString())
                        log.e(resultMsg?.arg2.toString())
                        log.e(resultMsg?.obj.toString())
                        log.e(resultMsg?.replyTo.toString())
                        log.e(resultMsg?.sendingUid.toString())
                        log.e(resultMsg?.what.toString())

                        resultMsg?.run {
                            (this.obj as WebView.WebViewTransport).webView =
                                popUpWebView
                            sendToTarget()
                        }
                    }
                    return true
                }
            }
        }

        CookieManager.getInstance().apply {
            removeAllCookies(null)
            flush()

            acceptCookie()
            setAcceptThirdPartyCookies(binding.wvBody, true)
        }

        WebStorage.getInstance().deleteAllData()

        binding.wvBody.loadUrl("https://m.hoyolab.com/#/timeline")

        makeToastLong(requireContext(), getString(R.string.msg_toast_how_to_get_cookie))
    }

    override fun handleEvents(event: Event) {
        super.handleEvents(event)

        when (event) {
            is Event.RefreshWebView -> {
                log.e()
                binding.wvBody.reload()
            }
            else -> {}
        }
    }

    private fun initSf() {
        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                mVM.sfGetCookieFlow.collect { cookie ->
                    cookie?.let {
                        NewHoyolabAccountActivity.startActivityWithCookie(requireActivity(), cookie)
                        makeToast(requireContext(), getString(R.string.msg_toast_get_cookie_success))
                        activity!!.finish()
                    }
                }
            }
        }
    }
}