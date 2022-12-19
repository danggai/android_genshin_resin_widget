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
import danggai.app.presentation.util.log

@AndroidEntryPoint
class NewHoyolabAccountActivity : BindingActivity<ActivityNewHoyolabAccountBinding, NewHoyolabAccountViewModel>() {

    companion object {
        const val ARG_PARAM_COOKIE = "ARG_PARAM_COOKIE"
        const val ARG_PARAM_UID = "ARG_PARAM_UID"

        fun startActivity(act: Activity) {
            log.e()
            val itn = Intent(act, NewHoyolabAccountActivity::class.java)
            act.startActivity(itn)
        }

        fun startActivityWithCookie(act: Activity, cookie: String) {
            log.e()
            val itn = Intent(act, NewHoyolabAccountActivity::class.java)
            itn.putExtra(ARG_PARAM_COOKIE, cookie)
            act.startActivity(itn)
        }

        fun startActivityWithUid(act: Activity, uid: String) {
            log.e()
            val itn = Intent(act, NewHoyolabAccountActivity::class.java)
            itn.putExtra(ARG_PARAM_UID, uid)
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

    override fun onNewIntent(intent: Intent) {
        log.e()
        if (intent.hasExtra(ARG_PARAM_COOKIE)) {
            log.e()
            val mFragment = supportFragmentManager.fragments[0] as NewHoyolabAccountFragment
            mFragment.onNewIntent(intent)
        }
        super.onNewIntent(intent)
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, NewHoyolabAccountFragment.newInstance(), NewHoyolabAccountFragment.TAG)
            .commit()
    }
}