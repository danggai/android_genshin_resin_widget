package danggai.data.checkin.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import danggai.data.checkin.remote.api.CheckInApi
import danggai.data.checkin.repository.CheckInRepositoryImpl
import danggai.data.module.NetworkModule
import danggai.domain.changedataswitch.repository.ChangeDataSwitchRepository
import danggai.domain.changedataswitch.usecase.ChangeDataSwitchUseCase
import danggai.domain.checkin.repository.CheckInRepository
import danggai.domain.checkin.usecase.CheckInUseCase
import kotlinx.coroutines.CoroutineDispatcher
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
        api: CheckInApi,
        ioDispatcher: CoroutineDispatcher
    ): CheckInRepository {
        return CheckInRepositoryImpl(api, ioDispatcher)
    }

    @Provides
    fun provideCheckInUseCase(
        repository: CheckInRepository
    ): CheckInUseCase {
        return CheckInUseCase(repository)
    }
}