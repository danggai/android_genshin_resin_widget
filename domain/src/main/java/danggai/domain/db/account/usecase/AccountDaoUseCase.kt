package danggai.domain.db.account.usecase

import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountDaoUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    fun insertAccount(account: Account): Flow<Long> =
        accountRepository.insertAccount(account)
    
    fun selectAccountByUid(uid: String): Flow<Account> =
        accountRepository.selectAccountByUid(uid)

    fun getAllAccount(): Flow<List<Account>> =
        accountRepository.getAllAccount()

    fun deleteAccount(cookie: String): Flow<Int> =
        accountRepository.deleteAccount(cookie)

    fun deleteAllAccounts(): Flow<Int> =
        accountRepository.deleteAllAccounts()
}