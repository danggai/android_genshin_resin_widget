package danggai.app.resinwidget.ui.design

import android.app.WallpaperManager
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.tabs.TabLayoutMediator
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.FragmentWidgetDesignBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.ui.design.checkin.WidgetDesignFragmentDetail
import danggai.app.resinwidget.ui.design.resin.WidgetDesignFragmentResin
import danggai.app.resinwidget.ui.main.MainAdapter
import danggai.app.resinwidget.ui.main.checkin.MainCheckInFragment
import danggai.app.resinwidget.ui.main.resin.MainResinFragment
import danggai.app.resinwidget.util.CommonFunction.isDarkMode
import danggai.app.resinwidget.util.EventObserver
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import org.koin.androidx.viewmodel.ext.android.getViewModel


class WidgetDesignFragment : BindingFragment<FragmentWidgetDesignBinding>() {

    companion object {
        val TAG: String = WidgetDesignFragment::class.java.simpleName
        fun newInstance() = WidgetDesignFragment()
    }

    private lateinit var mVM: WidgetDesignViewModel

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_widget_design

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = getViewModel()
        binding.lifecycleOwner = viewLifecycleOwner

        binding.vm?.let {
            mVM = it
            it.setCommonFun(view)
        }

        val pagerAdapter = MainAdapter(requireActivity())

        pagerAdapter.addFragment(WidgetDesignFragmentResin())
        pagerAdapter.addFragment(WidgetDesignFragmentDetail())

        binding.vpMain.adapter = pagerAdapter

        TabLayoutMediator(binding.tlBottom, binding.vpMain) { tab, position ->
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

            try {
                val wallpaperManager = WallpaperManager.getInstance(it)
                val wallpaperDrawable = wallpaperManager.drawable

                binding.vpMain.background = wallpaperDrawable
            } catch (e:SecurityException) {
                log.e()
                makeToast(it, getString(R.string.msg_toast_storage_permission_denied))
            }

            when(mVM.lvWidgetTheme.value) {
                Constant.PREF_WIDGET_THEME_AUTOMATIC -> binding.rbThemeAutomatic.isChecked = true
                Constant.PREF_WIDGET_THEME_LIGHT -> binding.rbThemeLight.isChecked = true
                Constant.PREF_WIDGET_THEME_DARK -> binding.rbThemeDark.isChecked = true
                else -> binding.rbThemeAutomatic.isChecked = true
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
                PreferenceManager.setIntTimeNotation(_context, mVM.lvTimeNotation.value)

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