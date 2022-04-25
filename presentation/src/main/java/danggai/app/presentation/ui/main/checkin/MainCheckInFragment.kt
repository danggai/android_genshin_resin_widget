package danggai.app.presentation.ui.main.checkin

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentMainCheckInBinding
import danggai.app.presentation.ui.main.MainViewModel

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
    }
}