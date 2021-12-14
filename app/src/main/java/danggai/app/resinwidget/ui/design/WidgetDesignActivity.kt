package danggai.app.resinwidget.ui.design

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.ActivityWidgetDesignBinding
import danggai.app.resinwidget.ui.BindingActivity
import danggai.app.resinwidget.util.log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

class WidgetDesignActivity : BindingActivity<ActivityWidgetDesignBinding>() {

    companion object {
        fun startActivity(act: Activity) {
            log.e()
            val itn = Intent(act, WidgetDesignActivity::class.java)
            act.startActivity(itn)
        }
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.activity_widget_design

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, WidgetDesignFragment.newInstance(), WidgetDesignFragment.TAG)
            .commit()
    }

}