package danggai.data.changedataswitch.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import danggai.data.changedataswitch.remote.api.ChangeDataSwitchApi
import danggai.data.changedataswitch.repository.ChangeDataSwitchRepositoryImpl
import danggai.data.module.NetworkModule
import danggai.domain.changedataswitch.repository.ChangeDataSwitchRepository
import danggai.domain.changedataswitch.usecase.ChangeDataSwitchUseCase
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class ChangeDataSwitchModule {
    @Singleton
    @Provides
    fun provideChangeDataSwtichApi(retrofit: Retrofit): ChangeDataSwitchApi {
        return retrofit.create(ChangeDataSwitchApi::class.java)
    }

    @Singleton
    @Provides
    fun provideChangeDataSwitchRepository(
        repositoryImpl: ChangeDataSwitchRepositoryImpl
    ): ChangeDataSwitchRepository = repositoryImpl

    @Provides
    fun provideChangeDataSwitchUseCase(
        repository: ChangeDataSwitchRepository
    ): ChangeDataSwitchUseCase {
        return ChangeDataSwitchUseCase(repository)
    }
}