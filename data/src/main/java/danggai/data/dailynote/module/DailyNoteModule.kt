package danggai.data.dailynote.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import danggai.data.dailynote.remote.api.DailyNoteApi
import danggai.data.dailynote.repository.DailyNoteRepositoryImpl
import danggai.data.module.NetworkModule
import danggai.domain.dailynote.repository.DailyNoteRepository
import danggai.domain.dailynote.usecase.DailyNoteUseCase
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class DailyNoteModule {
    @Singleton
    @Provides
    fun provideDailyNoteApi(retrofit: Retrofit) : DailyNoteApi {
        return retrofit.create(DailyNoteApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDailyNoteRepository(
        repositoryImpl: DailyNoteRepositoryImpl
    ): DailyNoteRepository = repositoryImpl

    @Provides
    fun provideDailyNoteUseCase(
        repository: DailyNoteRepository
    ): DailyNoteUseCase {
        return DailyNoteUseCase(repository)
    }
}