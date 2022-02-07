package danggai.app.resinwidget

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import danggai.app.resinwidget.di.NetworkModule
import danggai.app.resinwidget.di.ViewModelModule
import danggai.app.resinwidget.di.WorkerModule
import danggai.app.resinwidget.di.repositoryModule
import danggai.app.resinwidget.util.LocaleWrapper
import danggai.app.resinwidget.util.log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.io.IOException
import java.net.SocketException


class App: Application() {

    @KoinExperimentalAPI
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            workManagerFactory()
            koin.loadModules(listOf(ViewModelModule, NetworkModule, repositoryModule, WorkerModule))
//            koin.createRootScope()
        }

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