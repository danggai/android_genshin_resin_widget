package danggai.app.resinwidget.ui.main

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.MainFragmentBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.util.EventObserver
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainFragment : BindingFragment<MainFragmentBinding>() {

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    private lateinit var mVM: MainViewModel

    @LayoutRes
    override fun getLayoutResId() = R.layout.main_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = getViewModel()
        binding.lifecycleOwner = viewLifecycleOwner

        binding.vm?.let {
            mVM = it
            it.setCommonFun(view)
        }

        initUi()
        initLv()
    }

    private fun initUi() {
        context?.let { it ->
            mVM.initUI(PreferenceManager.getStringUid(it), PreferenceManager.getStringCookie(it))
        }
    }

    private fun initLv() {
        mVM.lvSaveUserInfo.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                context?.let { it ->
                    log.e()
                    PreferenceManager.setStringUid(it, mVM.lvUid.value)
                    PreferenceManager.setStringCookie(it, mVM.lvCookie.value)

                    makeToast(it, getString(R.string.msg_toast_save_done))
                }
            }
        })
    }

}