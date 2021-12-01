package danggai.app.resinwidget.ui.main.checkin

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.FragmentCheckInBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.ui.main.MainViewModel
import danggai.app.resinwidget.util.PreferenceManager
import org.koin.androidx.viewmodel.ext.android.getViewModel


class MainCheckInFragment : BindingFragment<FragmentCheckInBinding>() {

    companion object {
        val TAG: String = MainCheckInFragment::class.java.simpleName
        fun newInstance() = MainCheckInFragment()
    }

    private lateinit var mVM: MainViewModel

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_check_in

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
            mVM.lvTimeNotation.value = PreferenceManager.getIntTimeNotation(it)
            mVM.lvWidgetTheme.value = PreferenceManager.getIntWidgetTheme(it)

            mVM.lvEnableNotiEach40Resin.value = PreferenceManager.getBooleanNotiEach40Resin(it)
            mVM.lvEnableNoti140Resin.value = PreferenceManager.getBooleanNoti140Resin(it)
            mVM.lvEnableNotiCustomResin.value = PreferenceManager.getBooleanNotiCustomResin(it)
            mVM.lvCustomNotiResin.value = PreferenceManager.getIntCustomTargetResin(it).let { int ->
                if (int == -1) "0" else int.toString()
            }

            mVM.lvEnableAutoCheckIn.value = PreferenceManager.getBooleanEnableAutoCheckIn(it)
            mVM.lvEnableNotiCheckinSuccess.value = PreferenceManager.getBooleanNotiCheckInSuccess(it)
            mVM.lvEnableNotiCheckinFailed.value = PreferenceManager.getBooleanNotiCheckInFailed(it)
        }

    }

}