package danggai.domain.network.getgamerecordcard.entity

data class GetGameRecordCard(
    val retcode: String,
    val message: String,
    val data: GameRecordCardList,
) {
    data class GameRecordCardList(
        val list: List<GameRecordCard>
    )

    companion object {
        val EMPTY = GetGameRecordCard(
            "",
            "",
            GameRecordCardList(listOf())
        )
    }
}