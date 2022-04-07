package danggai.data.getgamerecordcard.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import danggai.data.checkin.repository.CheckInRepositoryImpl
import danggai.data.getgamerecordcard.remote.api.GetGameRecordCardApi
import danggai.data.getgamerecordcard.repository.GetGameRecordCardRepositoryImpl
import danggai.data.module.NetworkModule
import danggai.domain.getgamerecordcard.repository.GetGameRecordCardRepository
import danggai.domain.getgamerecordcard.usecase.GetGameRecordCardUseCase
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class GetGameRecordCardModule {
    @Singleton
    @Provides
    fun provideChangeDataSwtichApi(retrofit: Retrofit) : GetGameRecordCardApi {
        return retrofit.create(GetGameRecordCardApi::class.java)
    }

    @Singleton
    @Provides
    fun provideGetGameRecordCardRepository(
        repositoryImpl: GetGameRecordCardRepositoryImpl
    ): GetGameRecordCardRepository = repositoryImpl


    @Provides
    fun provideGetGameRecordCardUseCase(
        repository: GetGameRecordCardRepository
    ): GetGameRecordCardUseCase {
        return GetGameRecordCardUseCase(repository)
    }
}