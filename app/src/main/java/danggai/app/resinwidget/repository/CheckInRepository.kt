package danggai.app.resinwidget.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.data.local.CheckIn
import danggai.app.resinwidget.data.req.ReqCheckIn
import danggai.app.resinwidget.data.res.Meta
import danggai.app.resinwidget.data.res.ResCheckIn
import danggai.app.resinwidget.network.ApiClient
import danggai.app.resinwidget.util.CommonFunction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class CheckInRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val ioDispatcher: CoroutineDispatcher
): Repository {
    fun checkIn(
        data: ReqCheckIn,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ResCheckIn> {
        val emptyData = ResCheckIn.Data( "","", CheckIn(""))

        val response = apiClient.checkIn(
            data.region,
            data.actId,
            data.cookie,
            CommonFunction.getGenshinDS()
        )

        response.suspendOnSuccess {
            emit(ResCheckIn(Meta(this.response.code(), this.response.message()), this.response.body()?:emptyData))
        }.suspendOnError {
            emit(ResCheckIn(Meta(this.response.code(), this.response.message()), emptyData))
        }.suspendOnException {
            emit(ResCheckIn(Meta(Constant.META_CODE_CLIENT_ERROR, this.exception.message?:""), emptyData))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}