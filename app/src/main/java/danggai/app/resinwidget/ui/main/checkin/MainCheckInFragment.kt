package danggai.app.resinwidget.ui.main.checkin

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdView
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.CheckInFragmentBinding
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


class MainCheckInFragment : BindingFragment<CheckInFragmentBinding>() {

    companion object {
        val TAG: String = MainCheckInFragment::class.java.simpleName
        fun newInstance() = MainCheckInFragment()
    }

    private lateinit var mVM: MainViewModel

    @LayoutRes
    override fun getLayoutResId() = R.layout.check_in_fragment

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
        }
    }

}