package danggai.data.db.account.mapper

import danggai.data.db.account.entity.AccountEntity
import danggai.domain.db.account.entity.Account

fun mapToAccount(account: AccountEntity): Account {
    return Account(
        account.accountNickName,
        account.cookie,
        account.genshinUid,
        account.server,
        account.honkaiSrNickName,
        account.honkaiSrUid,
        account.honkaiSrServer,
        account.zzzNickName,
        account.zzzUid,
        account.zzzServer,
        account.enableGenshinCheckin,
        account.enableHonkai3rdCheckin,
        account.enableHonkaiSRCheckin,
        account.enableZZZCheckin,
        account.enableTotCheckin
    )
}

fun mapToAccountEntity(account: Account): AccountEntity {
    return AccountEntity(
        account.nickname,
        account.cookie,
        account.genshin_uid,
        account.server,
        account.honkai_sr_nickname,
        account.honkai_sr_uid,
        account.honkai_sr_server,
        account.zzz_nickname,
        account.zzz_uid,
        account.zzz_server,
        account.enable_genshin_checkin,
        account.enable_honkai3rd_checkin,
        account.enable_honkai_sr_checkin,
        account.enable_zzz_checkin,
        account.enable_tot_checkin,
    )
}