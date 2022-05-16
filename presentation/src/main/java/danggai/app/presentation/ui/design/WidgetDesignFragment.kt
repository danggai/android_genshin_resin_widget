package danggai.app.presentation.ui.design

import android.app.WallpaperManager
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentWidgetDesignBinding
import danggai.app.presentation.ui.design.charaters.WidgetDesignCharacterFragment
import danggai.app.presentation.ui.design.detail.WidgetDesignDetailFragment
import danggai.app.presentation.ui.design.resin.WidgetDesignResinFragment
import danggai.app.presentation.util.log

@AndroidEntryPoint
class WidgetDesignFragment : BindingFragment<FragmentWidgetDesignBinding, WidgetDesignViewModel>() {

    companion object {
        val TAG: String = WidgetDesignFragment::class.java.simpleName
        fun newInstance() = WidgetDesignFragment()
    }

    private val mVM: WidgetDesignViewModel by activityViewModels()

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_widget_design

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM
        binding.vm?.setCommonFun()

        initTabLayout()

        initUi()
    }

    private fun initTabLayout() {
        val pagerAdapter = WidgetDesignAdapter(requireActivity())

        pagerAdapter.addFragment(WidgetDesignResinFragment())
        pagerAdapter.addFragment(WidgetDesignDetailFragment())
        pagerAdapter.addFragment(WidgetDesignCharacterFragment())

        binding.vpMain.adapter = pagerAdapter

        TabLayoutMediator(binding.tlTop, binding.vpMain) { tab, position ->
            tab.text = when (position) {
                0 -> "Resin"
                1 -> "Detail"
                2 -> "Characters"
                else -> ""
            }
        }.attach()
    }

    private fun initUi() {
        mVM.initUi()
        context?.let { it ->
            try {
                val wallpaperManager = WallpaperManager.getInstance(it)
                val wallpaperDrawable = wallpaperManager.drawable

                binding.vpMain.background = wallpaperDrawable
            } catch (e:SecurityException) {
                log.e()
                makeToast(it, getString(R.string.msg_toast_storage_permission_denied))
            }
        }
    }
}