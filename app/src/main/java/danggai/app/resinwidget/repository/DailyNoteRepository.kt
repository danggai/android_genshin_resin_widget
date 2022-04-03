package danggai.app.resinwidget.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.data.req.ReqDailyNote
import danggai.app.resinwidget.data.res.Meta
import danggai.app.resinwidget.data.res.ResDailyNote
import danggai.app.resinwidget.network.ApiClient
import danggai.app.resinwidget.util.CommonFunction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class DailyNoteRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val ioDispatcher: CoroutineDispatcher
): Repository {
    fun dailyNote(
        data: ReqDailyNote,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ResDailyNote> {
        val emptyData = ResDailyNote.Data("","", DailyNote( -1,-1,"-1", -1, -1, false, -1, -1, -1, -1, "-1", -1, -1, listOf()))

        val response = apiClient.dailyNote(
            data.uid,
            data.server,
            data.cookie,
            CommonFunction.getGenshinDS()
        )

        response.suspendOnSuccess {
            emit(ResDailyNote(Meta(this.response.code(), this.response.message()), this.response.body()?:emptyData))
        }.suspendOnError {
            emit(ResDailyNote(Meta(this.response.code(), this.response.message()), emptyData))
        }.suspendOnException {
            emit(ResDailyNote(Meta(Constant.META_CODE_CLIENT_ERROR, this.exception.message?:""), emptyData))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}