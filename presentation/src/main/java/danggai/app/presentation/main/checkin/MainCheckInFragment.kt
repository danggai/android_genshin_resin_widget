package danggai.app.presentation.main.checkin

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.core.util.PreferenceManager
import danggai.app.presentation.core.util.log
import danggai.app.presentation.databinding.FragmentMainCheckInBinding
import danggai.app.presentation.main.MainViewModel
import danggai.domain.util.Constant
import io.reactivex.rxkotlin.toObservable

@AndroidEntryPoint
class MainCheckInFragment : BindingFragment<FragmentMainCheckInBinding, MainViewModel>() {

    companion object {
        val TAG: String = MainCheckInFragment::class.java.simpleName
        fun newInstance() = MainCheckInFragment()
    }

    private val mVM: MainViewModel by activityViewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_main_check_in

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM
        binding.vm?.setCommonFun(view)

        initUi()
    }

    private fun initUi() {
        context?.let {
            mVM.lvEnableGenshinAutoCheckIn.value = PreferenceManager.getBooleanEnableGenshinAutoCheckIn(it)
            mVM.lvEnableHonkai3rdAutoCheckIn.value = PreferenceManager.getBooleanEnableHonkai3rdAutoCheckIn(it)
            mVM.lvEnableNotiCheckinSuccess.value = PreferenceManager.getBooleanNotiCheckInSuccess(it)
            mVM.lvEnableNotiCheckinFailed.value = PreferenceManager.getBooleanNotiCheckInFailed(it)
        }
    }
}