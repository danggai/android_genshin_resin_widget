package danggai.domain.getgamerecordcard.entity

data class GetGameRecordCard(
    val retcode: String,
    val message: String,
    val data: GameRecordCardList,
) {
    data class GameRecordCardList(
        val list: List<GameRecordCard>
    )

    data class GameRecordCard (
        val background_image: String,
        val data: List<InnerData>,
        val data_switches: List<DataSwitch>,
        val game_id: Int,
        val game_role_id: String,
        val h5_data_switches: List<Any>,
        val has_role: Boolean,
        val is_public: Boolean,
        val level: Int,
        val nickname: String,
        val region: String,
        val region_name: String,
        val url: String
    )

    data class DataSwitch(
        val is_public: Boolean,
        val switch_id: Int,
        val switch_name: String
    )

    data class InnerData(
        val name: String,
        val type: Int,
        val value: String
    )
}