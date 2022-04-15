package danggai.domain.network.getgamerecordcard.entity


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
) {
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

    companion object {
        val EMPTY = GameRecordCard(
            background_image = "",
            data = listOf(),
            data_switches = listOf(),
            game_id = -1,
            game_role_id = "",
            h5_data_switches = listOf(),
            has_role = false,
            is_public = false,
            level = -1,
            nickname = "",
            region = "",
            region_name = "",
            url = "",
        )
    }
}
