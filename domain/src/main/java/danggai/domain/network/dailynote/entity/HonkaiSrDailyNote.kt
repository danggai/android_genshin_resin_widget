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
    val accepted_epedition_num: Int,    // 탐사 파견
    val total_expedition_num: Int,
    val expeditions: List<HonkaiSrExpedition>,

    val current_stamina: Int,           // 개척력
    val max_stamina: Int,
    val stamina_recover_time: String,

    val current_reserve_stamina: Int,    // 예비 개척력
    val is_reserve_stamina_full: Boolean,

    val current_train_score: Int,       // 일퀘 점수
    val max_train_score: Int,

    val current_rogue_score: Int,       // 시뮬레이션 우주
    val max_rogue_score: Int,

    val rogue_tourn_weekly_cur: Int,    // 차분화 우주
    val rogue_tourn_weekly_max: Int,
    val rogue_tourn_weekly_unlocked: Boolean,

    val weekly_cocoon_cnt: Int,         // 전쟁의 여운
    val weekly_cocoon_limit: Int,

){
    companion object {
        val EMPTY = HonkaiSrDailyNoteData(
            accepted_epedition_num = -1,
            total_expedition_num = -1,
            expeditions = listOf(),

            current_stamina = -1,
            max_stamina = -1,
            stamina_recover_time = "-1",

            current_reserve_stamina  = -1,
            is_reserve_stamina_full = false,

            current_train_score = -1,
            max_train_score = -1,

            current_rogue_score = -1,
            max_rogue_score = -1,

            rogue_tourn_weekly_cur = -1,
            rogue_tourn_weekly_max = -1,
            rogue_tourn_weekly_unlocked = false,

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

data class HonkaiSrDataLocal(
    val accepted_epedition_num: Int,    // 탐사 파견
    val total_expedition_num: Int,
    val expeditions: List<HonkaiSrExpedition>,

    val current_stamina: Int,           // 개척력
    val max_stamina: Int,
    val stamina_recover_time: String,

    val current_reserve_stamina: Int,    // 예비 개척력
    val is_reserve_stamina_full: Boolean,

    val current_train_score: Int,       // 일퀘 점수
    val max_train_score: Int,

    val current_rogue_score: Int,       // 시뮬레이션 우주
    val max_rogue_score: Int,
    val rogue_clear_count: Int,

    val rogue_tourn_weekly_cur: Int,    // 차분화 우주
    val rogue_tourn_weekly_max: Int,
    val rogue_tourn_weekly_unlocked: Boolean,

    val weekly_cocoon_cnt: Int,         // 전쟁의 여운
    val weekly_cocoon_limit: Int,

){
    companion object {
        val EMPTY = HonkaiSrDataLocal(
            accepted_epedition_num = -1,
            total_expedition_num = -1,
            expeditions = listOf(),

            current_stamina = -1,
            max_stamina = -1,
            stamina_recover_time = "-1",

            current_reserve_stamina  = -1,
            is_reserve_stamina_full = false,

            current_train_score = -1,
            max_train_score = -1,

            current_rogue_score = -1,
            max_rogue_score = -1,
            rogue_clear_count = -1,

            rogue_tourn_weekly_cur = -1,
            rogue_tourn_weekly_max = -1,
            rogue_tourn_weekly_unlocked = false,

            weekly_cocoon_cnt = -1,
            weekly_cocoon_limit = -1,
        )
    }
}