package danggai.app.resinwidget.network

import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.data.local.CheckIn
import danggai.app.resinwidget.data.local.DailyNote
import danggai.app.resinwidget.data.res.*
import danggai.app.resinwidget.util.CommonFunction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val ioDispatcher: CoroutineDispatcher
) {

    fun dailyNote(
        uid: String,
        server: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ResDailyNote> {
        val emptyData = ResDailyNote.Data("","", DailyNote( -1,-1,"-1", -1, -1, false, -1, -1, -1, -1, "-1", -1, -1, listOf()))

        val response = apiClient.dailyNote(uid, server, cookie, CommonFunction.getGenshinDS())

        response.suspendOnSuccess {
            emit(ResDailyNote(Meta(this.response.code(), this.response.message()), this.response.body()?:emptyData))
        }.suspendOnError {
            emit(ResDailyNote(Meta(this.response.code(), this.response.message()), emptyData))
        }.suspendOnException {
            emit(ResDailyNote(Meta(Constant.META_CODE_CLIENT_ERROR, this.exception.message?:""), emptyData))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

    fun changeDataSwitch(
        gameId: Int,
        switchId: Int,
        isPublic: Boolean,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ResDefault> {
        val emptyData = ResDefault.Data("","")

        val response = apiClient.changeDataSwitch(gameId, switchId, isPublic, cookie, CommonFunction.getGenshinDS())

        response.suspendOnSuccess {
            emit(ResDefault(Meta(this.response.code(), this.response.message()), this.response.body()?:emptyData))
        }.suspendOnError {
            emit(ResDefault(Meta(this.response.code(), this.response.message()), emptyData))
        }.suspendOnException {
            emit(ResDefault(Meta(Constant.META_CODE_CLIENT_ERROR, this.exception.message?:""), emptyData))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

    fun getCameRecordCard(
        hoyolabUid: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ResGameRecordCard> {
        val emptyData = ResGameRecordCard.Data("","", ResGameRecordCard.GameRecordCardList(listOf()))

        val response = apiClient.getGameRecordCard(hoyolabUid, cookie, CommonFunction.getGenshinDS())

        response.suspendOnSuccess {
            emit(ResGameRecordCard(Meta(this.response.code(), this.response.message()), this.response.body()?:emptyData))
        }.suspendOnError {
            emit(ResGameRecordCard(Meta(this.response.code(), this.response.message()), this.response.body()?:emptyData))
        }.suspendOnException {
            emit(ResGameRecordCard(Meta(Constant.META_CODE_CLIENT_ERROR, this.exception.message?:""), emptyData))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

    fun checkIn(
        region: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ) = flow<ResCheckIn> {
        val emptyData = ResCheckIn.Data("","", CheckIn(""))

        val actId = Constant.OS_ACT_ID

        val response = apiClient.checkIn(region, actId, cookie, CommonFunction.getGenshinDS())

        response.suspendOnSuccess {
            emit(ResCheckIn(Meta(this.response.code(), this.response.message()), this.response.body()?:emptyData))
        }.suspendOnError {
            emit(ResCheckIn(Meta(this.response.code(), this.response.message()), this.response.body()?:emptyData))
        }.suspendOnException {
            emit(ResCheckIn(Meta(Constant.META_CODE_CLIENT_ERROR, this.exception.message?:""), emptyData))
        }
    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)
}