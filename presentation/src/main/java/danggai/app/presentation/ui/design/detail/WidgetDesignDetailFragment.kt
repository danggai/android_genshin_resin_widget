package danggai.app.presentation.ui.design.detail

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
import danggai.app.presentation.databinding.FragmentWidgetDesignDetailBinding
import danggai.app.presentation.extension.repeatOnLifeCycleStarted
import danggai.app.presentation.ui.design.WidgetDesignViewModel
import danggai.app.presentation.util.CommonFunction.isDarkMode
import danggai.app.presentation.util.log
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WidgetDesignDetailFragment : BindingFragment<FragmentWidgetDesignDetailBinding, WidgetDesignViewModel>() {

    companion object {
        val TAG: String = WidgetDesignDetailFragment::class.java.simpleName
        fun newInstance() = WidgetDesignDetailFragment()
    }

    @LayoutRes
    override fun getLayoutResId() = R.layout.fragment_widget_design_detail

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
            it.llDisable.visibility = View.GONE
        }

        when(mVM.sfDetailTimeNotation.value) {
            Constant.PREF_TIME_NOTATION_REMAIN_TIME -> binding.rbRemainTime.isChecked = true
            Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> binding.rbFullChargeTime.isChecked = true
            Constant.PREF_TIME_NOTATION_DISABLE -> binding.rbDisableTime.isChecked = true
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
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                mVM.sfWidgetTheme.collect {
                    context?.let { _context ->
                        log.e()
                        val unwrappedDrawable = AppCompatResources.getDrawable(_context, R.drawable.rounded_square_5dp)
                        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)

                        val bgColor: Int =  if (mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                            ColorUtils.setAlphaComponent(getColor(_context, R.color.white), mVM.sfTransparency.value)
                        } else if ((mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                            ColorUtils.setAlphaComponent(getColor(_context, R.color.black), mVM.sfTransparency.value)
                        } else {
                            ColorUtils.setAlphaComponent(getColor(_context, R.color.white), mVM.sfTransparency.value)
                        }

                        val mainFontColor: Int =  if (mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                            R.color.widget_font_main_light
                        } else if ((mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                            R.color.widget_font_main_dark
                        } else {
                            R.color.widget_font_main_light
                        }

                        val subFontColor: Int = if (mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                            R.color.widget_font_sub_light
                        } else if ((mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
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
                        binding.widget.tvTransformer.setTextColor(getColor(_context, mainFontColor))
                        binding.widget.tvTransformerTitle.setTextColor(getColor(_context, mainFontColor))
                        binding.widget.tvTransformerTime.setTextColor(getColor(_context, mainFontColor))
                        binding.widget.tvTransformerTimeTitle.setTextColor(getColor(_context, mainFontColor))

                        binding.widget.llRoot.background = wrappedDrawable
                    }
                }
            }
        }

        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                mVM.sfTransparency.collect {
                    context?.let { _context ->
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
                mVM.sfFontSizeDetail.collect {
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
                    binding.widget.tvTransformer.textSize = it.toFloat()
                    binding.widget.tvTransformerTitle.textSize = it.toFloat()
                    binding.widget.tvTransformerTime.textSize = it.toFloat()
                    binding.widget.tvTransformerTimeTitle.textSize = it.toFloat()
                }
            }

            launch {
                mVM.sfDetailTimeNotation.collect {
                    context?.let { _context ->
                        log.e()

                        when (mVM.sfDetailTimeNotation.value) {
                            Constant.PREF_TIME_NOTATION_REMAIN_TIME -> {
                                binding.widget.rlResinTime.visibility = if (mVM.sfResinDataVisibility.value) View.VISIBLE else View.GONE
                                binding.widget.rlExpeditionTime.visibility = if (mVM.sfExpeditionDataVisibility.value) View.VISIBLE else View.GONE
                                binding.widget.rlRealmCurrencyTime.visibility = if (mVM.sfRealmCurrencyDataVisibility.value) View.VISIBLE else View.GONE
                                binding.widget.rlTransformerTime.visibility = if (mVM.sfTransformerDataVisibility.value) View.VISIBLE else View.GONE

                                binding.widget.tvResinTimeTitle.text = _context.getString(R.string.until_fully_replenished)
                                binding.widget.tvResinTime.text = String.format(getString(R.string.widget_ui_remain_time), 0, 0)
                                binding.widget.tvRealmCurrencyTimeTitle.text = _context.getString(R.string.until_fully_replenished)
                                binding.widget.tvRealmCurrencyTime.text = String.format(getString(R.string.widget_ui_remain_time), 0, 0)
                                binding.widget.tvExpeditionTimeTitle.text = _context.getString(R.string.until_all_completed)
                                binding.widget.tvExpeditionTime.text = String.format(getString(R.string.widget_ui_remain_time), 0, 0)
                                binding.widget.tvTransformerTimeTitle.text = _context.getString(R.string.until_reusable)
                                binding.widget.tvTransformerTime.text = String.format(getString(R.string.widget_ui_remain_time), 0, 0)
                            }
                            Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME ->  {
                                binding.widget.rlResinTime.visibility = if (mVM.sfResinDataVisibility.value) View.VISIBLE else View.GONE
                                binding.widget.rlExpeditionTime.visibility = if (mVM.sfExpeditionDataVisibility.value) View.VISIBLE else View.GONE
                                binding.widget.rlRealmCurrencyTime.visibility = if (mVM.sfRealmCurrencyDataVisibility.value) View.VISIBLE else View.GONE
                                binding.widget.rlTransformerTime.visibility = if (mVM.sfTransformerDataVisibility.value) View.VISIBLE else View.GONE

                                binding.widget.tvResinTimeTitle.text = _context.getString(R.string.estimated_replenishment_time)
                                binding.widget.tvResinTime.text = String.format(getString(R.string.widget_ui_today), 0, 0)
                                binding.widget.tvRealmCurrencyTimeTitle.text = _context.getString(R.string.estimated_replenishment_time)
                                binding.widget.tvRealmCurrencyTime.text = String.format(getString(R.string.widget_ui_date), "1"+getString(R.string.date_st), 0, 0)
                                binding.widget.tvExpeditionTimeTitle.text = _context.getString(R.string.estimated_completion_time)
                                binding.widget.tvExpeditionTime.text = String.format(getString(R.string.widget_ui_today), 0, 0)
                                binding.widget.tvTransformerTimeTitle.text = _context.getString(R.string.estimated_reusable_time)
                                binding.widget.tvTransformerTime.text = String.format(getString(R.string.widget_ui_date), "1"+getString(R.string.date_st), 0, 0)
                            }
                            Constant.PREF_TIME_NOTATION_DISABLE ->  {
                                binding.widget.rlResinTime.visibility = View.GONE
                                binding.widget.rlExpeditionTime.visibility = View.GONE
                                binding.widget.rlRealmCurrencyTime.visibility = View.GONE
                                binding.widget.rlTransformerTime.visibility = View.GONE
                            }
                        }
                    }
                }
            }

            launch {
                mVM.sfResinDataVisibility.collect {
                    log.e()
                    binding.widget.rlResin.visibility = if (it) View.VISIBLE else View.GONE
                    binding.widget.rlResinTime.visibility = if (it && mVM.sfDetailTimeNotation.value != Constant.PREF_TIME_NOTATION_DISABLE) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfDailyCommissionDataVisibility.collect {
                    log.e()
                    binding.widget.rlDailyCommission.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfWeeklyBossDataVisibility.collect {
                    log.e()
                    binding.widget.rlWeeklyBoss.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfRealmCurrencyDataVisibility.collect {
                    log.e()
                    binding.widget.rlRealmCurrency.visibility = if (it) View.VISIBLE else View.GONE
                    binding.widget.rlRealmCurrencyTime.visibility = if (it && mVM.sfDetailTimeNotation.value != Constant.PREF_TIME_NOTATION_DISABLE) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfExpeditionDataVisibility.collect {
                    log.e()
                    binding.widget.rlExpedition.visibility = if (it) View.VISIBLE else View.GONE
                    binding.widget.rlExpeditionTime.visibility = if (it && mVM.sfDetailTimeNotation.value != Constant.PREF_TIME_NOTATION_DISABLE) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfTransformerDataVisibility.collect {
                    log.e()
                    binding.widget.rlTransformer.visibility = if (it) View.VISIBLE else View.GONE
                    binding.widget.rlTransformerTime.visibility = if (it && mVM.sfDetailTimeNotation.value != Constant.PREF_TIME_NOTATION_DISABLE) View.VISIBLE else View.GONE
                }
            }
        }
    }
}