package danggai.app.resinwidget.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.data.req.ReqChangeDataSwitch
import danggai.app.resinwidget.data.res.Meta
import danggai.app.resinwidget.data.res.ResDefault
import danggai.app.resinwidget.network.ApiClient
import danggai.app.resinwidget.util.CommonFunction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ChangeDataSwitchRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val ioDispatcher: CoroutineDispatcher
): Repository {
    fun changeDataSwitch(
        data: ReqChangeDataSwitch,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ResDefault> {
        val emptyData = ResDefault.Data("","")

        val response = apiClient.changeDataSwitch(
            data.gameId,
            data.switchId,
            data.isPublic,
            data.cookie,
            CommonFunction.getGenshinDS()
        )

        response.suspendOnSuccess {
            emit(ResDefault(Meta(this.response.code(), this.response.message()), this.response.body()?:emptyData))
        }.suspendOnError {
            emit(ResDefault(Meta(this.response.code(), this.response.message()), emptyData))
        }.suspendOnException {
            emit(ResDefault(Meta(Constant.META_CODE_CLIENT_ERROR, this.exception.message?:""), emptyData))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}