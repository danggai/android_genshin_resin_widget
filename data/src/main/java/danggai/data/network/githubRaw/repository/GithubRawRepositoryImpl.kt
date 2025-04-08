package danggai.data.network.githubRaw.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.data.network.githubRaw.remote.api.GithubRawApi
import danggai.domain.core.ApiResult
import danggai.domain.network.githubRaw.entity.RecentGenshinCharacters
import danggai.domain.network.githubRaw.repository.GithubRawRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GithubRawRepositoryImpl @Inject constructor(
    private val githubRawApi: GithubRawApi,
    private val ioDispatcher: CoroutineDispatcher
) : GithubRawRepository {
    override suspend fun recentGenshinCharacters(
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<RecentGenshinCharacters>> {
        val response = githubRawApi.recentGenshinCharacters()

        response.suspendOnSuccess {
            emit(this.response.body()?.let {
                ApiResult.Success<RecentGenshinCharacters>(
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