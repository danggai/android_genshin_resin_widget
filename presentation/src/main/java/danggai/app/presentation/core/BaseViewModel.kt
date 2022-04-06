package danggai.app.presentation.core

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import danggai.app.presentation.core.util.Event

open class BaseViewModel(open val app: Application) : ViewModel() {

    var lvMakeToast = MutableLiveData<Event<String>>()

    fun getString(resId: Int): String {
        return app.getString(resId)
    }

}