package danggai.app.resinwidget.ui.cookie_web_view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.CookieWebViewActivityBinding
import danggai.app.resinwidget.ui.BindingActivity

class CookieWebViewActivity : BindingActivity<CookieWebViewActivityBinding>() {

    companion object {
        fun startActivity(act: Activity) {
            val intent = Intent(act, CookieWebViewActivity::class.java)
            act.startActivity(intent)
        }
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.cookie_web_view_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, CookieWebViewFragment.newInstance(), CookieWebViewFragment.TAG)
            .commit()
    }

}