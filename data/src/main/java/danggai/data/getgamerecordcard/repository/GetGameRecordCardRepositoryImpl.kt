package danggai.data.getgamerecordcard.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.domain.base.Meta
import danggai.domain.getgamerecordcard.repository.GetGameRecordCardRepository
import danggai.domain.util.Constant
import danggai.data.getgamerecordcard.remote.api.GetGameRecordCardApi
import danggai.domain.base.ApiResult
import danggai.domain.getgamerecordcard.entity.GetGameRecordCard
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetGameRecordCardRepositoryImpl @Inject constructor(
    private val getGameRecordCardApi: GetGameRecordCardApi,
    private val ioDispatcher: CoroutineDispatcher
) : GetGameRecordCardRepository {
    override suspend fun getGameRecordCard(
        hoyolabUid: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<GetGameRecordCard>> {
        val emptyData = GetGameRecordCard(
            "",
            "",
            GetGameRecordCard.GameRecordCardList(listOf())
        )

        val response = getGameRecordCardApi.getGameRecordCard(
            hoyolabUid,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(ApiResult<GetGameRecordCard>(
                Meta(this.response.code(), this.response.message()),
                this.response.body()?:emptyData))
        }.suspendOnError {
            emit(ApiResult<GetGameRecordCard>(
                Meta(this.response.code(), this.response.message()),
                emptyData))
        }.suspendOnException {
            emit(ApiResult<GetGameRecordCard>(
                Meta(Constant.META_CODE_CLIENT_ERROR, this.exception.message ?: ""),
                emptyData))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}