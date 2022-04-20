package danggai.data.network.getgamerecordcard.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.data.network.getgamerecordcard.remote.api.GetGameRecordCardApi
import danggai.domain.core.ApiResult
import danggai.domain.network.getgamerecordcard.entity.GetGameRecordCard
import danggai.domain.network.getgamerecordcard.repository.GetGameRecordCardRepository
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
        val response = getGameRecordCardApi.getGameRecordCard(
            hoyolabUid,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(this.response.body()?.let {
                ApiResult.Success<GetGameRecordCard>(
                    this.response.code(),
                    it
                )
            } ?:ApiResult.Null() )
        }.suspendOnError {
            emit(ApiResult.Failure(
                this.response.code(),
                this.response.message()
            ))
        }.suspendOnException {
            emit(ApiResult.Error(
                this.exception
            ))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}