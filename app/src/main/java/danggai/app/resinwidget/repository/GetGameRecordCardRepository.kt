package danggai.app.resinwidget.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.data.req.ReqGetGameRecordCard
import danggai.app.resinwidget.data.res.Meta
import danggai.app.resinwidget.data.res.ResGetGameRecordCard
import danggai.app.resinwidget.network.ApiClient
import danggai.app.resinwidget.util.CommonFunction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetGameRecordCardRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val ioDispatcher: CoroutineDispatcher
): Repository {
    fun getGameRecordCard(
        data: ReqGetGameRecordCard,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ResGetGameRecordCard> {
        val emptyData = ResGetGameRecordCard.Data("","", ResGetGameRecordCard.GameRecordCardList(listOf()))

        val response = apiClient.getGameRecordCard(
            data.hoyolabUid,
            data.cookie,
            CommonFunction.getGenshinDS()
        )

        response.suspendOnSuccess {
            emit(ResGetGameRecordCard(Meta(this.response.code(), this.response.message()), this.response.body()?:emptyData))
        }.suspendOnError {
            emit(ResGetGameRecordCard(Meta(this.response.code(), this.response.message()), emptyData))
        }.suspendOnException {
            emit(ResGetGameRecordCard(Meta(Constant.META_CODE_CLIENT_ERROR, this.exception.message?:""), emptyData))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

}