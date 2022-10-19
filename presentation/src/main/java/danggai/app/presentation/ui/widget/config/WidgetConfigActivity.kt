package danggai.app.presentation.ui.widget.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingActivity
import danggai.app.presentation.databinding.ActivityWidgetConfigBinding
import danggai.app.presentation.util.log

@AndroidEntryPoint
class WidgetConfigActivity : BindingActivity<ActivityWidgetConfigBinding, WidgetConfigViewModel>() {

    companion object {
        const val ARG_PARAM_COOKIE = "ARG_PARAM_COOKIE"
        const val ARG_PARAM_UID = "ARG_PARAM_UID"

        fun startActivity(act: Activity) {
            log.e()
            val itn = Intent(act, WidgetConfigActivity::class.java)
            act.startActivity(itn)
        }
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.activity_widget_config

    private val mVM: WidgetConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this
        binding.vm = mVM

        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, WidgetConfigFragment.newInstance(), WidgetConfigFragment.TAG)
            .commit()
    }
}