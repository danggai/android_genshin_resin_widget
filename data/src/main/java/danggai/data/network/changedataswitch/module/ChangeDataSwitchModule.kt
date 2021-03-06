package danggai.data.network.changedataswitch.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import danggai.data.module.NetworkModule
import danggai.data.network.changedataswitch.remote.api.ChangeDataSwitchApi
import danggai.data.network.changedataswitch.repository.ChangeDataSwitchRepositoryImpl
import danggai.domain.network.changedataswitch.repository.ChangeDataSwitchRepository
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
}