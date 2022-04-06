package danggai.domain.changedataswitch.repository

import danggai.domain.base.ApiResult
import danggai.domain.base.Repository
import danggai.domain.changedataswitch.entity.ChangeDataSwitch
import kotlinx.coroutines.flow.Flow


interface ChangeDataSwitchRepository: Repository {
    fun changeDataSwitch(
        gameId: Int,
        switchId: Int,
        isPublic: Boolean,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<ChangeDataSwitch>>
}