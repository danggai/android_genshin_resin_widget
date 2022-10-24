package danggai.app.presentation.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingActivity
import danggai.app.presentation.databinding.ActivityMainBinding
import danggai.app.presentation.util.log
import danggai.domain.util.Constant
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding, MainViewModel>() {

    private val rxBackButtonAction: Subject<Long> = BehaviorSubject.createDefault(0L).toSerialized()

    private val mVM: MainViewModel by viewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this
        binding.vm = mVM

        initFragment()
        initRx()
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