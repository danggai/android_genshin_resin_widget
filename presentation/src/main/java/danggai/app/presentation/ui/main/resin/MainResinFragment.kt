package danggai.app.presentation.ui.main.resin

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentMainResinBinding
import danggai.app.presentation.ui.main.MainViewModel
import danggai.domain.util.Constant

@AndroidEntryPoint
class MainResinFragment : BindingFragment<FragmentMainResinBinding, MainViewModel>() {

    companion object {
        val TAG: String = MainResinFragment::class.java.simpleName
        fun newInstance() = MainResinFragment()
    }

    private val mVM: MainViewModel by activityViewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_main_resin

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM

        initUi()
    }

    private fun initUi() {
        mVM.initUI()

        when (mVM.sfServer.value) {
            Constant.PREF_SERVER_ASIA -> binding.rbAsia.isChecked = true
            Constant.PREF_SERVER_USA -> binding.rbUsa.isChecked = true
            Constant.PREF_SERVER_EUROPE -> binding.rbEuro.isChecked = true
            Constant.PREF_SERVER_CHT -> binding.rbCht.isChecked = true
            else -> binding.rbAsia.isChecked = true
        }

        when (mVM.sfAutoRefreshPeriod.value) {
            15L -> binding.rb15m.isChecked = true
            30L -> binding.rb30m.isChecked = true
            60L -> binding.rb1h.isChecked = true
            120L -> binding.rb2h.isChecked = true
            else -> binding.rbDisable.isChecked = true
        }
    }
}