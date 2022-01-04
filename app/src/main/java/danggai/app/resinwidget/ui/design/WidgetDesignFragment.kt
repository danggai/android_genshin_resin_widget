package danggai.app.resinwidget.ui.design

import android.app.WallpaperManager
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.FragmentWidgetDesignBinding
import danggai.app.resinwidget.ui.BindingFragment
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


        initUi()
        initLv()
    }



    private fun initUi() {
        binding.widget.let {
            it.pbLoading.visibility = View.GONE
            it.ivResin.visibility = View.VISIBLE
            it.llResin.visibility = View.VISIBLE
            it.llDisable.visibility = View.GONE
        }

        context?.let { it ->
            mVM.lvWidgetTheme.value = PreferenceManager.getIntWidgetTheme(it)
            mVM.lvTransparency.value = PreferenceManager.getIntBackgroundTransparency(it)

            try {
                val wallpaperManager = WallpaperManager.getInstance(it)
                val wallpaperDrawable = wallpaperManager.drawable

                binding.llRoot.background = wallpaperDrawable
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
                PreferenceManager.setIntBackgroundTransparency(_context, mVM.lvTransparency.value)

                makeToast(_context, getString(R.string.msg_toast_save_done))
                activity?.finish()
            }
        })

        mVM.lvTransparency.observe(viewLifecycleOwner, {
            context?.let { _context ->
                log.e()
                val unwrappedDrawable = AppCompatResources.getDrawable(_context, R.drawable.rounded_square_5dp)
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)

                val color: Int = if (mVM.lvWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) getColor(_context, R.color.white)
                else if ((mVM.lvWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) getColor(_context, R.color.black)
                else getColor(_context, R.color.white)

                DrawableCompat.setTint(wrappedDrawable, ColorUtils.setAlphaComponent(color, mVM.lvTransparency.value))

                binding.widget.llRoot.background = wrappedDrawable
            }
        })

        mVM.lvWidgetTheme.observe(viewLifecycleOwner, {
            context?.let { _context ->
                log.e()
                val unwrappedDrawable = AppCompatResources.getDrawable(_context, R.drawable.rounded_square_5dp)
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)

                if (mVM.lvWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                    DrawableCompat.setTint(wrappedDrawable, ColorUtils.setAlphaComponent(getColor(_context, R.color.white), mVM.lvTransparency.value))
                    binding.widget.tvResin.setTextColor(getColor(_context, R.color.widget_font_main_light))
                    binding.widget.tvResinMax.setTextColor(getColor(_context, R.color.widget_font_main_light))
                    binding.widget.tvRemainTime.setTextColor(getColor(_context, R.color.widget_font_main_light))
                    binding.widget.ivRefersh.setColorFilter(getColor(_context, R.color.widget_font_sub_light))
                    binding.widget.tvSyncTime.setTextColor(getColor(_context, R.color.widget_font_sub_light))
                } else if ((mVM.lvWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                    DrawableCompat.setTint(wrappedDrawable, ColorUtils.setAlphaComponent(getColor(_context, R.color.black), mVM.lvTransparency.value))
                    binding.widget.tvResin.setTextColor(getColor(_context, R.color.widget_font_main_dark))
                    binding.widget.tvResinMax.setTextColor(getColor(_context, R.color.widget_font_main_dark))
                    binding.widget.tvRemainTime.setTextColor(getColor(_context, R.color.widget_font_main_dark))
                    binding.widget.ivRefersh.setColorFilter(getColor(_context, R.color.widget_font_sub_dark))
                    binding.widget.tvSyncTime.setTextColor(getColor(_context, R.color.widget_font_sub_dark))
                } else {
                    DrawableCompat.setTint(wrappedDrawable, ColorUtils.setAlphaComponent(getColor(_context, R.color.white), mVM.lvTransparency.value))
                    binding.widget.tvResin.setTextColor(getColor(_context, R.color.widget_font_main_light))
                    binding.widget.tvResinMax.setTextColor(getColor(_context, R.color.widget_font_main_light))
                    binding.widget.tvRemainTime.setTextColor(getColor(_context, R.color.widget_font_main_light))
                    binding.widget.ivRefersh.setColorFilter(getColor(_context, R.color.widget_font_sub_light))
                    binding.widget.tvSyncTime.setTextColor(getColor(_context, R.color.widget_font_sub_light))
                }

                binding.widget.llRoot.background = wrappedDrawable


            }
        })
    }

}