package danggai.data.network.character.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import danggai.data.module.NetworkModule
import danggai.data.network.character.remote.api.CharacterApi
import danggai.data.network.character.repository.CharacterRepositoryImpl
import danggai.domain.network.character.repository.CharacterRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class CharacterModule {
    @Singleton
    @Provides
    fun provideCharacterApi(retrofit: Retrofit): CharacterApi {
        return retrofit.create(CharacterApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCharacterRepository(
        repositoryImpl: CharacterRepositoryImpl
    ): CharacterRepository = repositoryImpl
}