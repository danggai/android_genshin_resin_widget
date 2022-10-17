package danggai.domain.db.account.entity

data class Account (
    val cookie: String,
    val genshin_uid: String,
    val enable_genshin_checkin: Boolean,    // 원신
    val enable_honkai3rd_checkin: Boolean,  // 붕괴3rd
    val enable_tot_checkin: Boolean,        // 미해결사건부

) {
    companion object {
        val EMPTY = Account(
            cookie = "",
            genshin_uid = "",
            enable_genshin_checkin = false,
            enable_honkai3rd_checkin = false,
            enable_tot_checkin = false
        )
    }
}