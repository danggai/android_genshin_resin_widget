package danggai.app.resinwidget.ui.main.resin

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import com.google.android.gms.ads.AdView
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.FragmentResinBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.ui.main.MainViewModel
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