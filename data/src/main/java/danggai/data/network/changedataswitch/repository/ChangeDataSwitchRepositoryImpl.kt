package danggai.data.network.changedataswitch.repository

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.data.network.changedataswitch.remote.api.ChangeDataSwitchApi
import danggai.domain.base.ApiResult
import danggai.domain.network.changedataswitch.entity.ChangeDataSwitch
import danggai.domain.network.changedataswitch.repository.ChangeDataSwitchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ChangeDataSwitchRepositoryImpl @Inject constructor(
    private val changeDataSwitchApi: ChangeDataSwitchApi,
    private val ioDispatcher: CoroutineDispatcher
) : ChangeDataSwitchRepository {
    override suspend fun changeDataSwitch(
        gameId: Int,
        switchId: Int,
        isPublic: Boolean,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ApiResult<ChangeDataSwitch>> {
        val response = changeDataSwitchApi.changeDataSwitch(
            gameId,
            switchId,
            isPublic,
            cookie,
            ds
        )

        response.suspendOnSuccess {
            emit(this.response.body()?.let {
                ApiResult.Success<ChangeDataSwitch>(
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