package danggai.app.resinwidget.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import danggai.app.resinwidget.App
import danggai.data.util.ResourceProviderImpl
import danggai.domain.util.ResourceProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideApplication(): App {
        return App()
    }

    @Singleton
    @Provides
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): ResourceProvider = ResourceProviderImpl(context)
}