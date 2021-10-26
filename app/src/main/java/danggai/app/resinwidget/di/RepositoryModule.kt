package danggai.app.resinwidget.di

import danggai.app.resinwidget.data.api.ApiInterface
import danggai.app.resinwidget.data.api.ApiRepository
import org.koin.dsl.module
import retrofit2.Retrofit

val repositoryModule = module {
    single(createdAtStart = false) { ApiRepository(get<Retrofit>().create(ApiInterface::class.java)) }
}