package danggai.domain.network.checkin.repository

import danggai.domain.core.ApiResult
import danggai.domain.core.Repository
import danggai.domain.network.checkin.entity.CheckIn
import danggai.domain.network.checkin.entity.CheckInZZZ
import kotlinx.coroutines.flow.Flow

interface CheckInRepository : Repository {
    suspend fun genshinImpact(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckIn>>

    suspend fun genshinImpactRetry(
        lang: String,
        actId: String,
        cookie: String,
        challenge: String,
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

    suspend fun honkaiSR(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckIn>>

    suspend fun zenlessZoneZero(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckInZZZ>>
}