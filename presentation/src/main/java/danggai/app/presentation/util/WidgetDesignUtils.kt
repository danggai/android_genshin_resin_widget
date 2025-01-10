package danggai.app.presentation.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import danggai.app.presentation.R
import danggai.app.presentation.databinding.WidgetBatteryBinding
import danggai.app.presentation.databinding.WidgetDetailFixedBinding
import danggai.app.presentation.databinding.WidgetHksrDetailFixedBinding
import danggai.app.presentation.databinding.WidgetResinFixedBinding
import danggai.app.presentation.databinding.WidgetTrailblazePowerBinding
import danggai.app.presentation.databinding.WidgetZzzDetailBinding
import danggai.app.presentation.util.CommonFunction.isDarkMode
import danggai.domain.local.DetailWidgetDesignSettings
import danggai.domain.local.ResinWidgetDesignSettings
import danggai.domain.util.Constant

object WidgetDesignUtils {
    fun isDarkTheme(context: Context, widgetTheme: Int) = when (widgetTheme) {
        Constant.PREF_WIDGET_THEME_DARK -> true
        Constant.PREF_WIDGET_THEME_LIGHT -> false
        else -> context.isDarkMode() // 시스템 설정에 따름
    }

    fun setStaminaWidgetTextColor(
        binding: WidgetResinFixedBinding,
        mainFontColor: Int,
        subFontColor: Int
    ) {
        binding.tvResin.setTextColor(mainFontColor)
        binding.tvResinMax.setTextColor(mainFontColor)
        binding.tvRemainTime.setTextColor(mainFontColor)
        binding.ivRefersh.setColorFilter(subFontColor)
        binding.tvSyncTime.setTextColor(subFontColor)
    }

    fun setStaminaWidgetTextColor(
        binding: WidgetTrailblazePowerBinding,
        mainFontColor: Int,
        subFontColor: Int
    ) {
        binding.tvTrailPower.setTextColor(mainFontColor)
        binding.tvTrailPowerMax.setTextColor(mainFontColor)
        binding.tvRemainTime.setTextColor(mainFontColor)
        binding.ivRefresh.setColorFilter(subFontColor)
        binding.tvSyncTime.setTextColor(subFontColor)
    }

    fun setStaminaWidgetTextColor(
        binding: WidgetBatteryBinding,
        mainFontColor: Int,
        subFontColor: Int
    ) {
        binding.tvBattery.setTextColor(mainFontColor)
        binding.tvBatteryMax.setTextColor(mainFontColor)
        binding.tvRemainTime.setTextColor(mainFontColor)
        binding.ivRefersh.setColorFilter(subFontColor)
        binding.tvSyncTime.setTextColor(subFontColor)
    }

