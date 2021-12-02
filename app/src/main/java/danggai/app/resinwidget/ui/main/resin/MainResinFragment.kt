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
            mVM.lvAutoRefreshPeriod.value = PreferenceManager.getLongAutoRefreshPeriod(it)
            
            mVM.lvEnableNotiEach40Resin.value = PreferenceManager.getBooleanNotiEach40Resin(it)
            mVM.lvEnableNoti140Resin.value = PreferenceManager.getBooleanNoti140Resin(it)
            mVM.lvEnableNotiCustomResin.value = PreferenceManager.getBooleanNotiCustomResin(it)
            mVM.lvCustomNotiResin.value = PreferenceManager.getIntCustomTargetResin(it).let { int ->
                if (int == -1) "0" else int.toString()
            }

            mVM.lvTimeNotation.value = PreferenceManager.getIntTimeNotation(it)
            mVM.lvWidgetTheme.value = PreferenceManager.getIntWidgetTheme(it)
        }

        when(mVM.lvAutoRefreshPeriod.value) {
            15L -> binding.rb15m.isChecked = true
            30L -> binding.rb30m.isChecked = true
            60L -> binding.rb1h.isChecked = true
            120L -> binding.rb2h.isChecked = true
            else -> binding.rbDisable.isChecked = true
        }

        when(mVM.lvTimeNotation.value) {
            Constant.PREF_TIME_NOTATION_REMAIN_TIME -> binding.rbRemainTime.isChecked = true
            Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> binding.rbFullChargeTime.isChecked = true
            else -> binding.rbRemainTime.isChecked = true
        }

        when(mVM.lvWidgetTheme.value) {
            Constant.PREF_WIDGET_THEME_AUTOMATIC -> binding.rbThemeAutomatic.isChecked = true
            Constant.PREF_WIDGET_THEME_LIGHT -> binding.rbThemeLight.isChecked = true
            Constant.PREF_WIDGET_THEME_DARK -> binding.rbThemeBlack.isChecked = true
            else -> binding.rbThemeAutomatic.isChecked = true
        }

    }
}