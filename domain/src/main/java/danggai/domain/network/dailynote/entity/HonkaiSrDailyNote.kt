package danggai.domain.network.dailynote.entity

import com.google.gson.annotations.SerializedName


data class HonkaiSrDailyNote(
    val data: HonkaiSrDailyNoteData,
    val message: String,
    val retcode: Int
) {
    companion object {
        val EMPTY = HonkaiSrDailyNote(
            retcode = 0,
            message = "",
            data = HonkaiSrDailyNoteData.EMPTY
        )
    }
}

data class HonkaiSrDailyNoteData(
    @SerializedName("accepted_expedition_num") val acceptedExpeditionNum: Int,  // 탐사 파견
    @SerializedName("total_expedition_num") val totalExpeditionNum: Int,
    @SerializedName("expeditions") val expeditions: List<HonkaiSrExpedition>,

    @SerializedName("current_stamina") val currentStamina: Int,               // 개척력
    @SerializedName("max_stamina") val maxStamina: Int,
    @SerializedName("stamina_recover_time") val staminaRecoverTime: String,

    @SerializedName("current_reserve_stamina") val currentReserveStamina: Int,  // 예비 개척력
    @SerializedName("is_reserve_stamina_full") val isReserveStaminaFull: Boolean,

    @SerializedName("current_train_score") val currentTrainScore: Int,         // 일퀘 점수
    @SerializedName("max_train_score") val maxTrainScore: Int,

    @SerializedName("current_rogue_score") val currentRogueScore: Int,         // 시뮬레이션 우주
    @SerializedName("max_rogue_score") val maxRogueScore: Int,

    @SerializedName("rogue_tourn_weekly_cur") val rogueTournWeeklyCur: Int,    // 차분화 우주
    @SerializedName("rogue_tourn_weekly_max") val rogueTournWeeklyMax: Int,
    @SerializedName("rogue_tourn_weekly_unlocked") val rogueTournWeeklyUnlocked: Boolean,

    @SerializedName("weekly_cocoon_cnt") val weeklyCocoonCnt: Int,             // 전쟁의 여운
    @SerializedName("weekly_cocoon_limit") val weeklyCocoonLimit: Int,
) {
    companion object {
        val EMPTY = HonkaiSrDailyNoteData(
            acceptedExpeditionNum = -1,
            totalExpeditionNum = -1,
            expeditions = listOf(),

            currentStamina = -1,
            maxStamina = -1,
            staminaRecoverTime = "-1",

            currentReserveStamina = -1,
            isReserveStaminaFull = false,

            currentTrainScore = -1,
            maxTrainScore = -1,

            currentRogueScore = -1,
            maxRogueScore = -1,

            rogueTournWeeklyCur = -1,
            rogueTournWeeklyMax = -1,
            rogueTournWeeklyUnlocked = false,

            weeklyCocoonCnt = -1,
            weeklyCocoonLimit = -1,
        )
    }
}

data class HonkaiSrExpedition(
    @SerializedName("avatars") val avatars: List<String>,       // 파견 아바타 초상화 url
    @SerializedName("name") val name: String,
    @SerializedName("remaining_time") val remainingTime: Int,
    @SerializedName("status") val status: String
)

data class HonkaiSrDataLocal(
    val dailyNote: HonkaiSrDailyNoteData,
    val rogueClearCount: Int,
    val isError: Boolean
) {
    companion object {
        val EMPTY = HonkaiSrDataLocal(
            dailyNote = HonkaiSrDailyNoteData.EMPTY,
            rogueClearCount = -1,
            isError = false
        )
    }
}