    fun applyDetailWidgetColors(
        widget: WidgetDetailFixedBinding,
        bgColor: Int,
        mainFontColor: Int,
        subFontColor: Int,
        wrappedDrawable: Drawable
    ) {
        widget.apply {
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

    fun setDetailWidgetFontSize(widget: WidgetDetailFixedBinding, fontSize: Int) {
        widget.apply {
            fontSize.toFloat().let {
                tvResin.textSize = it
                tvResinTitle.textSize = it
                tvResinTime.textSize = it
                tvResinTimeTitle.textSize = it
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

    fun applyDetailWidgetColors(
        widget: WidgetHksrDetailFixedBinding,
        bgColor: Int,
        mainFontColor: Int,
        subFontColor: Int,
        wrappedDrawable: Drawable
    ) {
        widget.apply {
            llRoot.setBackgroundColor(bgColor)

            ivError.setColorFilter(R.color.red)

            ivRefresh.setColorFilter(subFontColor)
            tvSyncTime.setTextColor(subFontColor)
            tvDisable.setTextColor(subFontColor)

            tvTrailblazePower.setTextColor(mainFontColor)
            tvTrailblazePowerTitle.setTextColor(mainFontColor)
            tvTrailblazePowerTime.setTextColor(mainFontColor)
            tvTrailblazePowerTimeTitle.setTextColor(mainFontColor)
            tvReserveTrailblazePower.setTextColor(mainFontColor)
            tvReserveTrailblazePowerTitle.setTextColor(mainFontColor)
            tvDailyTraining.setTextColor(mainFontColor)
            tvDailyTrainingTitle.setTextColor(mainFontColor)
            tvEchoOfWar.setTextColor(mainFontColor)
            tvEchoOfWarTitle.setTextColor(mainFontColor)
            tvSimulatedUniverse.setTextColor(mainFontColor)
            tvSimulatedUniverseTitle.setTextColor(mainFontColor)
            tvSimulatedUniverseCleared.setTextColor(mainFontColor)
            tvSimulatedUniverseTitleCleared.setTextColor(mainFontColor)
            tvSynchronicityPoint.setTextColor(mainFontColor)
            tvSynchronicityPointTitle.setTextColor(mainFontColor)
            tvAssignmentTime.setTextColor(mainFontColor)
            tvAssignmentTitle.setTextColor(mainFontColor)

            llRoot.background = wrappedDrawable
        }
    }

    fun setDetailWidgetFontSize(widget: WidgetHksrDetailFixedBinding, fontSize: Int) {
        widget.apply {
            fontSize.toFloat().let {
                tvTrailblazePower.textSize = it
                tvTrailblazePowerTitle.textSize = it
                tvTrailblazePowerTime.textSize = it
                tvTrailblazePowerTimeTitle.textSize = it
                tvReserveTrailblazePower.textSize = it
                tvReserveTrailblazePowerTitle.textSize = it
                tvDailyTraining.textSize = it
                tvDailyTrainingTitle.textSize = it
                tvEchoOfWar.textSize = it
                tvEchoOfWarTitle.textSize = it
                tvSimulatedUniverse.textSize = it
                tvSimulatedUniverseTitle.textSize = it
                tvSimulatedUniverseCleared.textSize = it
                tvSimulatedUniverseTitleCleared.textSize = it
                tvSynchronicityPoint.textSize = it
                tvSynchronicityPointTitle.textSize = it
                tvAssignmentTime.textSize = it
                tvAssignmentTitle.textSize = it
            }
        }
    }

    fun applyDetailWidgetColors(
        widget: WidgetZzzDetailBinding,
        bgColor: Int,
        mainFontColor: Int,
        subFontColor: Int,
        wrappedDrawable: Drawable
    ) {
        widget.apply {
            llRoot.setBackgroundColor(bgColor)

            ivRefersh.setColorFilter(subFontColor)
            tvSyncTime.setTextColor(subFontColor)
            tvDisable.setTextColor(subFontColor)

            tvBattery.setTextColor(mainFontColor)
            tvBatteryTime.setTextColor(mainFontColor)
            tvBatteryTitle.setTextColor(mainFontColor)
            tvBatteryTimeTitle.setTextColor(mainFontColor)
            tvScratchCard.setTextColor(mainFontColor)
            tvScratchCardTitle.setTextColor(mainFontColor)
            tvVideoStoreManagement.setTextColor(mainFontColor)
            tvVideoStoreManagementTitle.setTextColor(mainFontColor)
            tvEngagementToday.setTextColor(mainFontColor)
            tvEngagementTodayTitle.setTextColor(mainFontColor)

            llRoot.background = wrappedDrawable
        }
    }

    fun setDetailWidgetFontSize(widget: WidgetZzzDetailBinding, fontSize: Int) {
        widget.apply {
            fontSize.toFloat().let {
                tvBattery.textSize = it
                tvBatteryTitle.textSize = it
                tvBatteryTime.textSize = it
                tvBatteryTimeTitle.textSize = it
                tvEngagementToday.textSize = it
                tvEngagementTodayTitle.textSize = it
                tvScratchCard.textSize = it
                tvScratchCardTitle.textSize = it
                tvVideoStoreManagement.textSize = it
                tvVideoStoreManagementTitle.textSize = it
            }
        }
    }

    fun applyWidgetTheme(
        widgetDesign: ResinWidgetDesignSettings,
        context: Context,
        view: RemoteViews,
    ) {
        val isDarkTheme = isDarkTheme(context, widgetDesign.widgetTheme)

        val bgColor: Int = if (isDarkTheme)
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.black),
                widgetDesign.backgroundTransparency
            )
        else
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.white),
                widgetDesign.backgroundTransparency
            )

        val mainFontColor: Int =
            if (isDarkTheme) ContextCompat.getColor(context, R.color.widget_font_main_dark)
            else ContextCompat.getColor(context, R.color.widget_font_main_light)

        val subFontColor: Int =
            if (isDarkTheme) ContextCompat.getColor(context, R.color.widget_font_sub_dark)
            else ContextCompat.getColor(context, R.color.widget_font_sub_light)

        view.setInt(R.id.ll_root, "setBackgroundColor", bgColor)
        view.setInt(R.id.iv_refersh, "setColorFilter", subFontColor)
        view.setInt(R.id.iv_refresh, "setColorFilter", subFontColor)
        view.setTextColor(R.id.tv_sync_time, subFontColor)
        view.setTextColor(R.id.tv_disable, mainFontColor)

        /* Talent Widget 꼽사리ㅎㅎ; */
        view.setTextColor(R.id.tv_no_talent_ingredient, mainFontColor)
        view.setTextColor(R.id.tv_no_selected_characters, mainFontColor)

        when (view.layoutId) {
            R.layout.widget_resin_fixed,
            R.layout.widget_trailblaze_power,
            R.layout.widget_battery -> {
                val fontSize = widgetDesign.fontSize
                view.setFloat(R.id.tv_resin, "setTextSize", fontSize.toFloat())
                view.setFloat(R.id.tv_trail_power, "setTextSize", fontSize.toFloat())
            }
        }

        view.setTextColor(R.id.tv_resin, mainFontColor)
        view.setTextColor(R.id.tv_resin_max, mainFontColor)
        view.setTextColor(R.id.tv_trail_power, mainFontColor)
        view.setTextColor(R.id.tv_trail_power_max, mainFontColor)
        view.setTextColor(R.id.tv_battery, mainFontColor)
        view.setTextColor(R.id.tv_battery_max, mainFontColor)
        view.setTextColor(R.id.tv_remain_time, mainFontColor)
    }

    fun applyWidgetTheme(
        widgetDesign: DetailWidgetDesignSettings,
        context: Context,
        view: RemoteViews,
    ) {
        val isDarkTheme = isDarkTheme(context, widgetDesign.widgetTheme)

        val bgColor: Int = if (isDarkTheme)
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.black),
                widgetDesign.backgroundTransparency
            )
        else
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(context, R.color.white),
                widgetDesign.backgroundTransparency
            )

        val mainFontColor: Int =
            if (isDarkTheme) ContextCompat.getColor(context, R.color.widget_font_main_dark)
            else ContextCompat.getColor(context, R.color.widget_font_main_light)

        val subFontColor: Int =
            if (isDarkTheme) ContextCompat.getColor(context, R.color.widget_font_sub_dark)
            else ContextCompat.getColor(context, R.color.widget_font_sub_light)

        view.setInt(R.id.ll_root, "setBackgroundColor", bgColor)
        view.setInt(R.id.iv_refersh, "setColorFilter", subFontColor)
        view.setInt(R.id.iv_refresh, "setColorFilter", subFontColor)
        view.setTextColor(R.id.tv_sync_time, subFontColor)

        val mainFontViews = listOf(
            R.id.tv_disable,
            R.id.tv_no_selected_characters,

            R.id.tv_resin,
            R.id.tv_resin_title,
            R.id.tv_resin_time,
            R.id.tv_resin_time_title,
            R.id.tv_daily_commission,
            R.id.tv_daily_commission_title,
            R.id.tv_weekly_boss,
            R.id.tv_weekly_boss_title,
            R.id.tv_expedition_title,
            R.id.tv_expedition_time,
            R.id.tv_transformer_title,
            R.id.tv_transformer,
            R.id.tv_realm_currency,
            R.id.tv_realm_currency_title,
            R.id.tv_realm_currency_time,
            R.id.tv_realm_currency_time_title,

            R.id.tv_trailblaze_power,
            R.id.tv_trailblaze_power_title,
            R.id.tv_trailblaze_power_time,
            R.id.tv_trailblaze_power_time_title,
            R.id.tv_reserve_trailblaze_power,
            R.id.tv_reserve_trailblaze_power_title,
            R.id.tv_daily_training,
            R.id.tv_daily_training_title,
            R.id.tv_echo_of_war,
            R.id.tv_echo_of_war_title,
            R.id.tv_simulated_universe,
            R.id.tv_simulated_universe_title,
            R.id.tv_simulated_universe_cleared,
            R.id.tv_simulated_universe_title_cleared,
            R.id.tv_synchronicity_point,
            R.id.tv_synchronicity_point_title,
            R.id.tv_assignment_time,
            R.id.tv_assignment_title,

            R.id.tv_battery,
            R.id.tv_battery_title,
            R.id.tv_battery_time,
            R.id.tv_battery_time_title,
            R.id.tv_engagement_today,
            R.id.tv_engagement_today_title,
            R.id.tv_ridu_weekly,
            R.id.tv_ridu_weekly_title,
            R.id.tv_investigation_point,
            R.id.tv_investigation_point_title,
            R.id.tv_scratch_card,
            R.id.tv_scratch_card_title,
            R.id.tv_video_store_management,
            R.id.tv_video_store_management_title,
        )

        fun setFontColorAndSize(view: RemoteViews, id: Int, color: Int, size: Float) {
            view.setTextColor(id, color)
            view.setFloat(id, "setTextSize", size)
        }

        val fontSize = widgetDesign.fontSize.toFloat()

        mainFontViews.forEach { id ->
            setFontColorAndSize(view, id, mainFontColor, fontSize)
        }
    }
}