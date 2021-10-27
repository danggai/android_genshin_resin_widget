package danggai.app.resinwidget.data.local

data class DailyNote (
    val current_resin: Int,
    val max_resin: Int,
    val resin_recovery_time: String,
    val current_expedition_num: Int,
    val max_expedition_num: Int,
    val expeditions: List<GameRoleExpedition>) {

    data class GameRoleExpedition (
        val avatar_side_icon: String,
        val status: String,
        val remained_time: String
        )
}