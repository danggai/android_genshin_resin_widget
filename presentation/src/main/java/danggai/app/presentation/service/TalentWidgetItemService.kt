package danggai.app.presentation.service

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import danggai.app.presentation.R
import danggai.app.presentation.util.CommonFunction
import danggai.app.presentation.util.PlayableCharacters
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.domain.local.Elements
import danggai.domain.local.TalentArea
import danggai.domain.local.TalentDate
import danggai.domain.local.TalentDays
import danggai.domain.network.githubRaw.entity.RecentGenshinCharacters
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TalentWidgetItemService() : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0) ?: 0
        return TalentWidgetItemFactory(this.applicationContext, appWidgetId)
    }
}

data class CharacterItem(
    val id: Int,
    val name_ko: String,                    // 이름
    val name_en: String,                    // 이름
    val rarity: Int,                        // 4~5성
    val element: Elements,
    val talentArea: TalentArea,
    val talentDay: TalentDate,
    val icon: Bitmap,                          // 프로필 사진 res ID
)

class TalentWidgetItemFactory(
    private val context: Context,
    private val appWidgetId: Int
) : RemoteViewsService.RemoteViewsFactory {

    private var data = arrayListOf<CharacterItem>()
    override fun onCreate() {}

    override fun onDataSetChanged() {
        log.e()
        setData()
    }

    private fun setData() {
        val paramType = PreferenceManager.getString(
            context,
            Constant.PREF_TELENT_WIDGET_TYPE + "_$appWidgetId"
        )

        log.e("paramType : $paramType")
        when (paramType) {
            Constant.PREF_TALENT_RECENT_CHARACTERS -> {
                val recentCharacters = PreferenceManager.getT<RecentGenshinCharacters>(
                    context,
                    Constant.PREF_RECENT_CHARACTER_LIST
                )?.characters ?: listOf()

                val filteredList =
                    recentCharacters.filter { isTalentAvailableToday(it.talentDay) }

                CoroutineScope(Dispatchers.IO).launch {
                    var count = 0
                    val result = CoroutineScope(Dispatchers.IO).async {
                        filteredList
                            .map { item ->
                                async {
                                    CharacterItem(
                                        count++,
                                        item.name_ko,
                                        item.name_en,
                                        item.rarity,
                                        item.element,
                                        item.talentArea,
                                        item.talentDay,
                                        getImage(context, item.icon)
                                            ?: BitmapFactory.decodeResource(
                                                context.resources,
                                                R.drawable.icon_unknown
                                            )
                                    )
                                }
                            }.awaitAll()
                    }

                    data = result.await() as ArrayList<CharacterItem>
                }
            }

            else -> {
                val selectedCharacterIds =
                    PreferenceManager.getIntArray(context, Constant.PREF_SELECTED_CHARACTER_ID_LIST)

                data = PlayableCharacters.filter {
                    selectedCharacterIds.contains(it.id) && isTalentAvailableToday(it.talentDay)
                }.map { item ->
                    CharacterItem(
                        item.id,
                        item.name_ko,
                        item.name_en,
                        item.rarity,
                        item.element,
                        item.talentArea,
                        item.talentDay,
                        BitmapFactory.decodeResource(
                            context.resources,
                            item.icon
                        )
                    )
                } as ArrayList<CharacterItem>
            }
        }

        data.apply {
            sortBy { -it.id }
            sortBy { -it.rarity }
        }
    }

    override fun onDestroy() {}

    override fun getCount() = data.size

    override fun getViewAt(position: Int): RemoteViews {
        return if (position >= count) {
            loadingView
        } else {
            createCharacterWidgetItem(context, data, position)
        }
    }

    private fun createCharacterWidgetItem(
        context: Context,
        data: List<CharacterItem>,
        position: Int
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.item_character_widget).apply {
            // 배경
            setViewVisibility(R.id.iv_background, View.VISIBLE)
            setImageViewResource(
                R.id.iv_background,
                getBackgroundResourceByRarity(data[position].rarity)
            )

            // 캐릭터 아이콘
            setViewVisibility(R.id.iv_icon, View.VISIBLE)
            setImageViewBitmap(R.id.iv_icon, data[position].icon)

            // 지역 엠블렘
            setViewVisibility(R.id.iv_area_emblem, View.VISIBLE)
            setImageViewResource(
                R.id.iv_area_emblem,
                getEmblemResource(data[position].talentArea)
            )
        }
    }

    private suspend fun getImage(context: Context, imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                Glide.with(context)
                    .asBitmap()
                    .load(Constant.NEW_CHARA_GENSHIN_BASE_URL + imageUrl)
                    .submit()
                    .get()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun getBackgroundResourceByRarity(rarity: Int): Int {
        return if (rarity == 5) {
            R.drawable.bg_character_5stars
        } else {
            R.drawable.bg_character_4stars
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