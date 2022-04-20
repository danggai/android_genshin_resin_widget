package danggai.domain.network.changedataswitch.repository

import danggai.domain.core.ApiResult
import danggai.domain.core.Repository
import danggai.domain.network.changedataswitch.entity.ChangeDataSwitch
import kotlinx.coroutines.flow.Flow


interface ChangeDataSwitchRepository: Repository {
    suspend fun changeDataSwitch(
        gameId: Int,
        switchId: Int,
        isPublic: Boolean,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<ChangeDataSwitch>>
}