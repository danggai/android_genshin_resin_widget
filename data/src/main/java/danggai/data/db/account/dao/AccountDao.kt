package danggai.data.db.account.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import danggai.data.db.account.entity.AccountEntity
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert(onConflict = REPLACE)
    fun insertAccount(account: AccountEntity): Long

    @Query("SELECT * FROM account WHERE genshinUid = :uid")
    fun selectAccountByUid(uid: String): AccountEntity

    @Query("SELECT * FROM account")
    fun selectAllAccountFlow(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM account")
    fun selectAllAccount(): List<AccountEntity>

    @Query("DELETE FROM account WHERE genshinUid = :uid")
    fun deleteAccount(uid: String): Int

    @Query("DELETE FROM account")
    fun deleteAllAccounts(): Int
}