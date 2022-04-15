package danggai.data.network.checkin.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import danggai.data.module.NetworkModule
import danggai.data.network.checkin.remote.api.CheckInApi
import danggai.data.network.checkin.repository.CheckInRepositoryImpl
import danggai.domain.network.checkin.repository.CheckInRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class CheckInModule {
    @Singleton
    @Provides
    fun provideCheckInApi(retrofit: Retrofit): CheckInApi {
        return retrofit.create(CheckInApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCheckInRepository(
        repositoryImpl: CheckInRepositoryImpl
    ): CheckInRepository = repositoryImpl
}