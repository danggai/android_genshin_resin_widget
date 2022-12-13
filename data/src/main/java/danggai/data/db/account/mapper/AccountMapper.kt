package danggai.data.db.account.mapper

import danggai.data.db.account.entity.AccountEntity
import danggai.domain.db.account.entity.Account

fun mapToAccount(account: AccountEntity): Account {
    return Account(
        account.accountNickName,
        account.cookie,
        account.genshinUid,
        account.server,
        account.enableGenshinCheckin,
        account.enableHonkai3rdCheckin,
        account.enableTotCheckin
    )
}

fun mapToAccountEntity(account: Account): AccountEntity {
    return AccountEntity(
        account.nickname,
        account.cookie,
        account.genshin_uid,
        account.server,
        account.enable_genshin_checkin,
        account.enable_honkai3rd_checkin,
        account.enable_tot_checkin,
    )
}