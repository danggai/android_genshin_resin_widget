package danggai.app.resinwidget.ui.main.checkin

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.FragmentCheckInBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.ui.main.MainViewModel
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
        }
    }

}