package danggai.domain.db.account.repository

import danggai.domain.core.Repository
import danggai.domain.db.account.entity.Account
import kotlinx.coroutines.flow.Flow


interface AccountRepository: Repository {
    suspend fun insertAccount(account: Account): Flow<Long>
    suspend fun getAllAccount(): Flow<List<Account>>
    suspend fun deleteAccount(cookie: String): Flow<Int>
    suspend fun deleteAllAccounts(): Flow<Int>
}