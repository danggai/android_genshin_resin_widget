package danggai.app.resinwidget.ui.design.detail

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.databinding.FragmentWidgetDesignDetailBinding
import danggai.app.resinwidget.ui.BindingFragment
import danggai.app.resinwidget.ui.design.WidgetDesignViewModel
import danggai.app.resinwidget.util.CommonFunction.isDarkMode
import danggai.app.resinwidget.util.PreferenceManager
import danggai.app.resinwidget.util.log
import org.koin.androidx.viewmodel.ext.android.getViewModel


class WidgetDesignDetailFragment : BindingFragment<FragmentWidgetDesignDetailBinding>() {

    companion object {
        val TAG: String = WidgetDesignDetailFragment::class.java.simpleName
        fun newInstance() = WidgetDesignDetailFragment()
    }

    private lateinit var mVM: WidgetDesignViewModel

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_widget_design_detail

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
            it.llDisable.visibility = View.GONE
        }

        context?.let {
            mVM.lvDetailTimeNotation.value = PreferenceManager.getIntDetailTimeNotation(it)

            mVM.lvResinDataVisibility.value = PreferenceManager.getBooleanWidgetResinDataVisibility(it)
            mVM.lvDailyCommissionDataVisibility.value = PreferenceManager.getBooleanWidgetDailyCommissionDataVisibility(it)
            mVM.lvWeeklyBossDataVisibility.value = PreferenceManager.getBooleanWidgetWeeklyBossDataVisibility(it)
            mVM.lvRealmCurrencyDataVisibility.value = PreferenceManager.getBooleanWidgetRealmCurrencyDataVisibility(it)
            mVM.lvExpeditionDataVisibility.value = PreferenceManager.getBooleanWidgetExpeditionDataVisibility(it)

            when(mVM.lvDetailTimeNotation.value) {
                Constant.PREF_TIME_NOTATION_REMAIN_TIME -> binding.rbRemainTime.isChecked = true
                Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> binding.rbFullChargeTime.isChecked = true
                else -> binding.rbRemainTime.isChecked = true
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
        mVM.lvTransparency.observe(viewLifecycleOwner) {
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
        }

        mVM.lvFontSizeDetail.observe(viewLifecycleOwner) {
            binding.widget.tvResin.textSize = it.toFloat()
            binding.widget.tvResinTitle.textSize = it.toFloat()
            binding.widget.tvResinTime.textSize = it.toFloat()
            binding.widget.tvResinTimeTitle.textSize = it.toFloat()
            binding.widget.tvDailyCommission.textSize = it.toFloat()
            binding.widget.tvDailyCommissionTitle.textSize = it.toFloat()
            binding.widget.tvWeeklyBoss.textSize = it.toFloat()
            binding.widget.tvWeeklyBossTitle.textSize = it.toFloat()
            binding.widget.tvRealmCurrency.textSize = it.toFloat()
            binding.widget.tvRealmCurrencyTitle.textSize = it.toFloat()
            binding.widget.tvRealmCurrencyTime.textSize = it.toFloat()
            binding.widget.tvRealmCurrencyTimeTitle.textSize = it.toFloat()
            binding.widget.tvExpedition.textSize = it.toFloat()
            binding.widget.tvExpeditionTitle.textSize = it.toFloat()
            binding.widget.tvExpeditionTime.textSize = it.toFloat()
            binding.widget.tvExpeditionTimeTitle.textSize = it.toFloat()
        }

        mVM.lvWidgetTheme.observe(viewLifecycleOwner) {
            context?.let { _context ->
                log.e()
                val unwrappedDrawable = AppCompatResources.getDrawable(_context, R.drawable.rounded_square_5dp)
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)

                val bgColor: Int =  if (mVM.lvWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                    ColorUtils.setAlphaComponent(getColor(_context, R.color.white), PreferenceManager.getIntBackgroundTransparency(_context))
                } else if ((mVM.lvWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                    ColorUtils.setAlphaComponent(getColor(_context, R.color.black), PreferenceManager.getIntBackgroundTransparency(_context))
                } else {
                    ColorUtils.setAlphaComponent(getColor(_context, R.color.white), PreferenceManager.getIntBackgroundTransparency(_context))
                }

                val mainFontColor: Int =  if (mVM.lvWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                    R.color.widget_font_main_light
                } else if ((mVM.lvWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                    R.color.widget_font_main_dark
                } else {
                    R.color.widget_font_main_light
                }

                val subFontColor: Int = if (mVM.lvWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                    R.color.widget_font_sub_light
                } else if ((mVM.lvWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                    R.color.widget_font_sub_dark
                } else {
                    R.color.widget_font_sub_light
                }

                binding.widget.llRoot.setBackgroundColor(bgColor)
                binding.widget.ivRefersh.setColorFilter(subFontColor)
                binding.widget.tvSyncTime.setTextColor(subFontColor)
                binding.widget.tvDisable.setTextColor(subFontColor)

                binding.widget.tvResin.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvResinTitle.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvResinTime.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvResinTimeTitle.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvDailyCommission.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvDailyCommissionTitle.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvWeeklyBoss.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvWeeklyBossTitle.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvExpedition.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvExpeditionTitle.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvExpeditionTime.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvExpeditionTimeTitle.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvRealmCurrency.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvRealmCurrencyTitle.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvRealmCurrencyTime.setTextColor(getColor(_context, mainFontColor))
                binding.widget.tvRealmCurrencyTimeTitle.setTextColor(getColor(_context, mainFontColor))

                binding.widget.llRoot.background = wrappedDrawable
            }
        }

        mVM.lvDetailTimeNotation.observe(viewLifecycleOwner) {
            context?.let { _context ->
                log.e()

                if (mVM.lvDetailTimeNotation.value == Constant.PREF_TIME_NOTATION_REMAIN_TIME) {
                    binding.widget.tvResinTimeTitle.text = _context.getString(R.string.until_fully_replenished)
                    binding.widget.tvResinTime.text = String.format(getString(R.string.widget_ui_remain_time), 0, 0)
                    binding.widget.tvRealmCurrencyTimeTitle.text = _context.getString(R.string.until_fully_replenished)
                    binding.widget.tvRealmCurrencyTime.text = String.format(getString(R.string.widget_ui_remain_time), 0, 0)
                    binding.widget.tvExpeditionTimeTitle.text = _context.getString(R.string.until_all_completed)
                    binding.widget.tvExpeditionTime.text = String.format(getString(R.string.widget_ui_remain_time), 0, 0)
                } else {
                    binding.widget.tvResinTimeTitle.text = _context.getString(R.string.when_fully_replenished)
                    binding.widget.tvResinTime.text = String.format(getString(R.string.widget_ui_today), 0, 0)
                    binding.widget.tvRealmCurrencyTimeTitle.text = _context.getString(R.string.when_fully_replenished)
                    binding.widget.tvRealmCurrencyTime.text = String.format(getString(R.string.widget_ui_date), "1"+getString(R.string.date_st), 0, 0)
                    binding.widget.tvExpeditionTimeTitle.text = _context.getString(R.string.estimated_completion_time)
                    binding.widget.tvExpeditionTime.text = String.format(getString(R.string.widget_ui_today), 0, 0)
                }
            }
        }

        mVM.lvResinDataVisibility.observe(viewLifecycleOwner) {
            log.e()
            binding.widget.rlResin.visibility = if (it) View.VISIBLE else View.GONE
            binding.widget.rlResinTime.visibility = if (it) View.VISIBLE else View.GONE
        }

        mVM.lvDailyCommissionDataVisibility.observe(viewLifecycleOwner) {
            log.e()
            binding.widget.rlDailyCommission.visibility = if (it) View.VISIBLE else View.GONE
        }

        mVM.lvWeeklyBossDataVisibility.observe(viewLifecycleOwner) {
            log.e()
            binding.widget.rlWeeklyBoss.visibility = if (it) View.VISIBLE else View.GONE
        }

        mVM.lvRealmCurrencyDataVisibility.observe(viewLifecycleOwner) {
            log.e()
            binding.widget.rlRealmCurrency.visibility = if (it) View.VISIBLE else View.GONE
            binding.widget.rlRealmCurrencyTime.visibility = if (it) View.VISIBLE else View.GONE
        }

        mVM.lvExpeditionDataVisibility.observe(viewLifecycleOwner) {
            log.e()
            binding.widget.rlExpedition.visibility = if (it) View.VISIBLE else View.GONE
            binding.widget.rlExpeditionTime.visibility = if (it) View.VISIBLE else View.GONE
        }

    }
}