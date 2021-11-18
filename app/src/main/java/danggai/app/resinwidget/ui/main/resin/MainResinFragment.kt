package danggai.app.resinwidget.ui.main.resin

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import danggai.app.resinwidget.BuildConfig
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.MainFragmentBinding
import danggai.app.resinwidget.databinding.ResinFragmentBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.ui.cookie_web_view.CookieWebViewActivity
import danggai.app.resinwidget.ui.main.MainActivity
import danggai.app.resinwidget.ui.main.MainViewModel
import danggai.app.resinwidget.util.CommonFunction
import danggai.app.resinwidget.util.EventObserver
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import org.koin.androidx.viewmodel.ext.android.getViewModel


class MainResinFragment : BindingFragment<ResinFragmentBinding>() {

    companion object {
        val TAG: String = MainResinFragment::class.java.simpleName
        fun newInstance() = MainResinFragment()
    }

    private lateinit var mVM: MainViewModel
    private lateinit var mAdView : AdView

    @LayoutRes
    override fun getLayoutResId() = R.layout.resin_fragment

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

        when(mVM.lvAutoRefreshPeriod.value) {
            -1L -> binding.rbDisable.isChecked = true
            15L -> binding.rb15m.isChecked = true
            30L -> binding.rb30m.isChecked = true
            60L -> binding.rb1h.isChecked = true
            120L -> binding.rb2h.isChecked = true
        }

        when(mVM.lvTimeNotation.value) {
            -1 -> binding.rbRemainTime.isChecked = true
            0 -> binding.rbRemainTime.isChecked = true
            1 -> binding.rbFullChargeTime.isChecked = true
        }

    }
}