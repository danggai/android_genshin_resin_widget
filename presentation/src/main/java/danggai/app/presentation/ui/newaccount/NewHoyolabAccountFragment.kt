package danggai.app.presentation.ui.newaccount

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentNewHoyolabAccountBinding
import danggai.app.presentation.databinding.FragmentWidgetDesignBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.design.charaters.WidgetDesignCharacterFragment
import danggai.app.presentation.ui.design.charaters.select.WidgetDesignSelectCharacterFragment
import danggai.app.presentation.ui.design.detail.WidgetDesignDetailFragment
import danggai.app.presentation.ui.design.resin.WidgetDesignResinFragment
import danggai.app.presentation.ui.widget.TalentWidget
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.domain.util.Constant
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewHoyolabAccountFragment : BindingFragment<FragmentNewHoyolabAccountBinding, NewHoyolabAccountViewModel>() {

    companion object {
        val TAG: String = NewHoyolabAccountFragment::class.java.simpleName
        fun newInstance() = NewHoyolabAccountFragment()
    }

    private val mVM: NewHoyolabAccountViewModel by activityViewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_new_hoyolab_account

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM
        binding.vm?.setCommonFun()

        initSf()
    }

    private fun initSf() {
        viewLifecycleOwner.repeatOnLifeCycleStarted {
//            launch {
//                mVM.sfApplySavedData.collect {
//                    context?.let { _context ->
//                        log.e()
//                        _context.sendBroadcast(
//                            Intent(_context, TalentWidget::class.java)
//                                .setAction(Constant.ACTION_TALENT_WIDGET_REFRESH)
//                        )
//                    }
//                }
//            }
//
//            launch {
//                mVM.sfStartSelectFragment.collect {
//                    parentFragmentManager.beginTransaction()
//                        .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down, R.anim.slide_in_up, R.anim.slide_out_down)
//                        .add(R.id.fragment,
//                            WidgetDesignSelectCharacterFragment.newInstance(),
//                            WidgetDesignSelectCharacterFragment.TAG)
//                        .commit()
//                }
//            }
        }
    }
}