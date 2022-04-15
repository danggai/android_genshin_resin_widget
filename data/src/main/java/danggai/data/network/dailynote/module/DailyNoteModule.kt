package danggai.data.network.dailynote.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import danggai.data.module.NetworkModule
import danggai.data.network.dailynote.remote.api.DailyNoteApi
import danggai.data.network.dailynote.repository.DailyNoteRepositoryImpl
import danggai.domain.network.dailynote.repository.DailyNoteRepository
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
}