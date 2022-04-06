package danggai.domain.checkin.usecase

import danggai.domain.base.ApiResult
import danggai.domain.checkin.repository.CheckInRepository
import danggai.domain.checkin.entity.CheckIn
import kotlinx.coroutines.flow.Flow

class CheckInUseCase (private val checkInRepository: CheckInRepository) {
    suspend fun checkIn(
        region: String,
        actId: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckIn>> =
        checkInRepository.checkIn(
            region = region,
            actId = actId,
            cookie = cookie,
            ds = ds,
            onStart = onStart,
            onComplete = onComplete
        )
}