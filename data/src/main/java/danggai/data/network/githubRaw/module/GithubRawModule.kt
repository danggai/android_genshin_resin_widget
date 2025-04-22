package danggai.data.network.githubRaw.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import danggai.data.module.NetworkModule
import danggai.data.network.githubRaw.remote.api.GithubRawApi
import danggai.data.network.githubRaw.repository.GithubRawRepositoryImpl
import danggai.domain.network.githubRaw.repository.GithubRawRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class GithubRawModule {
    @Singleton
    @Provides
    fun provideGithubRawApi(retrofit: Retrofit): GithubRawApi {
        return retrofit.create(GithubRawApi::class.java)
    }

    @Singleton
    @Provides
    fun provideGithubRawRepository(
        repositoryImpl: GithubRawRepositoryImpl
    ): GithubRawRepository = repositoryImpl
}