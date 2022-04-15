package danggai.domain.network.getgamerecordcard.usecase

import danggai.domain.base.ApiResult
import danggai.domain.network.getgamerecordcard.entity.GetGameRecordCard
import danggai.domain.network.getgamerecordcard.repository.GetGameRecordCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGameRecordCardUseCase @Inject constructor(
    private val getGameRecordCardRepository: GetGameRecordCardRepository
    ) {
    suspend operator fun invoke(
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

