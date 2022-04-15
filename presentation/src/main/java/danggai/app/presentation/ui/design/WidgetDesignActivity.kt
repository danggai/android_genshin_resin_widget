package danggai.app.presentation.ui.design

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingActivity
import danggai.app.presentation.core.util.log
import danggai.app.presentation.databinding.ActivityWidgetDesignBinding

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