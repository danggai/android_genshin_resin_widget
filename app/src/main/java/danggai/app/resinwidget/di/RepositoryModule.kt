package danggai.app.resinwidget.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import danggai.app.resinwidget.network.ApiClient
import danggai.app.resinwidget.network.ApiRepository
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideApiRepository(
        apiClient: ApiClient,
        ioDispatcher: CoroutineDispatcher
    ): ApiRepository {
        return ApiRepository(apiClient, ioDispatcher)
    }
}