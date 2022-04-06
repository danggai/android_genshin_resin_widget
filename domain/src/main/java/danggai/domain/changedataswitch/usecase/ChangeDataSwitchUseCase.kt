package danggai.domain.changedataswitch.usecase

import danggai.domain.base.ApiResult
import danggai.domain.changedataswitch.repository.ChangeDataSwitchRepository
import danggai.domain.changedataswitch.entity.ChangeDataSwitch
import kotlinx.coroutines.flow.Flow

class ChangeDataSwitchUseCase (private val changeDataSwitchRepository: ChangeDataSwitchRepository) {
    fun changeDataSwitch(
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

//    suspend fun changeDataSwitch(
//        gameId: Int,
//        switchId: Int,
//        isPublic: Boolean,
//        cookie: String,
//        ds: String
//    ): ApiResponse<ResChangeDataSwitch.Data> =
//        apiService.changeDataSwitch(
//            gameId = gameId,
//            switchId = switchId,
//            isPublic = isPublic,
//            cookie = cookie,
//            ds = ds
//        )

}