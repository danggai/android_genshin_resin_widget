package danggai.app.presentation.ui.design.charaters

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentWidgetDesignCharacterBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.design.WidgetDesignViewModel
import danggai.app.presentation.ui.design.detail.WidgetDesignDetailFragment
import danggai.domain.util.Constant
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WidgetDesignCharacterFragment : BindingFragment<FragmentWidgetDesignCharacterBinding, WidgetDesignViewModel>() {

    companion object {
        val TAG: String = WidgetDesignDetailFragment::class.java.simpleName
        fun newInstance() = WidgetDesignDetailFragment()
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_widget_design_character

    private val mVM: WidgetDesignViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM

        initUi()
        initSf()    }

    private fun initUi() {
        when(mVM.sfWidgetTheme.value) {
            Constant.PREF_WIDGET_THEME_AUTOMATIC -> binding.rbThemeAutomatic.isChecked = true
            Constant.PREF_WIDGET_THEME_LIGHT -> binding.rbThemeLight.isChecked = true
            Constant.PREF_WIDGET_THEME_DARK -> binding.rbThemeDark.isChecked = true
            else -> binding.rbThemeAutomatic.isChecked = true
        }
    }

    private fun initSf() {
        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                mVM.sfCharacterListRefreshSwitch.collect {
                    if (mVM.sfSelectedCharacterList.value.size == 0) {
                        binding.llDisable.visibility = View.VISIBLE
                        binding.gvCharacters.visibility = View.GONE
                    } else {
                        binding.llDisable.visibility = View.GONE
                        binding.gvCharacters.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}