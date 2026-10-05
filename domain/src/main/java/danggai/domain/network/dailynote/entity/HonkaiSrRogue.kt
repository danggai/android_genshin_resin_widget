package danggai.domain.network.dailynote.entity

data class HonkaiSrRogue(
    val data: HonkaiSrRogueData,
    val message: String,
    val retcode: Int
) {
    companion object {
        val EMPTY = HonkaiSrRogue(
            retcode = 0,
            message = "",
            data = HonkaiSrRogueData.EMPTY
        )
    }
}

data class HonkaiSrRogueData(
    val current_record: CurrentRecord,
    val last_record: CurrentRecord? = null,         // 지난 시즌 기록
    val role: RogueRole? = null,                    // 전적 페이지 상단 프로필
    val basic_info: RogueBasicInfo? = null          // 시뮬레이션 우주 해금 수치
) {
    companion object {
        val EMPTY = HonkaiSrRogueData(CurrentRecord.EMPTY)
    }
}

data class RogueRole(
    val server: String,
    val nickname: String,                           // 개척자 이름
    val level: Int                                  // 트레일 레벨 (전적 페이지 "Lv.70")
)

data class RogueBasicInfo(
    val unlocked_buff_num: Int,                     // 해금한 축복
    val unlocked_miracle_num: Int,                  // 해금한 기적
    val unlocked_skill_points: Int                  // 해금한 스킬 포인트
)

data class CurrentRecord(
    val basic: Basic,
    val has_data: Boolean,
    val records: List<Any> = listOf(),              // 클리어 기록 목록
    val best_record: Any? = null                    // 최고 기록
) {
    companion object {
        val EMPTY = CurrentRecord(Basic.EMPTY, false)
    }
}

data class Basic(
    val current_rogue_score: Int,
    val finish_cnt: Int,
    val id: Int,
    val max_rogue_score: Int,
    val schedule_begin: Schedule,
    val schedule_end: Schedule
) {
    companion object {
        val EMPTY = Basic(0,0,0,0, Schedule.EMPTY, Schedule.EMPTY)
    }
}

data class Schedule(
    val day: Int,
    val hour: Int,
    val minute: Int,
    val month: Int,
    val second: Int,
    val year: Int
) {
    companion object {
        val EMPTY = Schedule(0,0,0,0,0,0)
    }
}
