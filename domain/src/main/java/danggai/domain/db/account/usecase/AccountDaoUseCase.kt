package danggai.domain.db.account.usecase

import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountDaoUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend fun insertAccount(account: Account): Flow<Long> =
        accountRepository.insertAccount(account)

    suspend fun getAllAccount(): Flow<List<Account>> =
        accountRepository.getAllAccount()

    suspend fun deleteAccount(cookie: String): Flow<Int> =
        accountRepository.deleteAccount(cookie)

    suspend fun deleteAllAccounts(): Flow<Int> =
        accountRepository.deleteAllAccounts()
}