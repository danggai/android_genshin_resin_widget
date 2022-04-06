package danggai.domain.getgamerecordcard.usecase

import danggai.domain.base.ApiResult
import danggai.domain.getgamerecordcard.repository.GetGameRecordCardRepository
import danggai.domain.getgamerecordcard.entity.GetGameRecordCard
import kotlinx.coroutines.flow.Flow

class GetGameRecordCardUseCase(private val getGameRecordCardRepository: GetGameRecordCardRepository) {
    suspend fun getGameRecordCard(
        hoyolabUid: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<GetGameRecordCard>> =
        getGameRecordCardRepository.getGameRecordCard(
            hoyolabUid = hoyolabUid,
            cookie = cookie,
            ds = ds,
            onStart = onStart,
            onComplete = onComplete
        )
}

