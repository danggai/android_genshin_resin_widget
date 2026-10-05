package danggai.app.presentation.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import danggai.app.presentation.R
import danggai.app.presentation.databinding.WidgetBatteryBinding
import danggai.app.presentation.databinding.WidgetResinFixedBinding
import danggai.app.presentation.databinding.WidgetTrailblazePowerBinding
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
        binding.ivRefresh.setColorFilter(subFontColor)
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
        binding.ivRefresh.setColorFilter(subFontColor)
        binding.tvSyncTime.setTextColor(subFontColor)
    }

    fun applyDetailWidgetColors(
        root: View,
        bgColor: Int,
        mainFontColor: Int,
        subFontColor: Int,
        wrappedDrawable: Drawable
    ) {
        val llRoot = root.findViewById<View>(R.id.ll_root)
        llRoot.setBackgroundColor(bgColor)

        // 스타레일 위젯에만 있는 에러 아이콘
        val ivError = root.findViewById<ImageView>(R.id.iv_error)
        if (ivError != null) {
            ivError.setColorFilter(ContextCompat.getColor(root.context, R.color.red))
        }

        root.findViewById<ImageView>(R.id.iv_refresh).setColorFilter(subFontColor)
        root.findViewById<TextView>(R.id.tv_sync_time).setTextColor(subFontColor)
        root.findViewById<TextView>(R.id.tv_disable).setTextColor(subFontColor)

        val textIds = CommonFunction.getDetailWidgetTextIds(root)
        CommonFunction.setTextColorByIds(root, textIds, mainFontColor)

        llRoot.background = wrappedDrawable
    }

    fun setDetailWidgetFontSize(root: View, fontSize: Int) {
        val textIds = CommonFunction.getDetailWidgetTextIds(root)
        CommonFunction.setTextSizeByIds(root, textIds, fontSize.toFloat())
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
        view.setInt(R.id.iv_refresh, "setColorFilter", subFontColor)
        view.setTextColor(R.id.tv_sync_time, subFontColor)

        val fontSize = widgetDesign.fontSize.toFloat()

        // RemoteViews 는 자식 뷰를 탐색할 수 없어서, 같은 레이아웃을 inflate 해 TextView id 를 찾는다.
        val layout = LayoutInflater.from(context).inflate(view.layoutId, null)
        val rowTextIds = CommonFunction.getDetailWidgetTextIds(layout)

        val textIds = rowTextIds + listOf(R.id.tv_disable, R.id.tv_no_selected_characters)

        for (id in textIds) {
            view.setTextColor(id, mainFontColor)
            view.setFloat(id, "setTextSize", fontSize)
        }
    }
}