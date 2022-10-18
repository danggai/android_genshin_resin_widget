package danggai.domain.db.account.repository

import danggai.domain.core.Repository
import danggai.domain.db.account.entity.Account
import kotlinx.coroutines.flow.Flow


interface AccountRepository: Repository {
    fun insertAccount(account: Account): Flow<Long>
    fun selectAccountByUid(uid: String): Flow<Account>
    fun getAllAccount(): Flow<List<Account>>
    fun deleteAccount(cookie: String): Flow<Int>
    fun deleteAllAccounts(): Flow<Int>
}