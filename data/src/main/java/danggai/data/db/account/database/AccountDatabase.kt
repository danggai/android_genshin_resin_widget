package danggai.data.db.account.database

import androidx.room.Database
import androidx.room.RoomDatabase
import danggai.data.db.account.dao.AccountDao
import danggai.data.db.account.entity.AccountEntity

@Database(entities = [AccountEntity::class], version = 1, exportSchema = false)
abstract class AccountDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}