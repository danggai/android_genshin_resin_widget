package danggai.data.dailynote.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.data.dailynote.remote.api.DailyNoteApi
import danggai.domain.base.ApiResult
import danggai.domain.base.Meta
import danggai.domain.dailynote.entity.DailyNote
import danggai.domain.dailynote.repository.DailyNoteRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class DailyNoteRepositoryImpl @Inject constructor(
    private val dailyNoteApi: DailyNoteApi,
    private val ioDispatcher: CoroutineDispatcher
) : DailyNoteRepository {
    override suspend fun dailyNote(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<DailyNote>> {
        val emptyData = DailyNote("","",
            DailyNote.Data(-1,
                -1,
                "-1",
                -1,
                -1,
                false,
                -1,
                -1,
                -1,
                -1,
                "-1",
                -1,
                -1,
                listOf()))

        val response = dailyNoteApi.dailyNote(
            uid,
            server,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(ApiResult<DailyNote>(
                Meta(this.response.code(), this.response.message()),
                this.response.body()?:emptyData
            ))
        }.suspendOnError {
            emit(ApiResult<DailyNote>(
                Meta(this.response.code(), this.response.message()),
                emptyData
            ))
        }.suspendOnException {
            emit(ApiResult<DailyNote>(
                Meta(Constant.META_CODE_CLIENT_ERROR, this.exception.message ?: ""),
                emptyData
            ))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}

