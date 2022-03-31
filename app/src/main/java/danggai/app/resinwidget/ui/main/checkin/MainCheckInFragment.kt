package danggai.app.resinwidget.ui.main.checkin

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.resinwidget.R
import danggai.app.resinwidget.core.BindingFragment
import danggai.app.resinwidget.databinding.FragmentCheckInBinding
import danggai.app.resinwidget.ui.main.MainViewModel
import danggai.app.resinwidget.util.PreferenceManager

@AndroidEntryPoint
class MainCheckInFragment : BindingFragment<FragmentCheckInBinding, MainViewModel>() {

    companion object {
        val TAG: String = MainCheckInFragment::class.java.simpleName
        fun newInstance() = MainCheckInFragment()
    }

    private val mVM: MainViewModel by activityViewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_check_in

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM
        binding.vm?.setCommonFun(view)

        initUi()
    }

    private fun initUi() {
        context?.let {
            mVM.lvEnableAutoCheckIn.value = PreferenceManager.getBooleanEnableAutoCheckIn(it)
            mVM.lvEnableNotiCheckinSuccess.value = PreferenceManager.getBooleanNotiCheckInSuccess(it)
            mVM.lvEnableNotiCheckinFailed.value = PreferenceManager.getBooleanNotiCheckInFailed(it)
        }

    }

}