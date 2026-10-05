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
    @SerializedName("accepted_epedition_num") val acceptedExpeditionNum: Int,   // 탐사 파견 (서버 키 오타 그대로)
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

    @SerializedName("grid_fight_weekly_cur") val gridFightWeeklyCur: Int,    // 화폐 전쟁
    @SerializedName("grid_fight_weekly_max") val gridFightWeeklyMax: Int,

    @SerializedName("weekly_cocoon_cnt") val weeklyCocoonCnt: Int,             // 전쟁의 여운
    @SerializedName("weekly_cocoon_limit") val weeklyCocoonLimit: Int,

    @SerializedName("stamina_full_ts") val staminaFullTs: Long = 0,            // 개척력 회복 완료 시각 (전적 페이지 "실시간 메모" 300/300)
    @SerializedName("current_ts") val currentTs: Long = 0,                     // 서버 현재 시각
    @SerializedName("rogue_tourn_exp_is_full") val rogueTournExpIsFull: Boolean = false,  // 차분화 우주 경험치 가득 참
    @SerializedName("period_score") val periodScore: Int = 0,                  // 주기 점수 (실시간 메모 "주기 점수 0/18000")
    @SerializedName("period_max_score") val periodMaxScore: Int = 0,
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

            gridFightWeeklyCur = -1,
            gridFightWeeklyMax = -1,

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