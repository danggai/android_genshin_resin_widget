package danggai.app.resinwidget.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.ActivityMainBinding
import danggai.app.resinwidget.ui.BindingActivity
import danggai.app.resinwidget.util.LocaleWrapper.wrap
import danggai.app.resinwidget.util.log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlin.system.exitProcess


class MainActivity : BindingActivity<ActivityMainBinding>() {

    companion object {
        val ARG_PARAM_COOKIE = "ARG_PARAM_COOKIE"

        fun startActivity(act: Activity, cookie: String) {
            log.e()
            val itn = Intent(act, MainActivity::class.java)
            itn.putExtra(ARG_PARAM_COOKIE, cookie)
            act.startActivity(itn)
        }
    }

    private val rxBackButtonAction: Subject<Long> = BehaviorSubject.createDefault(0L).toSerialized()

    @LayoutRes
    override fun getLayoutResId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        initFragment()
        initRx()
    }

    override fun onNewIntent(intent: Intent) {
        log.e()
        if (intent.hasExtra(ARG_PARAM_COOKIE)) {
            log.e()
            val mFragment = supportFragmentManager.fragments[0] as MainFragment
            mFragment.onNewIntent(intent)
        }
        super.onNewIntent(intent)
    }

    override fun onBackPressed() {
        rxBackButtonAction.onNext(System.currentTimeMillis())
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
                if (it) {
                    super.onBackPressed()
                    finish()
                    exitProcess(0)
                } else { Toast.makeText(applicationContext, getString(
                    R.string.msg_toast_back_button), Toast.LENGTH_SHORT).show() }
            }.addDisposableExt()
    }

}