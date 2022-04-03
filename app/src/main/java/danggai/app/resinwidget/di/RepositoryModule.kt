package danggai.app.resinwidget.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import danggai.app.resinwidget.network.ApiClient
import danggai.app.resinwidget.repository.ChangeDataSwitchRepository
import danggai.app.resinwidget.repository.CheckInRepository
import danggai.app.resinwidget.repository.DailyNoteRepository
import danggai.app.resinwidget.repository.GetGameRecordCardRepository
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    @ViewModelScoped
    fun provideDailyNoteRepository(
        apiClient: ApiClient,
        ioDispatcher: CoroutineDispatcher
    ): DailyNoteRepository {
        return DailyNoteRepository(apiClient, ioDispatcher)
    }
    
    @Provides
    @ViewModelScoped
    fun provideChangeDataSwitchRepository(
        apiClient: ApiClient,
        ioDispatcher: CoroutineDispatcher
    ): ChangeDataSwitchRepository {
        return ChangeDataSwitchRepository(apiClient, ioDispatcher)
    }
    
    @Provides
    @ViewModelScoped
    fun provideCheckInRepository(
        apiClient: ApiClient,
        ioDispatcher: CoroutineDispatcher
    ): CheckInRepository {
        return CheckInRepository(apiClient, ioDispatcher)
    }
    
    @Provides
    @ViewModelScoped
    fun provideGetGameRecordCardRepository(
        apiClient: ApiClient,
        ioDispatcher: CoroutineDispatcher
    ): GetGameRecordCardRepository {
        return GetGameRecordCardRepository(apiClient, ioDispatcher)
    }
    
}