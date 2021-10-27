package danggai.app.resinwidget.ui.main

import android.app.Application
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.ui.base.BaseViewModel
import danggai.app.resinwidget.util.NonNullMutableLiveData
import danggai.app.resinwidget.util.log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MainViewModel(override val app: Application, private val api: ApiRepository) : BaseViewModel(app) {

    private val rxApiDailyNote: PublishSubject<Boolean> = PublishSubject.create()

    val lvUid: NonNullMutableLiveData<String> = NonNullMutableLiveData("")
    val lvCookie: NonNullMutableLiveData<String> = NonNullMutableLiveData("")

    private fun initRx() {
        rxApiDailyNote
            .observeOn(Schedulers.newThread())
            .filter { it }
            .switchMap {
                api.dailyNote(lvUid.value, lvCookie.value)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ res ->
                log.e(res)
            }, {
                it.message?.let { msg ->
                    log.e(msg)
                }
            }).addCompositeDisposable()
    }

    fun initUI() {
        initRx()
//        rxApiDailyNote.onNext(true)
    }
}