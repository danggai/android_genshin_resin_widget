package danggai.app.resinwidget.ui.webview.cookie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.resinwidget.R
import danggai.app.resinwidget.core.BindingActivity
import danggai.app.resinwidget.databinding.ActivityCookieWebviewBinding

@AndroidEntryPoint
class CookieWebViewActivity : BindingActivity<ActivityCookieWebviewBinding, CookieWebViewViewModel>() {

    companion object {
        fun startActivity(act: Activity) {
            val intent = Intent(act, CookieWebViewActivity::class.java)
            act.startActivity(intent)
        }
    }

    private val mVM: CookieWebViewViewModel by viewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.activity_cookie_webview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this
        binding.vm = mVM

        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, CookieWebViewFragment.newInstance(), CookieWebViewFragment.TAG)
            .commit()
    }

}