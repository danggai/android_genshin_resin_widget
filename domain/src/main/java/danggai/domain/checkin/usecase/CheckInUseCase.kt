package danggai.domain.checkin.usecase

import danggai.domain.base.ApiResult
import danggai.domain.checkin.entity.CheckIn
import danggai.domain.checkin.repository.CheckInRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckInUseCase @Inject constructor(
    private val checkInRepository: CheckInRepository
    ) {
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