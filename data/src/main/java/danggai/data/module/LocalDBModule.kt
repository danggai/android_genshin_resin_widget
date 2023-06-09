package danggai.data.module;


import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import danggai.data.db.account.dao.AccountDao
import danggai.data.db.account.database.AccountDatabase
import danggai.data.db.account.respository.AccountRepositoryImpl
import danggai.domain.db.account.repository.AccountRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDBModule {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): AccountDatabase {
                return Room.databaseBuilder(
                        context,
                        AccountDatabase::class.java, "account.db"
                )
                        .addMigrations(MIGRATION_1_2)
                        .addMigrations(MIGRATION_2_3)
                        .allowMainThreadQueries()
                        .build()
        }

        @Provides
        @Singleton
        fun provideAccountDao(accountDatabase: AccountDatabase): AccountDao {
                return accountDatabase.accountDao()
        }

        @Singleton
        @Provides
        fun provideAccountRepository(
                repositoryImpl: AccountRepositoryImpl
        ): AccountRepository = repositoryImpl
}



val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE account ADD enableHonkaiSRCheckin INT NOT NULL DEFAULT 0")
        }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE account ADD honkaiSrNickname TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE account ADD honkaiSrUid TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE account ADD honkaiSrServer INTEGER NOT NULL DEFAULT 0")
        }
}
