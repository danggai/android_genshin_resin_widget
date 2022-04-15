package danggai.data.checkin.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.data.checkin.remote.api.CheckInApi
import danggai.domain.base.ApiResult
import danggai.domain.checkin.entity.CheckIn
import danggai.domain.checkin.repository.CheckInRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class CheckInRepositoryImpl @Inject constructor(
    private val checkInApi: CheckInApi,
    private val ioDispatcher: CoroutineDispatcher
) : CheckInRepository {
    override suspend fun genshinImpact(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<CheckIn>> {
        val response = checkInApi.genshinImpact(
            lang,
            actId,
            cookie
        )

        response.suspendOnSuccess {
            emit( this.response.body()?.let {
                ApiResult.Success<CheckIn>(
                    this.response.code(),
                    it
                )
            } ?: ApiResult.Null())
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

    override suspend fun honkai3rd(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<CheckIn>> {
        val response = checkInApi.honkai3rd(
            lang,
            actId,
            cookie
        )

        response.suspendOnSuccess {
            emit( this.response.body()?.let {
                ApiResult.Success<CheckIn>(
                    this.response.code(),
                    it
                )
            } ?: ApiResult.Null())
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