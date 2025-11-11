package danggai.domain.network.dailynote.entity

import com.google.gson.annotations.SerializedName

data class HonkaiSrGrid(
    val data: HonkaiSrGridData,
    val message: String,
    val retcode: Int
) {
    companion object {
        val EMPTY = HonkaiSrGrid(
            retcode = 0,
            message = "",
            data = HonkaiSrGridData.EMPTY
        )
    }
}

data class HonkaiSrGridData(
    @SerializedName("grid_fight_brief")
    val gridFightBrief: GridFightBrief
) {
    companion object {
        val EMPTY = HonkaiSrGridData(GridFightBrief.EMPTY)
    }
}

data class GridFightBrief(
    @SerializedName("division")
    val division: Division,
    @SerializedName("season_level")
    val seasonLevel: String,
    @SerializedName("weekly_score_cur")
    val weeklyScoreCur: String,
    @SerializedName("weekly_score_max")
    val weeklyScoreMax: String,
    @SerializedName("unlocked")
    val unlocked: Boolean,
    @SerializedName("has_played")
    val hasPlayed: Boolean,
    @SerializedName("handbook_progress")
    val handbookProgress: String,
    @SerializedName("trait_progress")
    val traitProgress: String,
    @SerializedName("quest_cur")
    val questCur: String,
    @SerializedName("quest_max")
    val questMax: String
) {
    companion object {
        val EMPTY = GridFightBrief(
            division = Division.EMPTY,
            seasonLevel = "",
            weeklyScoreCur = "",
            weeklyScoreMax = "",
            unlocked = false,
            hasPlayed = false,
            handbookProgress = "",
            traitProgress = "",
            questCur = "",
            questMax = ""
        )
    }
}

data class Division(
    @SerializedName("icon")
    val icon: String,
    @SerializedName("level")
    val level: String,
    @SerializedName("name_with_num")
    val nameWithNum: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("is_promotion")
    val isPromotion: Boolean,
    @SerializedName("icon_with_bg")
    val iconWithBg: String
) {
    companion object {
        val EMPTY = Division(
            icon = "",
            level = "",
            nameWithNum = "",
            name = "",
            isPromotion = false,
            iconWithBg = ""
        )
    }
}