package danggai.app.resinwidget.di

import danggai.app.resinwidget.worker.RefreshWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val WorkerModule = module {
    worker { params -> RefreshWorker(get(), workerParams = params.get(), get()) }
}