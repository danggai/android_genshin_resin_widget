package danggai.domain.db.account.entity

data class Account(
    val nickname: String,
    val cookie: String,
    val genshin_uid: String,
    val server: Int,
    val honkai_sr_nickname: String,
    val honkai_sr_uid: String,
    val honkai_sr_server: Int,
    val zzz_nickname: String,
    val zzz_uid: String,
    val zzz_server: Int,
    val enable_genshin_checkin: Boolean,    // 원신
    val enable_honkai3rd_checkin: Boolean,  // 붕괴3rd
    val enable_honkai_sr_checkin: Boolean,  // 붕괴: 스타레일
    val enable_zzz_checkin: Boolean,                // 젠레스 존 제로
    val enable_tot_checkin: Boolean,        // 미해결사건부
) {
    companion object {
        val EMPTY = Account(
            nickname = "",
            cookie = "",
            genshin_uid = "",
            server = 0,
            honkai_sr_nickname = "",
            honkai_sr_uid = "-1",
            honkai_sr_server = 0,
            zzz_nickname = "",
            zzz_uid = "-1",
            zzz_server = 0,
            enable_genshin_checkin = false,
            enable_honkai3rd_checkin = false,
            enable_honkai_sr_checkin = false,
            enable_zzz_checkin = false,
            enable_tot_checkin = false
        )

        val GUEST = Account(
            nickname = "Guest",
            cookie = "",
            genshin_uid = "-1",
            server = -1,
            honkai_sr_nickname = "",
            honkai_sr_uid = "-1",
            honkai_sr_server = 0,
            zzz_nickname = "",
            zzz_uid = "-1",
            zzz_server = 0,
            enable_genshin_checkin = false,
            enable_honkai3rd_checkin = false,
            enable_honkai_sr_checkin = true,
            enable_zzz_checkin = true,
            enable_tot_checkin = false
        )
    }
}