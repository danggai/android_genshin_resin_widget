package danggai.app.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import danggai.app.presentation.R
import danggai.app.presentation.util.Event
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    private var _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun sendEvent(event: Event) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    fun makeToast(msg: String) {
        sendEvent(Event.MakeToast(msg))
    }
}