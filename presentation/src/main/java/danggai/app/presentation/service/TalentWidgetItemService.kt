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
        val selectedCharacterIds = PreferenceManager.getIntArray(context, Constant.PREF_SELECTED_CHARACTER_ID_LIST)

        data = PlayableCharacters
            .filter {
                selectedCharacterIds.contains(it.id) &&
                        when (it.talentDay) {
                            Constant.TALENT_DATE_MONTHU -> CommonFunction.getDateInGenshin() in listOf(1, 2, 5)
                            Constant.TALENT_DATE_TUEFRI -> CommonFunction.getDateInGenshin() in listOf(1, 3, 6)
                            Constant.TALENT_DATE_WEDSAT -> CommonFunction.getDateInGenshin() in listOf(1, 4, 7)
                            Constant.TALENT_DATE_ALL -> true
                            else -> false
                        }
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
            val widgetItem = RemoteViews(context.packageName, R.layout.item_character_widget).apply {
                setViewVisibility(R.id.iv_background, View.VISIBLE)
                setImageViewResource(R.id.iv_background,
                    when (data[position].rarity) {
                        5 -> R.drawable.bg_character_5stars
                        else -> R.drawable.bg_character_4stars
                    }
                )

                setViewVisibility(R.id.iv_icon, View.VISIBLE)
                setImageViewResource(R.id.iv_icon, data[position].icon)

                setViewVisibility(R.id.iv_area_emblem, View.VISIBLE)
                setImageViewResource(R.id.iv_area_emblem,
                    when (data[position].talentArea) {
                        Constant.TALENT_AREA_MONDSTADT -> R.drawable.icon_emblem_mondstadt
                        Constant.TALENT_AREA_LIYUE -> R.drawable.icon_emblem_liyue
                        Constant.TALENT_AREA_INAZUMA -> R.drawable.icon_emblem_inazuma
                        Constant.TALENT_AREA_SUMERU -> R.drawable.icon_emblem_sumeru
                        Constant.TALENT_AREA_FONTAINE -> R.drawable.icon_emblem_fontaine
                        else -> R.drawable.icon_emblem_mondstadt
                    }
                )
            }
            return widgetItem
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