package danggai.app.resinwidget.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.R
import danggai.app.resinwidget.data.api.ApiRepository
import danggai.app.resinwidget.ui.base.BaseViewModel
import danggai.app.resinwidget.util.Event
import danggai.app.resinwidget.util.NonNullMutableLiveData
import danggai.app.resinwidget.util.log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MainViewModel(override val app: Application, private val api: ApiRepository) : BaseViewModel(app) {

    private val rxApiDailyNote: PublishSubject<Boolean> = PublishSubject.create()

    var lvSaveUserInfo = MutableLiveData<Event<Boolean>>()

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
                when (res.meta.code) {
                    Constant.META_CODE_SUCCESS -> {
                        log.e(res)
                        when (res.data.retcode) {
                            Constant.RETCODE_SUCCESS -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_success))
                            }
                            Constant.RETCODE_ERROR_CHARACTOR_INFO -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_charactor_info))
                            }
                            Constant.RETCODE_ERROR_INVALID_REQUEST -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error_invalid_request))
                            }
                            else -> {
                                lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error))
                            }
                        }
                    }
                }
            }, {
                it.message?.let { msg ->
                    lvMakeToast.value = Event(getString(R.string.msg_toast_dailynote_error))
                    log.e(msg)
                }
            }).addCompositeDisposable()
    }

    fun initUI(uid: String, cookie: String) {
        log.e()
        lvUid.value = uid
        lvCookie.value = cookie

        initRx()
    }

    fun onClickSave() {
        lvSaveUserInfo.value = Event(true)

        rxApiDailyNote.onNext(true)
    }

}