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
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.TimeFunction
import danggai.app.presentation.util.WidgetDesignUtils
import danggai.app.presentation.util.log
import danggai.domain.network.dailynote.entity.Transformer
import danggai.domain.network.dailynote.entity.TransformerTime
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@AndroidEntryPoint
class WidgetDesignDetailFragment :
    BindingFragment<FragmentWidgetDesignDetailBinding, WidgetDesignViewModel>() {

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

        binding.widget.tvWeeklyBoss.text = context?.let { CommonFunction.convertIntToTimes(3, it) }
        binding.widgetHksr.tvEchoOfWar.text =
            context?.let { CommonFunction.convertIntToTimes(3, it) }
        binding.widgetHksr.tvSimulatedUniverseCleared.text =
            context?.let { CommonFunction.convertIntToTimes(0, it) }

        when (mVM.sfDetailTimeNotation.value) {
            Constant.PREF_TIME_NOTATION_DEFAULT,
            Constant.PREF_TIME_NOTATION_REMAIN_TIME -> binding.rbRemainTime.isChecked = true

            Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> binding.rbFullChargeTime.isChecked =
                true

            Constant.PREF_TIME_NOTATION_DISABLE -> binding.rbDisableTime.isChecked = true
            else -> binding.rbRemainTime.isChecked = true
        }

        when (mVM.sfWidgetTheme.value) {
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
                        val unwrappedDrawable =
                            AppCompatResources.getDrawable(_context, R.drawable.rounded_square_5dp)
                        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)

                        val isDarkTheme = WidgetDesignUtils.isDarkTheme(_context, mVM.sfWidgetTheme.value)

                        val bgColor = ColorUtils.setAlphaComponent(
                            getColor(
                                _context,
                                if (isDarkTheme) R.color.black
                                else R.color.white
                            )
                            , mVM.sfTransparency.value
                        )

                        val mainFontColor = getColor(
                            _context,
                            if (isDarkTheme) R.color.widget_font_main_dark
                            else R.color.widget_font_main_light
                        )

                        val subFontColor = getColor(
                            _context,
                            if (isDarkTheme) R.color.widget_font_sub_dark
                            else R.color.widget_font_sub_light
                        )

                        WidgetDesignUtils.applyDetailWidgetColors(binding.widget, bgColor, mainFontColor, subFontColor, wrappedDrawable)
                        WidgetDesignUtils.applyDetailWidgetColors(binding.widgetHksr, bgColor, mainFontColor, subFontColor, wrappedDrawable)
                        WidgetDesignUtils.applyDetailWidgetColors(binding.widgetZzz, bgColor, mainFontColor, subFontColor, wrappedDrawable)
                    }
                }
            }
        }

        viewLifecycleOwner.repeatOnLifeCycleStarted {
            launch {
                mVM.sfSelectedPreview.collect { index ->
                    binding.widget.root.visibility = View.GONE
                    binding.widgetHksr.root.visibility = View.GONE
                    binding.widgetZzz.root.visibility = View.GONE
                    when (index) {
                        0 -> binding.widget.root.visibility = View.VISIBLE
                        1 -> binding.widgetHksr.root.visibility = View.VISIBLE
                        2 -> binding.widgetZzz.root.visibility = View.VISIBLE
                    }
                }
            }

            launch {
                mVM.sfTransparency.collect {
                    context?.let { _context ->
                        val unwrappedDrawable =
                            AppCompatResources.getDrawable(_context, R.drawable.rounded_square_5dp)
                        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)

                        val isDarkTheme = WidgetDesignUtils.isDarkTheme(_context, mVM.sfWidgetTheme.value)

                        val color: Int =
                            if (isDarkTheme) getColor(_context, R.color.black)
                            else getColor(_context, R.color.white)

                        DrawableCompat.setTint(
                            wrappedDrawable,
                            ColorUtils.setAlphaComponent(color, mVM.sfTransparency.value)
                        )

                        binding.widget.llRoot.background = wrappedDrawable
                        binding.widgetHksr.llRoot.background = wrappedDrawable
                        binding.widgetZzz.llRoot.background = wrappedDrawable
                    }
                }
            }

            launch {
                mVM.sfFontSizeDetail.collect {
                    WidgetDesignUtils.setDetailWidgetFontSize(binding.widget, it)
                    WidgetDesignUtils.setDetailWidgetFontSize(binding.widgetHksr, it)
                    WidgetDesignUtils.setDetailWidgetFontSize(binding.widgetZzz, it)
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

                                Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> {
                                    rlResinTime.visibility =
                                        if (mVM.sfResinDataVisibility.value) View.VISIBLE else View.GONE
                                    rlRealmCurrencyTime.visibility =
                                        if (mVM.sfRealmCurrencyDataVisibility.value) View.VISIBLE else View.GONE

                                    tvResinTimeTitle.text =
                                        _context.getString(R.string.estimated_replenishment_time)
                                    tvRealmCurrencyTimeTitle.text =
                                        _context.getString(R.string.estimated_replenishment_time)
                                    tvExpeditionTitle.text =
                                        _context.getString(R.string.expeditions_done_at)
                                }

                                Constant.PREF_TIME_NOTATION_DISABLE -> {
                                    rlResinTime.visibility = View.GONE
                                    rlRealmCurrencyTime.visibility = View.GONE
                                }
                            }

                            tvResinTime.text = TimeFunction.resinSecondToTime(
                                _context,
                                Date(),
                                fakeResinRemainTime,
                                mVM.sfDetailTimeNotation.value
                            )
                            tvRealmCurrencyTime.text = TimeFunction.realmCurrencySecondToTime(
                                _context,
                                Date(),
                                fakeRealmCurrencyRemainTime,
                                mVM.sfDetailTimeNotation.value
                            )
                            tvExpeditionTime.text = TimeFunction.expeditionSecondToTime(
                                _context,
                                Date(),
                                fakeExpedtionRemainTime,
                                mVM.sfDetailTimeNotation.value
                            )
                            tvTransformer.text = TimeFunction.transformerToTime(
                                _context,
                                Date(),
                                fakeTransformer,
                                mVM.sfDetailTimeNotation.value
                            )
                        }

                        binding.widgetHksr.apply {
                            when (mVM.sfDetailTimeNotation.value) {
                                Constant.PREF_TIME_NOTATION_DEFAULT,
                                Constant.PREF_TIME_NOTATION_REMAIN_TIME,
                                -> {
                                    rlTrailblazePowerTime.visibility =
                                        if (mVM.sfTrailBlazepowerDataVisibility.value) View.VISIBLE else View.GONE

                                    tvTrailblazePowerTimeTitle.text =
                                        _context.getString(R.string.until_fully_replenished)
                                    tvAssignmentTitle.text =
                                        _context.getString(R.string.until_assignment_done)
                                }

                                Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> {
                                    rlTrailblazePowerTime.visibility =
                                        if (mVM.sfTrailBlazepowerDataVisibility.value) View.VISIBLE else View.GONE

                                    tvTrailblazePowerTimeTitle.text =
                                        _context.getString(R.string.estimated_replenishment_time)
                                    tvAssignmentTitle.text =
                                        _context.getString(R.string.assignment_done_at)
                                }

                                Constant.PREF_TIME_NOTATION_DISABLE -> {
                                    rlTrailblazePowerTime.visibility = View.GONE
                                }
                            }

                            tvTrailblazePowerTime.text = TimeFunction.resinSecondToTime(
                                _context,
                                Date(),
                                fakeResinRemainTime,
                                mVM.sfDetailTimeNotation.value
                            )
                            tvAssignmentTime.text = TimeFunction.expeditionSecondToTime(
                                _context,
                                Date(),
                                fakeExpedtionRemainTime,
                                mVM.sfDetailTimeNotation.value
                            )
                        }

                        binding.widgetZzz.apply {
                            when (mVM.sfDetailTimeNotation.value) {
                                Constant.PREF_TIME_NOTATION_DEFAULT,
                                Constant.PREF_TIME_NOTATION_REMAIN_TIME,
                                -> {
                                    rlBatteryTime.visibility =
                                        if (mVM.sfBatteryDataVisibility.value) View.VISIBLE else View.GONE

                                    tvBatteryTimeTitle.text =
                                        _context.getString(R.string.until_fully_replenished)
                                }

                                Constant.PREF_TIME_NOTATION_FULL_CHARGE_TIME -> {
                                    rlBatteryTime.visibility =
                                        if (mVM.sfBatteryDataVisibility.value) View.VISIBLE else View.GONE

                                    tvBatteryTimeTitle.text =
                                        _context.getString(R.string.estimated_replenishment_time)
                                }

                                Constant.PREF_TIME_NOTATION_DISABLE -> {
                                    rlBatteryTime.visibility = View.GONE
                                }
                            }

                            tvBatteryTime.text = TimeFunction.resinSecondToTime(
                                _context,
                                Date(),
                                fakeResinRemainTime,
                                mVM.sfDetailTimeNotation.value
                            )
                        }
                    }
                }
            }

            // 원신
            launch {
                mVM.sfResinDataVisibility.collect {
                    log.e()
                    binding.widget.rlResin.visibility = if (it) View.VISIBLE else View.GONE
                    binding.widget.rlResinTime.visibility =
                        if (it && mVM.sfDetailTimeNotation.value != Constant.PREF_TIME_NOTATION_DISABLE) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfDailyCommissionDataVisibility.collect {
                    log.e()
                    binding.widget.rlDailyCommission.visibility =
                        if (it) View.VISIBLE else View.GONE
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
                    binding.widget.rlRealmCurrencyTime.visibility =
                        if (it && mVM.sfDetailTimeNotation.value != Constant.PREF_TIME_NOTATION_DISABLE) View.VISIBLE else View.GONE
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
                    binding.widgetHksr.tvUid.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfDetailNameVisibility.collect {
                    log.e()
                    binding.widget.tvName.visibility = if (it) View.VISIBLE else View.GONE
                    binding.widgetHksr.tvName.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            // 붕괴: 스타레일
            launch {
                mVM.sfTrailBlazepowerDataVisibility.collect {
                    log.e()
                    binding.widgetHksr.rlTrailblazePower.visibility =
                        if (it) View.VISIBLE else View.GONE
                    binding.widgetHksr.rlTrailblazePowerTime.visibility =
                        if (it && mVM.sfDetailTimeNotation.value != Constant.PREF_TIME_NOTATION_DISABLE) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfDailyTrainingDataVisibility.collect {
                    log.e()
                    binding.widgetHksr.rlDailyTraining.visibility =
                        if (it) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfEchoOfWarDataVisibility.collect {
                    log.e()
                    binding.widgetHksr.rlEchoOfWar.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfSimulatedUniverseDataVisibility.collect {
                    log.e()
                    binding.widgetHksr.rlSimulatedUniverse.visibility =
                        if (it) View.VISIBLE else View.GONE
                    binding.widgetHksr.rlSimulatedUniverseCleared.visibility =
                        if (it && mVM.sfSimulatedUniverseClearTimeVisibility.value) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfSimulatedUniverseClearTimeVisibility.collect {
                    log.e()
                    binding.widgetHksr.rlSimulatedUniverseCleared.visibility =
                        if (mVM.sfSimulatedUniverseDataVisibility.value && it) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfAssignmentTimeDataVisibility.collect {
                    log.e()
                    binding.widgetHksr.rlAssignment.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            // 젠레스 존 제로
            launch {
                mVM.sfBatteryDataVisibility.collect {
                    log.e()
                    binding.widgetZzz.rlBattery.visibility = if (it) View.VISIBLE else View.GONE
                    binding.widgetZzz.rlBatteryTime.visibility =
                        if (it && mVM.sfDetailTimeNotation.value != Constant.PREF_TIME_NOTATION_DISABLE) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfEngagementTodayDataVisibility.collect {
                    log.e()
                    binding.widgetZzz.rlEngagementToday.visibility =
                        if (it) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfScratchCardDataVisibility.collect {
                    log.e()
                    binding.widgetZzz.rlScratchCard.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            launch {
                mVM.sfVideoStoreManagementDataVisibility.collect {
                    log.e()
                    binding.widgetZzz.rlVideoStoreManagement.visibility =
                        if (it) View.VISIBLE else View.GONE
                }
            }
        }
    }
}