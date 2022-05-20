package danggai.app.presentation.ui.design.charaters.select

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentWidgetDesignCharacterSelectBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.design.WidgetDesignViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WidgetDesignSelectCharacterFragment : BindingFragment<FragmentWidgetDesignCharacterSelectBinding, WidgetDesignViewModel>() {

    companion object {
        val TAG: String = WidgetDesignSelectCharacterFragment::class.java.simpleName
        fun newInstance() = WidgetDesignSelectCharacterFragment()
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_widget_design_character_select

    private val mVM: WidgetDesignViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM

        initSf()
    }

    private fun initSf() {
        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                mVM.sfFinishSelectFragment.collect {
                    activity?.onBackPressed()
                }
            }
        }
    }
}