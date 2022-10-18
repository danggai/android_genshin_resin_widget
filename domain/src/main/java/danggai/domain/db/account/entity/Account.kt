package danggai.domain.db.account.entity

data class Account (
    val nickname: String,
    val cookie: String,
    val genshin_uid: String,
    val server: Int,
    val enable_genshin_checkin: Boolean,    // 원신
    val enable_honkai3rd_checkin: Boolean,  // 붕괴3rd
    val enable_tot_checkin: Boolean,        // 미해결사건부
) {
    companion object {
        val EMPTY = Account(
            nickname = "",
            cookie = "",
            genshin_uid = "",
            server = 0,
            enable_genshin_checkin = false,
            enable_honkai3rd_checkin = false,
            enable_tot_checkin = false
        )
    }
}