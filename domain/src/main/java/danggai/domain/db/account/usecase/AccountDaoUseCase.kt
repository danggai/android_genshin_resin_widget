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

    fun selectAllAccountFlow(): Flow<List<Account>> =
        accountRepository.selectAllAccountFlow()

    fun selectAllAccount(): Flow<List<Account>> =
        accountRepository.selectAllAccount()

    fun deleteAccount(uid: String): Flow<Int> =
        accountRepository.deleteAccount(uid)

    fun deleteAllAccounts(): Flow<Int> =
        accountRepository.deleteAllAccounts()
}