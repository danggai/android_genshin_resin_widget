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
import danggai.app.presentation.util.TimeFunction
import danggai.app.presentation.util.log
import danggai.domain.network.dailynote.entity.Transformer
import danggai.domain.network.dailynote.entity.TransformerTime
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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
            Constant.PREF_TIME_NOTATION_DEFAULT,
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
        CoroutineScope(Dispatchers.Main).launch {
            launch {
                mVM.sfWidgetTheme.collect {
                    context?.let { _context ->
                        log.e()
                        val unwrappedDrawable = AppCompatResources.getDrawable(_context, R.drawable.rounded_square_5dp)
                        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)

                        val bgColor: Int =  ColorUtils.setAlphaComponent(getColor(_context,
                            if (mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                                R.color.white
                            } else if ((mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                                R.color.black
                            } else {
                                R.color.white
                            }
                        ), mVM.sfTransparency.value)

                        val mainFontColor: Int = getColor(_context,
                            if (mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                                R.color.widget_font_main_light
                            } else if ((mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                                R.color.widget_font_main_dark
                            } else {
                                R.color.widget_font_main_light
                            }
                        )

                        val subFontColor: Int = getColor(_context,
                            if (mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_LIGHT) {
                                R.color.widget_font_sub_light
                            } else if ((mVM.sfWidgetTheme.value == Constant.PREF_WIDGET_THEME_DARK) || _context.isDarkMode()) {
                                R.color.widget_font_sub_dark
                            } else {
                                R.color.widget_font_sub_light
                            }
                        )

                        binding.widget.apply {
                            llRoot.setBackgroundColor(bgColor)

                            ivRefersh.setColorFilter(subFontColor)
                            tvSyncTime.setTextColor(subFontColor)
                            tvDisable.setTextColor(subFontColor)

                            tvResin.setTextColor(mainFontColor)
                            tvResinTitle.setTextColor(mainFontColor)
                            tvResinTime.setTextColor(mainFontColor)
                            tvResinTimeTitle.setTextColor(mainFontColor)
                            tvDailyCommission.setTextColor(mainFontColor)
                            tvDailyCommissionTitle.setTextColor(mainFontColor)
                            tvWeeklyBoss.setTextColor(mainFontColor)
                            tvWeeklyBossTitle.setTextColor(mainFontColor)
                            tvExpeditionTitle.setTextColor(mainFontColor)
                            tvExpeditionTime.setTextColor(mainFontColor)
                            tvRealmCurrency.setTextColor(mainFontColor)
                            tvRealmCurrencyTitle.setTextColor(mainFontColor)
                            tvRealmCurrencyTime.setTextColor(mainFontColor)
                            tvRealmCurrencyTimeTitle.setTextColor(mainFontColor)
                            tvTransformer.setTextColor(mainFontColor)
                            tvTransformerTitle.setTextColor(mainFontColor)

                            llRoot.background = wrappedDrawable
                        }
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
                    binding.widget.apply {
                        it.toFloat().let {
                            tvResin.textSize = it
                            tvResinTitle.textSize = it
                            tvResinTime.textSize = it
                            tvResinTimeTitle.textSize = it
                            tvDailyCommission.textSize = it
                            tvDailyCommissionTitle.textSize = it
                            tvWeeklyBoss.textSize = it
                            tvWeeklyBossTitle.textSize = it
                            tvRealmCurrency.textSize = it
                            tvRealmCurrencyTitle.textSize = it
                            tvRealmCurrencyTime.textSize = it
                            tvRealmCurrencyTimeTitle.textSize = it
                            tvExpeditionTitle.textSize = it
                            tvExpeditionTitle.textSize = it
                            tvExpeditionTime.textSize = it
                            tvExpeditionTime.textSize = it
                            tvTransformer.textSize = it
                            tvTransformerTitle.textSize = it
                        }
                    }
                }
            }

            launch {
                mVM.sfDetailTimeNotation.collect {
                    context?.let { _context ->
                        log.e()

                        val fakeResinRemainTime = "37913"
                        val fakeRealmCurrencyRemainTime = "96123"
                        val fakeExpedtionRemainTime = "74285"
                        val fakeTransformer = Transformer(true, TransformerTime(3, 0, 0, 0, true))

                        binding.widget.apply {
                            when (mVM.sfDetailTimeNotation.value) {
                                Constant.PREF_TIME_NOTATION_DEFAULT,
                                Constant.PREF_TIME_NOTATION_REMAIN_TIME,
                                -> {
                                    rlResinTime.visibility =
                                        if (mVM.sfResinDataVisibility.value) View.VISIBLE else View.GONE
                                    rlRealmCurrencyTime.visibility =
                                        if (mVM.sfRealmCurrencyDataVisibility.value) View.VISIBLE else View.GONE

                                    tvResinTimeTitle.text =
                                        _context.getString(R.string.until_fully_replenished)
                                    tvRealmCurrencyTimeTitle.text =
                                        _context.getString(R.string.until_fully_replenished)
                                    tvExpeditionTitle.text =
                                        _context.getString(R.string.until_expeditions_done)
                                }
                                Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME ->  {
                                    rlResinTime.visibility = if (mVM.sfResinDataVisibility.value) View.VISIBLE else View.GONE
                                    rlRealmCurrencyTime.visibility = if (mVM.sfRealmCurrencyDataVisibility.value) View.VISIBLE else View.GONE

                                    tvResinTimeTitle.text = _context.getString(R.string.estimated_replenishment_time)
                                    tvRealmCurrencyTimeTitle.text = _context.getString(R.string.estimated_replenishment_time)
                                    tvExpeditionTitle.text = _context.getString(R.string.expeditions_done_at)
                                }
                                Constant.PREF_TIME_NOTATION_DISABLE ->  {
                                    rlResinTime.visibility = View.GONE
                                    rlRealmCurrencyTime.visibility = View.GONE
                                }
                            }

                            tvResinTime.text = TimeFunction.resinSecondToTime(_context, Date(), fakeResinRemainTime, mVM.sfDetailTimeNotation.value)
                            tvRealmCurrencyTime.text = TimeFunction.realmCurrencySecondToTime(_context, Date(), fakeRealmCurrencyRemainTime, mVM.sfDetailTimeNotation.value)
                            tvExpeditionTime.text = TimeFunction.expeditionSecondToTime(_context, Date(), fakeExpedtionRemainTime, mVM.sfDetailTimeNotation.value)
                            tvTransformer.text = TimeFunction.transformerToTime(_context, Date(), fakeTransformer, mVM.sfDetailTimeNotation.value)
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
                }
            }

            launch {
                mVM.sfTransformerDataVisibility.collect {
                    log.e()
                    binding.widget.rlTransformer.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfDetailUidVisibility.collect {
                    log.e()
                    binding.widget.tvUid.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfDetailNameVisibility.collect {
                    log.e()
                    binding.widget.tvName.visibility = if (it) View.VISIBLE else View.GONE
                }
            }
        }
    }
}