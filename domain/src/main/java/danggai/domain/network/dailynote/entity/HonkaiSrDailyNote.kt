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
    val total_expedition_num: Int
){
    companion object {
        val EMPTY = HonkaiSrDailyNoteData(
            accepted_epedition_num = -1,
            current_stamina = -1,
            expeditions = listOf(),
            max_stamina = -1,
            stamina_recover_time = "-1",
            total_expedition_num = -1
        )
    }
}

data class HonkaiSrExpedition(
    val avatars: List<String>,       // 파견 아바타 초상화 url
    val name: String,
    val remaining_time: Int,
    val status: String
)