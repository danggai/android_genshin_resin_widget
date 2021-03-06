package danggai.data.module;


import android.content.Context
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import danggai.domain.util.Constant
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
        private const val TIMEOUT = 60L

        @Singleton
        @Provides
        fun provideCache(
                @ApplicationContext applicationContext: Context
        ): Cache { 
                return Cache(applicationContext.cacheDir, 10L * 1024 * 1024)
        }

        @Singleton
        @Provides
        fun provideOkHttpClient(
                cache: Cache
        ): OkHttpClient {
                return OkHttpClient.Builder()
                        .cache(cache)
                        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                        .addInterceptor(HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                        })
                        .build()
        }

        @Singleton
        @Provides
        fun provideRetrofitClient(
                okHttpClient: OkHttpClient
        ): Retrofit {
                return Retrofit.Builder()
                        .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(Constant.OS_TAKUMI_URL)
                        .client(okHttpClient)
                        .build()
        }
}
