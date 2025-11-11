package danggai.data.network.dailynote.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.data.network.dailynote.remote.api.DailyNoteApi
import danggai.domain.core.ApiResult
import danggai.domain.network.dailynote.entity.GenshinDailyNote
import danggai.domain.network.dailynote.entity.HonkaiSrDailyNote
import danggai.domain.network.dailynote.entity.HonkaiSrGrid
import danggai.domain.network.dailynote.entity.HonkaiSrRogue
import danggai.domain.network.dailynote.entity.ZZZDailyNote
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
    override suspend fun dailyNoteGenshin(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<GenshinDailyNote>> {
        val response = dailyNoteApi.dailyNoteGenshin(
            uid,
            server,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(this.response.body()?.let {
                ApiResult.Success<GenshinDailyNote>(
                    this.response.code(),
                    it
                )
            } ?: ApiResult.Null())
        }.suspendOnError {
            emit(
                ApiResult.Failure(
                    this.response.code(),
                    this.response.message()
                )
            )
        }.suspendOnException {
            emit(
                ApiResult.Error(
                    this.exception
                )
            )
        }
    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

    override suspend fun dailyNoteHonkaiSr(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<HonkaiSrDailyNote>> {
        val response = dailyNoteApi.dailyNoteHonkaiSr(
            uid,
            server,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(this.response.body()?.let {
                ApiResult.Success<HonkaiSrDailyNote>(
                    this.response.code(),
                    it
                )
            } ?: ApiResult.Null())
        }.suspendOnError {
            emit(
                ApiResult.Failure(
                    this.response.code(),
                    this.response.message()
                )
            )
        }.suspendOnException {
            emit(
                ApiResult.Error(
                    this.exception
                )
            )
        }
    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

    override suspend fun rogueHonkaiSr(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<HonkaiSrRogue>> {
        val response = dailyNoteApi.rogueHonkaiSr(
            uid,
            server,
            schedule_type = 3,
            need_detail = false,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(this.response.body()?.let {
                ApiResult.Success<HonkaiSrRogue>(
                    this.response.code(),
                    it
                )
            } ?: ApiResult.Null())
        }.suspendOnError {
            emit(
                ApiResult.Failure(
                    this.response.code(),
                    this.response.message()
                )
            )
        }.suspendOnException {
            emit(
                ApiResult.Error(
                    this.exception
                )
            )
        }
    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

    override suspend fun gridHonkaiSr(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<HonkaiSrGrid>> {
        val response = dailyNoteApi.gridHonkaiSr(
            uid,
            server,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(this.response.body()?.let {
                ApiResult.Success<HonkaiSrGrid>(
                    this.response.code(),
                    it
                )
            } ?: ApiResult.Null())
        }.suspendOnError {
            emit(
                ApiResult.Failure(
                    this.response.code(),
                    this.response.message()
                )
            )
        }.suspendOnException {
            emit(
                ApiResult.Error(
                    this.exception
                )
            )
        }
    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

    override suspend fun dailyNoteZZZ(
        uid: String,
        server: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<ZZZDailyNote>> {
        val response = dailyNoteApi.dailyNoteZZZ(
            uid,
            server,
            cookie
        )

        response.suspendOnSuccess {
            emit(this.response.body()?.let {
                ApiResult.Success<ZZZDailyNote>(
                    this.response.code(),
                    it
                )
            } ?: ApiResult.Null())
        }.suspendOnError {
            emit(
                ApiResult.Failure(
                    this.response.code(),
                    this.response.message()
                )
            )
        }.suspendOnException {
            emit(
                ApiResult.Error(
                    this.exception
                )
            )
        }
    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}

