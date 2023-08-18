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
) {
    companion object {
        val EMPTY = HonkaiSrRogueData(CurrentRecord.EMPTY)
    }
}

data class CurrentRecord(
    val basic: Basic,
    val has_data: Boolean,
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
