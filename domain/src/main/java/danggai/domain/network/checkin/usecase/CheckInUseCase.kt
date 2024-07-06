package danggai.domain.network.checkin.usecase

import danggai.domain.core.ApiResult
import danggai.domain.network.checkin.entity.CheckIn
import danggai.domain.network.checkin.entity.CheckInZZZ
import danggai.domain.network.checkin.repository.CheckInRepository
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

    suspend fun genshinImpactRetry(
        lang: String,
        actId: String,
        cookie: String,
        challenge: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckIn>> =
        checkInRepository.genshinImpactRetry(
            lang = lang,
            actId = actId,
            cookie = cookie,
            onStart = onStart,
            challenge = challenge,
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

    suspend fun honkaiSR(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckIn>> =
        checkInRepository.honkaiSR(
            lang = lang,
            actId = actId,
            cookie = cookie,
            onStart = onStart,
            onComplete = onComplete
        )

    suspend fun zenlessZoneZero(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<CheckInZZZ>> =
        checkInRepository.zenlessZoneZero(
            lang = lang,
            actId = actId,
            cookie = cookie,
            onStart = onStart,
            onComplete = onComplete
        )
}