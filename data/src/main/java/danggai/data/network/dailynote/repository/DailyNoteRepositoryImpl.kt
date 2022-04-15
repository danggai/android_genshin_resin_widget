package danggai.data.network.dailynote.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.data.network.dailynote.remote.api.DailyNoteApi
import danggai.domain.base.ApiResult
import danggai.domain.network.dailynote.entity.DailyNote
import danggai.domain.network.dailynote.repository.DailyNoteRepository
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
        val response = dailyNoteApi.dailyNote(
            uid,
            server,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(this.response.body()?.let {
                ApiResult.Success<DailyNote>(
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

