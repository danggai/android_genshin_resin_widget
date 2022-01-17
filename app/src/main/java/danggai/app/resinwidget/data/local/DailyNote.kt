package danggai.app.resinwidget.data.local

data class DailyNote(
    val current_resin: Int,
    val max_resin: Int,
    val resin_recovery_time: String?,

    val finished_task_num: Int, // 완료한 일일 임무 수
    val total_task_num: Int,    // 수행 가능한 일일 임무
    val is_extra_task_reward_received: Boolean, // 일일 임무 완료 보상

    val remain_resin_discount_num: Int,
    val resin_discount_num_limit: Int,          // 주간 보스 할인

    val current_home_coin: Int,
    val max_home_coin: Int,
    val home_coin_recovery_time: String?,

    val current_expedition_num: Int,
    val max_expedition_num: Int,
    val expeditions: List<GameRoleExpedition>?
) {
    data class GameRoleExpedition (
        val avatar_side_icon: String,
        val status: String,
        val remained_time: String
    )
}