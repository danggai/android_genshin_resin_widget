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
    @SerializedName("transformer") val transformer: Transformer? = Transformer.EMPTY,

    @SerializedName("calendar_url") val calendarUrl: String = "",                           // 이벤트 일정(전적 페이지) 링크
    @SerializedName("daily_task") val dailyTask: GenshinDailyTask? = null,                  // 일일 의뢰 상세 (인게임 실시간 노트 항목)
    @SerializedName("archon_quest_progress") val archonQuestProgress: GenshinArchonQuestProgress? = null, // 마신 임무 진행도
    @SerializedName("week_active_progress") val weekActiveProgress: GenshinWeekActiveProgress? = null     // 주간 활약도 (전적 페이지 미표시)
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

data class GenshinDailyTask(
    @SerializedName("total_num") val totalNum: Int,                                         // 일일 의뢰 전체 수
    @SerializedName("finished_num") val finishedNum: Int,
    @SerializedName("is_extra_task_reward_received") val isExtraTaskRewardReceived: Boolean,
    @SerializedName("task_rewards") val taskRewards: List<GenshinTaskStatus> = listOf(),
    @SerializedName("attendance_rewards") val attendanceRewards: List<GenshinAttendanceReward> = listOf(),
    @SerializedName("attendance_visible") val attendanceVisible: Boolean = false,
    @SerializedName("stored_attendance") val storedAttendance: String = "0",               // 용도 미확인
    @SerializedName("stored_attendance_refresh_countdown") val storedAttendanceRefreshCountdown: Long = 0
)

data class GenshinTaskStatus(
    @SerializedName("status") val status: String
)

data class GenshinAttendanceReward(
    @SerializedName("status") val status: String,
    @SerializedName("progress") val progress: Int
)

data class GenshinArchonQuestProgress(
    @SerializedName("list") val list: List<GenshinArchonQuest> = listOf(),
    @SerializedName("is_open_archon_quest") val isOpenArchonQuest: Boolean,
    @SerializedName("is_finish_all_mainline") val isFinishAllMainline: Boolean,
    @SerializedName("is_finish_all_interchapter") val isFinishAllInterchapter: Boolean,
    @SerializedName("wiki_url") val wikiUrl: String = ""
)

data class GenshinArchonQuest(
    @SerializedName("status") val status: String,
    @SerializedName("chapter_num") val chapterNum: String,
    @SerializedName("chapter_title") val chapterTitle: String,
    @SerializedName("id") val id: Int,
    @SerializedName("chapter_type") val chapterType: Int
)

data class GenshinWeekActiveProgress(
    @SerializedName("progress_current") val progressCurrent: Int,
    @SerializedName("progress_total") val progressTotal: Int,
    @SerializedName("period_progress_current") val periodProgressCurrent: Int,
    @SerializedName("period_progress_total") val periodProgressTotal: Int,
    @SerializedName("unlock") val unlock: Boolean,
    @SerializedName("is_active_period") val isActivePeriod: Boolean,
    @SerializedName("current_weekday") val currentWeekday: Int
)

data class GenshinExpedition(
    @SerializedName("avatar_side_icon") val avatarSideIcon: String,
    @SerializedName("status") val status: String,
    @SerializedName("remained_time") val remainedTime: String
)