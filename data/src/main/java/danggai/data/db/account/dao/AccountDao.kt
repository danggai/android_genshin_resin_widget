package danggai.data.db.account.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import danggai.data.db.account.entity.AccountEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface AccountDao {
    @Insert(onConflict = REPLACE)
    fun insertAccount(account: AccountEntity): Long

    @Query("SELECT * FROM account")
    fun getAllAccounts(): List<AccountEntity>

    @Query("DELETE FROM account WHERE cookie = :cookie")
    fun deleteAccount(cookie: String): Int

    @Query("DELETE FROM account")
    fun deleteAllAccounts(): Int
}