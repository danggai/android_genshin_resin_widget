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
        widget.llRoot.setBackgroundColor(bgColor)

        widget.ivRefersh.setColorFilter(subFontColor)
        widget.tvSyncTime.setTextColor(subFontColor)
        widget.tvDisable.setTextColor(subFontColor)

        CommonFunction.setTextColorByIds(
            widget.root,
            CommonFunction.DETAIL_WIDGET_TEXT_IDS,
            mainFontColor
        )

        widget.llRoot.background = wrappedDrawable
    }

    fun setDetailWidgetFontSize(widget: WidgetDetailFixedBinding, fontSize: Int) {
        CommonFunction.setTextSizeByIds(
            widget.root,
            CommonFunction.DETAIL_WIDGET_TEXT_IDS,
            fontSize.toFloat()
        )
    }

    fun applyDetailWidgetColors(
        widget: WidgetHksrDetailFixedBinding,
        bgColor: Int,
        mainFontColor: Int,
        subFontColor: Int,
        wrappedDrawable: Drawable
    ) {
        widget.llRoot.setBackgroundColor(bgColor)

        widget.ivError.setColorFilter(ContextCompat.getColor(widget.root.context, R.color.red))

        widget.ivRefresh.setColorFilter(subFontColor)
        widget.tvSyncTime.setTextColor(subFontColor)
        widget.tvDisable.setTextColor(subFontColor)

        CommonFunction.setTextColorByIds(
            widget.root,
            CommonFunction.DETAIL_WIDGET_TEXT_IDS,
            mainFontColor
        )

        widget.llRoot.background = wrappedDrawable
    }

    fun setDetailWidgetFontSize(widget: WidgetHksrDetailFixedBinding, fontSize: Int) {
        CommonFunction.setTextSizeByIds(
            widget.root,
            CommonFunction.DETAIL_WIDGET_TEXT_IDS,
            fontSize.toFloat()
        )
    }

    fun applyDetailWidgetColors(
        widget: WidgetZzzDetailBinding,
        bgColor: Int,
        mainFontColor: Int,
        subFontColor: Int,
        wrappedDrawable: Drawable
    ) {
        widget.llRoot.setBackgroundColor(bgColor)

        widget.ivRefersh.setColorFilter(subFontColor)
        widget.tvSyncTime.setTextColor(subFontColor)
        widget.tvDisable.setTextColor(subFontColor)

        CommonFunction.setTextColorByIds(
            widget.root,
            CommonFunction.DETAIL_WIDGET_TEXT_IDS,
            mainFontColor
        )

        widget.llRoot.background = wrappedDrawable
    }

    fun setDetailWidgetFontSize(widget: WidgetZzzDetailBinding, fontSize: Int) {
        CommonFunction.setTextSizeByIds(
            widget.root,
            CommonFunction.DETAIL_WIDGET_TEXT_IDS,
            fontSize.toFloat()
        )
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
                view.setFloat(R.id.tv_battery, "setTextSize", fontSize.toFloat())
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

        val fontSize = widgetDesign.fontSize.toFloat()

        // 행 TextView 는 미리보기와 같은 공용 목록을 사용. 안내 문구 2개는 실제 위젯에만 있어 따로 처리.
        val textIds = CommonFunction.DETAIL_WIDGET_TEXT_IDS +
                listOf(R.id.tv_disable, R.id.tv_no_selected_characters)

        for (id in textIds) {
            view.setTextColor(id, mainFontColor)
            view.setFloat(id, "setTextSize", fontSize)
        }
    }
}