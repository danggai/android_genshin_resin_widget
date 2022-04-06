package danggai.domain.checkin.repository

import danggai.domain.base.ApiResult
import danggai.domain.base.Repository
import danggai.domain.checkin.entity.CheckIn
import kotlinx.coroutines.flow.Flow

interface CheckInRepository: Repository {
    suspend fun checkIn(
        region: String,
        actId: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckIn>>
}