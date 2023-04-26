package danggai.data.network.checkin.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.data.network.checkin.remote.api.CheckInApi
import danggai.domain.core.ApiResult
import danggai.domain.network.checkin.entity.CheckIn
import danggai.domain.network.checkin.repository.CheckInRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
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

    override suspend fun honkaiSR(
        lang: String,
        actId: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<CheckIn>> {
        val response = checkInApi.honkaiSR(
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