package danggai.data.checkin.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.data.checkin.remote.api.CheckInApi
import danggai.domain.base.ApiResult
import danggai.domain.base.Meta
import danggai.domain.checkin.entity.CheckIn
import danggai.domain.checkin.repository.CheckInRepository
import danggai.domain.util.Constant
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
    override suspend fun checkIn(
        region: String,
        actId: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<CheckIn>> {
        val emptyData = CheckIn(
            "",
            "",
            CheckIn.Data(""))

        val response = checkInApi.checkIn(
            region,
            actId,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(ApiResult<CheckIn>(
                Meta(this.response.code(), this.response.message()),
                this.response.body()?:emptyData
            ))
        }.suspendOnError {
            emit(ApiResult<CheckIn>(
                Meta(this.response.code(), this.response.message()),
                emptyData
            ))
        }.suspendOnException {
            emit(ApiResult<CheckIn>(
                Meta(Constant.META_CODE_CLIENT_ERROR,
                this.exception.message ?: ""),
                emptyData
            ))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}