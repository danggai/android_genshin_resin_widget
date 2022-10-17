package danggai.app.presentation.ui.newaccount

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingActivity
import danggai.app.presentation.databinding.ActivityNewHoyolabAccountBinding
import danggai.app.presentation.databinding.ActivityWidgetDesignBinding
import danggai.app.presentation.ui.design.charaters.select.WidgetDesignSelectCharacterFragment
import danggai.app.presentation.util.log

@AndroidEntryPoint
class NewHoyolabAccountActivity : BindingActivity<ActivityNewHoyolabAccountBinding, NewHoyolabAccountViewModel>() {

    companion object {
        fun startActivity(act: Activity) {
            log.e()
            val itn = Intent(act, NewHoyolabAccountActivity::class.java)
            act.startActivity(itn)
        }
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.activity_new_hoyolab_account

    private val mVM: NewHoyolabAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this
        binding.vm = mVM

        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, NewHoyolabAccountFragment.newInstance(), NewHoyolabAccountFragment.TAG)
            .commit()
    }
}