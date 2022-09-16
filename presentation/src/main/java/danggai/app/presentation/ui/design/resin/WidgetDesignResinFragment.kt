package danggai.app.presentation.ui.design.resin

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import danggai.app.presentation.R
import danggai.app.presentation.core.BindingFragment
import danggai.app.presentation.databinding.FragmentWidgetDesignResinBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.design.WidgetDesignViewModel
import danggai.app.presentation.util.CommonFunction.isDarkMode
import danggai.app.presentation.util.log
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WidgetDesignResinFragment : BindingFragment<FragmentWidgetDesignResinBinding, WidgetDesignViewModel>() {

    companion object {
        val TAG: String = WidgetDesignResinFragment::class.java.simpleName
        fun newInstance() = WidgetDesignResinFragment()
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_widget_design_resin

    private val mVM: WidgetDesignViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = mVM

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

        when(mVM.sfResinImageVisibility.value) {
            Constant.PREF_WIDGET_RESIN_IMAGE_VISIBLE -> binding.rbResinImageVisible.isChecked = true
            Constant.PREF_WIDGET_RESIN_IMAGE_INVISIBLE -> binding.rbResinImageInvisible.isChecked = true
            else -> binding.rbResinImageVisible.isChecked = true
        }

        when(mVM.sfResinTimeNotation.value) {
            Constant.PREF_TIME_NOTATION_DEFAULT,
            Constant.PREF_TIME_NOTATION_REMAIN_TIME -> binding.rbRemainTime.isChecked = true
            Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> binding.rbFullChargeTime.isChecked = true
            else -> binding.rbRemainTime.isChecked = true
        }

        when(mVM.sfWidgetTheme.value) {
            Constant.PREF_WIDGET_THEME_AUTOMATIC -> binding.rbThemeAutomatic.isChecked = true
            Constant.PREF_WIDGET_THEME_LIGHT -> binding.rbThemeLight.isChecked = true
            Constant.PREF_WIDGET_THEME_DARK -> binding.rbThemeDark.isChecked = true
            else -> binding.rbThemeAutomatic.isChecked = true
        }
    }

    private fun initLv() {
        CoroutineScope(Dispatchers.Main).launch {
            launch {
                mVM.sfWidgetTheme.collect {
                    context?.let { _context ->
                        log.e()
                        val unwrappedDrawable = AppCompatResources.getDrawable(_context, R.drawable.rounded_square_5dp)
                        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)

                        if (mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                            DrawableCompat.setTint(wrappedDrawable, ColorUtils.setAlphaComponent(getColor(_context, R.color.white), mVM.sfTransparency.value))
                            binding.widget.tvResin.setTextColor(getColor(_context, R.color.widget_font_main_light))
                            binding.widget.tvResinMax.setTextColor(getColor(_context, R.color.widget_font_main_light))
                            binding.widget.tvRemainTime.setTextColor(getColor(_context, R.color.widget_font_main_light))
                            binding.widget.ivRefersh.setColorFilter(getColor(_context, R.color.widget_font_sub_light))
                            binding.widget.tvSyncTime.setTextColor(getColor(_context, R.color.widget_font_sub_light))
                        } else if ((mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                            DrawableCompat.setTint(wrappedDrawable, ColorUtils.setAlphaComponent(getColor(_context, R.color.black), mVM.sfTransparency.value))
                            binding.widget.tvResin.setTextColor(getColor(_context, R.color.widget_font_main_dark))
                            binding.widget.tvResinMax.setTextColor(getColor(_context, R.color.widget_font_main_dark))
                            binding.widget.tvRemainTime.setTextColor(getColor(_context, R.color.widget_font_main_dark))
                            binding.widget.ivRefersh.setColorFilter(getColor(_context, R.color.widget_font_sub_dark))
                            binding.widget.tvSyncTime.setTextColor(getColor(_context, R.color.widget_font_sub_dark))
                        } else {
                            DrawableCompat.setTint(wrappedDrawable, ColorUtils.setAlphaComponent(getColor(_context, R.color.white), mVM.sfTransparency.value))
                            binding.widget.tvResin.setTextColor(getColor(_context, R.color.widget_font_main_light))
                            binding.widget.tvResinMax.setTextColor(getColor(_context, R.color.widget_font_main_light))
                            binding.widget.tvRemainTime.setTextColor(getColor(_context, R.color.widget_font_main_light))
                            binding.widget.ivRefersh.setColorFilter(getColor(_context, R.color.widget_font_sub_light))
                            binding.widget.tvSyncTime.setTextColor(getColor(_context, R.color.widget_font_sub_light))
                        }
                        binding.widget.llRoot.background = wrappedDrawable
                    }
                }
            }
        }

        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                mVM.sfTransparency.collect {
                    context?.let { _context ->
                        log.e()
                        val unwrappedDrawable = AppCompatResources.getDrawable(_context, R.drawable.rounded_square_5dp)
                        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)

                        val color: Int = if (mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) getColor(_context, R.color.white)
                        else if ((mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) getColor(_context, R.color.black)
                        else getColor(_context, R.color.white)

                        DrawableCompat.setTint(wrappedDrawable, ColorUtils.setAlphaComponent(color, mVM.sfTransparency.value))

                        binding.widget.llRoot.background = wrappedDrawable
                    }
                }
            }

            launch {
                mVM.sfResinImageVisibility.collect {
                    log.e()

                    if (mVM.sfResinImageVisibility.value == Constant.PREF_WIDGET_RESIN_IMAGE_INVISIBLE) {
                        binding.widget.ivResin.visibility = View.GONE
                    } else {
                        binding.widget.ivResin.visibility = View.VISIBLE
                    }
                }
            }

            launch {
                mVM.sfResinTimeNotation.collect {
                    log.e()

                    if (mVM.sfResinTimeNotation.value == Constant.PREF_TIME_NOTATION_REMAIN_TIME) {
                        binding.widget.tvRemainTime.text = String.format(getString(R.string.widget_ui_remain_time), 0, 0)
                    } else {
                        binding.widget.tvRemainTime.text = String.format(getString(R.string.widget_format_max_time), 0, 0)
                    }
                }
            }

            launch {
                mVM.sfResinFontSize.collect {
                    binding.widget.tvResin.textSize = it.toFloat()
                }
            }
        }
    }
}