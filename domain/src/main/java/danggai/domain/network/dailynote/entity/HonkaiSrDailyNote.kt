package danggai.domain.network.dailynote.entity

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
    val accepted_epedition_num: Int,
    val current_stamina: Int,
    val expeditions: List<HonkaiSrExpedition>,
    val max_stamina: Int,
    val stamina_recover_time: String,
    val total_expedition_num: Int,
    val current_train_score: Int,
    val max_train_score: Int,
    val current_rogue_score: Int,
    val max_rogue_score: Int,
    val weekly_cocoon_cnt: Int,
    val weekly_cocoon_limit: Int,
){
    companion object {
        val EMPTY = HonkaiSrDailyNoteData(
            accepted_epedition_num = -1,
            current_stamina = -1,
            expeditions = listOf(),
            max_stamina = -1,
            stamina_recover_time = "-1",
            total_expedition_num = -1,
            current_train_score = -1,
            max_train_score = -1,
            current_rogue_score = -1,
            max_rogue_score = -1,
            weekly_cocoon_cnt = -1,
            weekly_cocoon_limit = -1,
        )
    }
}

data class HonkaiSrExpedition(
    val avatars: List<String>,       // 파견 아바타 초상화 url
    val name: String,
    val remaining_time: Int,
    val status: String
)