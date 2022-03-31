package danggai.app.resinwidget.ui.design

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.resinwidget.R
import danggai.app.resinwidget.core.BindingActivity
import danggai.app.resinwidget.databinding.ActivityWidgetDesignBinding
import danggai.app.resinwidget.util.log

@AndroidEntryPoint
class WidgetDesignActivity : BindingActivity<ActivityWidgetDesignBinding, WidgetDesignViewModel>() {

    companion object {
        fun startActivity(act: Activity) {
            log.e()
            val itn = Intent(act, WidgetDesignActivity::class.java)
            act.startActivity(itn)
        }
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.activity_widget_design

    private val mVM: WidgetDesignViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this
        binding.vm = mVM

        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, WidgetDesignFragment.newInstance(), WidgetDesignFragment.TAG)
            .commit()
    }

}