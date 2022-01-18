package danggai.app.resinwidget.ui.main.resin

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import com.google.android.gms.ads.AdView
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.FragmentResinBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.ui.main.MainViewModel
import danggai.app.resinwidget.util.PreferenceManager
import org.koin.androidx.viewmodel.ext.android.getViewModel


class MainResinFragment : BindingFragment<FragmentResinBinding>() {

    companion object {
        val TAG: String = MainResinFragment::class.java.simpleName
        fun newInstance() = MainResinFragment()
    }

    private lateinit var mVM: MainViewModel
    private lateinit var mAdView : AdView

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_resin

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = getViewModel()
        binding.lifecycleOwner = viewLifecycleOwner

        binding.vm?.let {
            mVM = it
            it.setCommonFun(view)
        }

        initUi()
    }

    private fun initUi() {
        context?.let {
            mVM.lvServer.value = PreferenceManager.getIntServer(it)
            mVM.lvAutoRefreshPeriod.value = PreferenceManager.getLongAutoRefreshPeriod(it)

            mVM.lvEnableNotiEach40Resin.value = PreferenceManager.getBooleanNotiEach40Resin(it)
            mVM.lvEnableNoti140Resin.value = PreferenceManager.getBooleanNoti140Resin(it)
            mVM.lvEnableNotiCustomResin.value = PreferenceManager.getBooleanNotiCustomResin(it)
            mVM.lvCustomNotiResin.value = PreferenceManager.getIntCustomTargetResin(it).let { int ->
                if (int == -1) "0" else int.toString()
            }

            when (mVM.lvServer.value) {
                Constant.PREF_SERVER_ASIA -> binding.rbAsia.isChecked = true
                Constant.PREF_SERVER_USA -> binding.rbUsa.isChecked = true
                Constant.PREF_SERVER_EUROPE -> binding.rbEuro.isChecked = true
                Constant.PREF_SERVER_CHT -> binding.rbCht.isChecked = true
                else -> binding.rbAsia.isChecked = true
            }

            when (mVM.lvAutoRefreshPeriod.value) {
                15L -> binding.rb15m.isChecked = true
                30L -> binding.rb30m.isChecked = true
                60L -> binding.rb1h.isChecked = true
                120L -> binding.rb2h.isChecked = true
                else -> binding.rbDisable.isChecked = true
            }
        }
    }
}