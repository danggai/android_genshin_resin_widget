package danggai.app.resinwidget.ui.cookie_web_view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.CookieWebViewFragmentBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.ui.main.MainActivity
import danggai.app.resinwidget.util.EventObserver
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import org.koin.androidx.viewmodel.ext.android.getViewModel

class CookieWebViewFragment : BindingFragment<CookieWebViewFragmentBinding>() {

    companion object {
        val TAG: String = CookieWebViewFragment::class.java.simpleName
        fun newInstance() = CookieWebViewFragment()
    }

    private lateinit var mVM: CookieWebViewViewModel

    @LayoutRes
    override fun getLayoutResId() = R.layout.cookie_web_view_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = getViewModel()
        binding.lifecycleOwner = viewLifecycleOwner

        binding.vm?.let {
            mVM = it
            it.setCommonFun(view)
        }

        initUi()
        initLv()
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
        log.e()

        binding.wvBody.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.pgBar.visibility = View.GONE
            }

        }
        binding.wvBody.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.pgBar.visibility = View.VISIBLE
                binding.pgBar.progress = newProgress

                if (newProgress >= 100) {
                    binding.pgBar.visibility = View.GONE
                }
            }
        }

        binding.wvBody.settings.loadWithOverviewMode =
            true;  // WebView 화면크기에 맞추도록 설정 - setUseWideViewPort 와 같이 써야함
        binding.wvBody.settings.useWideViewPort =
            true;  // wide viewport 설정 - setLoadWithOverviewMode 와 같이 써야함

        binding.wvBody.settings.setSupportZoom(false);  // 줌 설정 여부
        binding.wvBody.settings.builtInZoomControls = false;  // 줌 확대/축소 버튼 여부

        binding.wvBody.settings.javaScriptEnabled = true; // 자바스크립트 사용여부
        binding.wvBody.settings.javaScriptCanOpenWindowsAutomatically =
            true; // javascript가 window.open()을 사용할 수 있도록 설정
        binding.wvBody.settings.setSupportMultipleWindows(true); // 멀티 윈도우 사용 여부

        binding.wvBody.settings.domStorageEnabled = true;  // 로컬 스토리지 (localStorage) 사용여부

        binding.wvBody.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        binding.wvBody.loadUrl("https://www.hoyolab.com/")

        makeToastLong(requireContext(), getString(R.string.msg_toast_how_to_get_cookie))
    }

    private fun initLv() {
        mVM.lvGetCookie.observe(viewLifecycleOwner, EventObserver { boolean ->
            try {
                val cookieManager = CookieManager.getInstance()
                val cookie = cookieManager.getCookie("https://www.hoyolab.com/")
                log.e(cookie)

                if (cookie.contains("ltuid") && cookie.contains("ltoken")) {
                    MainActivity.startActivity(requireActivity(), cookie)
                    makeToast(requireContext(), getString(R.string.msg_toast_get_cookie_success))
                } else {
                    makeToast(requireContext(), getString(R.string.msg_toast_get_cookie_fail))
                }
            } catch (e: NullPointerException) {
                log.e(e.message.toString())
                makeToast(requireContext(), getString(R.string.msg_toast_get_cookie_null_fail))
            } catch (e: Exception) {
                log.e(e.message.toString())
                makeToast(requireContext(), getString(R.string.msg_toast_get_cookie_unknown_fail))
            }
        })

        mVM.lvRefresh.observe(viewLifecycleOwner, EventObserver { boolean ->
            log.e()
            binding.wvBody.reload()
        })

        mVM.lvFinish.observe(viewLifecycleOwner, EventObserver { boolean ->
            log.e()
            requireActivity().finish()
        })
    }
}