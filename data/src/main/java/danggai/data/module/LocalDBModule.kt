package danggai.data.module;


import android.content.Context
import androidx.room.Room
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
