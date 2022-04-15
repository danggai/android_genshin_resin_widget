package danggai.domain.checkin.usecase

import danggai.domain.base.ApiResult
import danggai.domain.checkin.entity.CheckIn
import danggai.domain.checkin.repository.CheckInRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckInUseCase @Inject constructor(
    private val checkInRepository: CheckInRepository
    ) {
    suspend fun genshinImpact(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckIn>> =
        checkInRepository.genshinImpact(
            lang = lang,
            actId = actId,
            cookie = cookie,
            onStart = onStart,
            onComplete = onComplete
        )

    suspend fun honkai3rd(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckIn>> =
        checkInRepository.honkai3rd(
            lang = lang,
            actId = actId,
            cookie = cookie,
            onStart = onStart,
            onComplete = onComplete
        )
}