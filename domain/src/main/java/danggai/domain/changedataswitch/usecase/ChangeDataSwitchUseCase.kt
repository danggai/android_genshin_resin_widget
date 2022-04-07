package danggai.domain.changedataswitch.usecase

import danggai.domain.base.ApiResult
import danggai.domain.changedataswitch.entity.ChangeDataSwitch
import danggai.domain.changedataswitch.repository.ChangeDataSwitchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangeDataSwitchUseCase @Inject constructor(
    private val changeDataSwitchRepository: ChangeDataSwitchRepository
    ) {
    suspend fun changeDataSwitch(
        gameId: Int,
        switchId: Int,
        isPublic: Boolean,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<ChangeDataSwitch>> =
        changeDataSwitchRepository.changeDataSwitch(
            gameId = gameId,
            switchId = switchId,
            isPublic = isPublic,
            cookie = cookie,
            ds = ds,
            onStart = onStart,
            onComplete = onComplete
        )
}