package danggai.app.presentation.design

import android.app.WallpaperManager
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.core.util.EventObserver
import danggai.app.presentation.core.util.PreferenceManager
import danggai.app.presentation.core.util.log
import danggai.app.presentation.databinding.FragmentWidgetDesignBinding
import danggai.app.presentation.design.detail.WidgetDesignDetailFragment
import danggai.app.presentation.design.resin.WidgetDesignResinFragment

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
        binding.vm?.setCommonFun(view)

        val pagerAdapter = WidgetDesignAdapter(requireActivity())

        pagerAdapter.addFragment(WidgetDesignResinFragment())
        pagerAdapter.addFragment(WidgetDesignDetailFragment())

        binding.vpMain.adapter = pagerAdapter

        TabLayoutMediator(binding.tlTop, binding.vpMain) { tab, position ->
            tab.text = when (position) {
                0 -> "Resin Widget"
                1 -> "Detail Widget"
                else -> ""
            }
        }.attach()

        initUi()
        initLv()
    }



    private fun initUi() {
        context?.let { it ->
            mVM.lvWidgetTheme.value = PreferenceManager.getIntWidgetTheme(it)
            mVM.lvTransparency.value = PreferenceManager.getIntBackgroundTransparency(it)
            mVM.lvFontSizeDetail.value = PreferenceManager.getIntWidgetDetailFontSize(it)
            mVM.lvFontSizeResin.value = PreferenceManager.getIntWidgetResinFontSize(it)

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

    private fun initLv() {
        mVM.lvSaveData.observe(viewLifecycleOwner, EventObserver { boolean ->
            context?.let { _context ->
                log.e()
                PreferenceManager.setIntWidgetTheme(_context, mVM.lvWidgetTheme.value)
                PreferenceManager.setIntWidgetResinImageVisibility(_context, mVM.lvResinImageVisibility.value)
                PreferenceManager.setIntBackgroundTransparency(_context, mVM.lvTransparency.value)
                PreferenceManager.setIntWidgetResinFontSize(_context, mVM.lvFontSizeResin.value)
                PreferenceManager.setIntWidgetDetailFontSize(_context, mVM.lvFontSizeDetail.value)
                PreferenceManager.setIntResinTimeNotation(_context, mVM.lvResinTimeNotation.value)
                PreferenceManager.setIntDetailTimeNotation(_context, mVM.lvDetailTimeNotation.value)

                PreferenceManager.setBooleanWidgetResinDataVisibility(_context, mVM.lvResinDataVisibility.value)
                PreferenceManager.setBooleanWidgetDailyCommissionDataVisibility(_context, mVM.lvDailyCommissionDataVisibility.value)
                PreferenceManager.setBooleanWidgetWeeklyBossDataVisibility(_context, mVM.lvWeeklyBossDataVisibility.value)
                PreferenceManager.setBooleanWidgetRealmCurrencyDataVisibility(_context, mVM.lvRealmCurrencyDataVisibility.value)
                PreferenceManager.setBooleanWidgetExpeditionDataVisibility(_context, mVM.lvExpeditionDataVisibility.value)

                makeToast(_context, getString(R.string.msg_toast_save_done))
                activity?.finish()
            }
        })
    }

}