package danggai.app.resinwidget.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.MainActivityBinding
import danggai.app.resinwidget.ui.BindingActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

class MainActivity : BindingActivity<MainActivityBinding>() {

    private val rxBackButtonAction: Subject<Long> = BehaviorSubject.createDefault(0L).toSerialized()

    @LayoutRes
    override fun getLayoutResId() = R.layout.main_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        initFragment()
        initRx()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, MainFragment.newInstance(), MainFragment.TAG)
            .commit()
    }

    private fun initRx() {
        rxBackButtonAction
            .observeOn(AndroidSchedulers.mainThread())
            .buffer(2,1)
            .map { it[1] - it[0] < Constant.BACK_BUTTON_INTERVAL }
            .subscribe {
                if (it) { super.onBackPressed() } else { Toast.makeText(applicationContext, getString(
                    R.string.msg_toast_back_button), Toast.LENGTH_SHORT).show() }
            }.addDisposableExt()
    }

}