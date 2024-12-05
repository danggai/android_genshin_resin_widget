package danggai.app.presentation.service

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import danggai.app.presentation.R
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.PlayableCharacters
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.domain.local.LocalCharacter
import danggai.domain.local.TalentArea
import danggai.domain.local.TalentDate
import danggai.domain.local.TalentDays
import danggai.domain.util.Constant

class TalentWidgetItemService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TalentWidgetItemFactory(this.applicationContext)
    }
}

class TalentWidgetItemFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private var data = arrayListOf<LocalCharacter>()
    override fun onCreate() {}

    override fun onDataSetChanged() {
        log.e()
        setData()
    }

    private fun setData() {
        val selectedCharacterIds =
            PreferenceManager.getIntArray(context, Constant.PREF_SELECTED_CHARACTER_ID_LIST)

        data = PlayableCharacters.filter {
            selectedCharacterIds.contains(it.id) && isTalentAvailableToday(it.talentDay)
        } as ArrayList<LocalCharacter>

        data.apply {
            sortBy { -it.id }
            sortBy { -it.rarity }
        }
    }

    override fun onDestroy() {}

    override fun getCount() = data.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= count) {
            return loadingView
        } else {
            val widgetItem =
                RemoteViews(context.packageName, R.layout.item_character_widget).apply {
                    setViewVisibility(R.id.iv_background, View.VISIBLE)
                    setImageViewResource(
                        R.id.iv_background,
                        when (data[position].rarity) {
                            5 -> R.drawable.bg_character_5stars
                            else -> R.drawable.bg_character_4stars
                        }
                    )

                    setViewVisibility(R.id.iv_icon, View.VISIBLE)
                    setImageViewResource(R.id.iv_icon, data[position].icon)

                    setViewVisibility(R.id.iv_area_emblem, View.VISIBLE)
                    setImageViewResource(
                        R.id.iv_area_emblem,
                        getEmblemResource(data[position].talentArea)
                    )
                }
            return widgetItem
        }
    }

    private fun getEmblemResource(talentArea: TalentArea): Int {
        return when (talentArea) {
            TalentArea.MONDSTADT -> R.drawable.icon_emblem_mondstadt
            TalentArea.LIYUE -> R.drawable.icon_emblem_liyue
            TalentArea.INAZUMA -> R.drawable.icon_emblem_inazuma
            TalentArea.SUMERU -> R.drawable.icon_emblem_sumeru
            TalentArea.FONTAINE -> R.drawable.icon_emblem_fontaine
            TalentArea.NATLAN -> R.drawable.icon_emblem_natlan
        }
    }

    private fun isTalentAvailableToday(talentDate: TalentDate): Boolean {
        val currentDate = CommonFunction.getDateInGenshin()
        return when (talentDate) {
            TalentDate.MON_THU -> currentDate in TalentDays.MON_THU
            TalentDate.TUE_FRI -> currentDate in TalentDays.TUE_FRI
            TalentDate.WED_SAT -> currentDate in TalentDays.WED_SAT
            TalentDate.ALL -> true
        }
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.item_character_widget).apply {
            setImageViewResource(R.id.iv_icon, R.drawable.icon_unknown)
            setViewVisibility(R.id.iv_background, View.GONE)
            setViewVisibility(R.id.iv_icon, View.GONE)
            setViewVisibility(R.id.iv_area_emblem, View.GONE)
        }
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return try {
            data[position].id.toLong()
        } catch (e: Exception) {
            position.toLong()
        }
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}