package danggai.app.resinwidget.di

import danggai.app.resinwidget.worker.CheckInWorker
import danggai.app.resinwidget.worker.DailyNoteWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val WorkerModule = module {
    worker { params -> DailyNoteWorker(get(), workerParams = params.get(), get()) }
    worker { params -> CheckInWorker(get(), workerParams = params.get(), get()) }
}