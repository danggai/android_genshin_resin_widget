package danggai.app.presentation.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * flow는 기본적으로 Android의 생명주기를 인식할 수 없습니다.
 * 이에, repeatOnLifeCycle 함수를 사용하여 flow에서도 생명주기를 인식하여
 * 데이터를 가져올 수 있도록 지원합니다.
 */
fun LifecycleOwner.repeatOnLifeCycleStarted(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
){
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(state, block)
    }
}