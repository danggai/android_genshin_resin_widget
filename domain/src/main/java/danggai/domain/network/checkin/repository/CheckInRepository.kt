package danggai.domain.network.checkin.repository

import danggai.domain.core.ApiResult
import danggai.domain.core.Repository
import danggai.domain.network.checkin.entity.CheckIn
import kotlinx.coroutines.flow.Flow

interface CheckInRepository: Repository {
    suspend fun genshinImpact(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckIn>>

    suspend fun honkai3rd(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckIn>>
}