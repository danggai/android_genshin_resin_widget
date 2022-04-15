package danggai.domain.network.dailynote.entity

data class DailyNote (
    val retcode: String,
    val message: String,
    val data: Data?
) {
    data class Data(
        val current_resin: Int,
        val max_resin: Int,
        val resin_recovery_time: String?,

        val finished_task_num: Int, // 완료한 일일 임무 수
        val total_task_num: Int,    // 수행 가능한 일일 임무
        val is_extra_task_reward_received: Boolean, // 일일 임무 완료 보상

        val remain_resin_discount_num: Int,
        val resin_discount_num_limit: Int,          // 주간 보스 할인

        val current_home_coin: Int?,
        val max_home_coin: Int?,
        val home_coin_recovery_time: String?,

        val current_expedition_num: Int,
        val max_expedition_num: Int,
        val expeditions: List<GameRoleExpedition>?
    )

    companion object {
        val EMPTY = DailyNote(
            retcode = "",
            message = "",
            Data(
                current_resin = -1,
                max_resin = -1,
                resin_recovery_time = "-1",
                finished_task_num = -1,
                total_task_num = -1,
                is_extra_task_reward_received = false,
                remain_resin_discount_num = -1,
                resin_discount_num_limit = -1,
                current_home_coin = -1,
                max_home_coin = -1,
                home_coin_recovery_time = "-1",
                current_expedition_num = -1,
                max_expedition_num = -1,
                expeditions = listOf()
            )
        )
    }
}
