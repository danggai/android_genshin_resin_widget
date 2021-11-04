package danggai.app.resinwidget.di

import danggai.app.resinwidget.ui.cookie_web_view.CookieWebViewViewModel
import danggai.app.resinwidget.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { CookieWebViewViewModel(get(), get()) }
}