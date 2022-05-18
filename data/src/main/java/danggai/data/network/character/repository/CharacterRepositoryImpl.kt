package danggai.data.network.character.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.data.network.character.remote.api.CharacterApi
import danggai.domain.core.ApiResult
import danggai.domain.network.character.entity.Character
import danggai.domain.network.character.repository.CharacterRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val characterApi: CharacterApi,
    private val ioDispatcher: CoroutineDispatcher
) : CharacterRepository {
    override suspend fun character(
        roleId: String,
        server: String,
        lang: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<Character>> {
        val response = characterApi.character(
            roleId,
            server,
            lang,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(this.response.body()?.let {
                ApiResult.Success<Character>(
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