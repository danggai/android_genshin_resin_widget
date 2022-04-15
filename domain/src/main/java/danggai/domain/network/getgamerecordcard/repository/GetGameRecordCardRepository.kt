package danggai.domain.network.getgamerecordcard.repository

import danggai.domain.base.ApiResult
import danggai.domain.base.Repository
import danggai.domain.network.getgamerecordcard.entity.GetGameRecordCard
import kotlinx.coroutines.flow.Flow

interface GetGameRecordCardRepository: Repository {
    suspend fun getGameRecordCard(
        hoyolabUid: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<GetGameRecordCard>>
}