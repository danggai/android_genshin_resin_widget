package danggai.app.resinwidget.core

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import danggai.app.resinwidget.util.Event

open class BaseViewModel(open val app: Application) : ViewModel() {

    var lvMakeToast = MutableLiveData<Event<String>>()

    fun getString(resId: Int): String {
        return app.getString(resId)
    }

}