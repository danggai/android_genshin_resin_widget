package danggai.app.resinwidget.data.res

import danggai.app.resinwidget.data.local.GameRecordCard

data class ResGetGameRecordCard (
    val meta: Meta,
    val data: Data
) {
    data class Data(
        val retcode: String,
        val message: String,
        val data: GameRecordCardList,
    )

    data class GameRecordCardList(
        val list: List<GameRecordCard>
    )
}