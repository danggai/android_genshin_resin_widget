package danggai.data.db.account.respository

import danggai.data.db.account.dao.AccountDao
import danggai.data.db.account.mapper.mapToAccount
import danggai.data.db.account.mapper.mapToAccountEntity
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
    override fun insertAccount(account: Account) = flow<Long> {
        val accountEntity = mapToAccountEntity(account)

        val result = accountDao.insertAccount(accountEntity)

        emit(result)
    }.flowOn(ioDispatcher)

    override fun selectAccountByUid(uid: String) = flow<Account> {
        val accountEntity = accountDao.selectAccountByUid(uid)

        emit(mapToAccount(accountEntity))
    }

    override fun getAllAccount() = flow<List<Account>> {
        accountDao.getAllAccounts().collect { list ->
            emit(list.toList().map { accountEntity ->
                mapToAccount(accountEntity)
            })
        }
    }.flowOn(ioDispatcher)

    override fun deleteAccount(cookie: String) = flow<Int> {
        val result = accountDao.deleteAccount(cookie)

        emit(result)
    }.flowOn(ioDispatcher)

    override fun deleteAllAccounts() = flow<Int> {
        val result = accountDao.deleteAllAccounts()

        emit(result)
    }.flowOn(ioDispatcher)
}