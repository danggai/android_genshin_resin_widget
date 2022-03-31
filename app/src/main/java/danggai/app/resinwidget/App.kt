package danggai.app.resinwidget

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import danggai.app.resinwidget.util.LocaleWrapper
import danggai.app.resinwidget.util.log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException
import javax.inject.Inject

@HiltAndroidApp
class App: Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

//    @KoinExperimentalAPI
    override fun onCreate() {
        super.onCreate()

//        startKoin {
//            androidLogger(Level.ERROR)
//            androidContext(this@App)
//            workManagerFactory()
//            koin.loadModules(listOf(ViewModelModule, NetworkModule, repositoryModule, WorkerModule))
//        }

        // Crashlytics
        FirebaseApp.initializeApp(this)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        

        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) {
                log.e("undeliverable exception: ${e.cause?.message?:"unknown"}")
            }
            if (e is IOException || e is SocketException || e is InterruptedException) {
                return@setErrorHandler
            }
            if (e is NullPointerException || e is IllegalArgumentException || e is IllegalStateException) {
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
        }
    }

    override fun attachBaseContext(baseContext: Context) {
        super.attachBaseContext(LocaleWrapper.wrap(baseContext))
    }
}