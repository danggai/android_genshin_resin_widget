package danggai.data.db.account.respository

import danggai.data.db.account.dao.AccountDao
import danggai.data.db.account.entity.AccountEntity
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.repository.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val ioDispatcher: CoroutineDispatcher
): AccountRepository {
    override suspend fun insertAccount(account: Account) = flow<Long> {
        val entity = AccountEntity(
            account.cookie,
            account.genshin_uid,
            account.enable_genshin_checkin,
            account.enable_honkai3rd_checkin,
            account.enable_tot_checkin,
        )

        val result = accountDao.insertAccount(entity)

        emit(result)
    }.flowOn(ioDispatcher)

    override suspend fun getAllAccount() = flow<List<Account>> {
        val result = accountDao.getAllAccounts()

        emit(result.toList().map {
            Account(
                it.cookie,
                it.genshinUid,
                it.enableGenshinCheckin,
                it.enableHonkai3rdCheckin,
                it.enableTotCheckin
            )
        })
    }.flowOn(ioDispatcher)

    override suspend fun deleteAccount(cookie: String) = flow<Int> {
        val result = accountDao.deleteAccount(cookie)

        emit(result)
    }.flowOn(ioDispatcher)

    override suspend fun deleteAllAccounts() = flow<Int> {
        val result = accountDao.deleteAllAccounts()

        emit(result)
    }.flowOn(ioDispatcher)
}