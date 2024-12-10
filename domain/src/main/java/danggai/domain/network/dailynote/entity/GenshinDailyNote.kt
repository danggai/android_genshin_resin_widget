package danggai.domain.network.dailynote.entity

import com.google.gson.annotations.SerializedName

data class GenshinDailyNote(
    val retcode: String,
    val message: String,
    val data: GenshinDailyNoteData?
) {
    companion object {
        val EMPTY = GenshinDailyNote(
            retcode = "",
            message = "",
            GenshinDailyNoteData.EMPTY
        )
    }
}

data class GenshinDailyNoteData(
    @SerializedName("current_resin") val currentResin: Int,
    @SerializedName("max_resin") val maxResin: Int,
    @SerializedName("resin_recovery_time") val resinRecoveryTime: String = "-1",

    @SerializedName("finished_task_num") val finishedTaskNum: Int,                          // 완료한 일일 임무 수
    @SerializedName("total_task_num") val totalTaskNum: Int,                                // 수행 가능한 일일 임무
    @SerializedName("is_extra_task_reward_received") val isExtraTaskRewardReceived: Boolean,// 일일 임무 완료 보상

    @SerializedName("remain_resin_discount_num") val remainResinDiscountNum: Int,
    @SerializedName("resin_discount_num_limit") val resinDiscountNumLimit: Int,             // 주간 보스 할인 최대치

    @SerializedName("current_home_coin") val currentHomeCoin: Int = -1,
    @SerializedName("max_home_coin") val maxHomeCoin: Int = -1,
    @SerializedName("home_coin_recovery_time") val homeCoinRecoveryTime: String = "-1",

    @SerializedName("current_expedition_num") val currentExpeditionNum: Int,
    @SerializedName("max_expedition_num") val maxExpeditionNum: Int,
    @SerializedName("expeditions") val expeditions: List<GenshinExpedition> = listOf(),
    @SerializedName("transformer") val transformer: Transformer? = Transformer.EMPTY
) {
    companion object {
        val EMPTY = GenshinDailyNoteData(
            currentResin = -1,
            maxResin = -1,
            resinRecoveryTime = "-1",
            finishedTaskNum = -1,
            totalTaskNum = -1,
            isExtraTaskRewardReceived = false,
            remainResinDiscountNum = -1,
            resinDiscountNumLimit = -1,
            currentHomeCoin = -1,
            maxHomeCoin = -1,
            homeCoinRecoveryTime = "-1",
            currentExpeditionNum = -1,
            maxExpeditionNum = -1,
            expeditions = listOf(),
            transformer = Transformer.EMPTY
        )
    }
}

data class GenshinExpedition(
    @SerializedName("avatar_side_icon") val avatarSideIcon: String,
    @SerializedName("status") val status: String,
    @SerializedName("remained_time") val remainedTime: String
)