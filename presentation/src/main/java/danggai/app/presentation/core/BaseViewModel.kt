package danggai.app.presentation.core

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import danggai.app.presentation.core.util.Event

open class BaseViewModel : ViewModel() {
    var lvMakeToast = MutableLiveData<Event<String>>()
}