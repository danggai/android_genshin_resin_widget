package danggai.data.db.account.respository

import danggai.data.db.account.dao.AccountDao
import danggai.data.db.account.mapper.mapToAccount
import danggai.data.db.account.mapper.mapToAccountEntity
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.repository.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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

    override fun selectAllAccountFlow() = flow<List<Account>> {
        accountDao.selectAllAccountFlow().collect { list ->
            emit(list.toList().map { accountEntity ->
                mapToAccount(accountEntity)
            })
        }
    }.flowOn(ioDispatcher)

    override fun selectAllAccount() = flow<List<Account>> {
        val result = accountDao.selectAllAccount()

        emit(result.toList().map { accountEntity ->
            mapToAccount(accountEntity)
        })
    }.flowOn(ioDispatcher)

    override fun deleteAccount(uid: String) = flow<Int> {
        val result = accountDao.deleteAccount(uid)

        emit(result)
    }.flowOn(ioDispatcher)

    override fun deleteAllAccounts() = flow<Int> {
        val result = accountDao.deleteAllAccounts()

        emit(result)
    }.flowOn(ioDispatcher)
}