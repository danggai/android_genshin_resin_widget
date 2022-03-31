package danggai.app.resinwidget.network

import com.skydoves.sandwich.ApiResponse
import danggai.app.resinwidget.data.res.ResCheckIn
import danggai.app.resinwidget.data.res.ResDailyNote
import danggai.app.resinwidget.data.res.ResDefault
import danggai.app.resinwidget.data.res.ResGameRecordCard
import javax.inject.Inject


class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun dailyNote(
        uid: String,
        server: String,
        cookie: String,
        ds: String
    ): ApiResponse<ResDailyNote.Data> =
        apiService.dailyNote(
            uid = uid,
            server = server,
            cookie = cookie,
            ds = ds
        )

    suspend fun changeDataSwitch(
        gameId: Int,
        switchId: Int,
        isPublic: Boolean,
        cookie: String,
        ds: String
    ): ApiResponse<ResDefault.Data> =
        apiService.changeDataSwitch(
            gameId = gameId,
            switchId = switchId,
            isPublic = isPublic,
            cookie = cookie,
            ds = ds
        )

    suspend fun getGameRecordCard(
        hoyolabUid: String,
        cookie: String,
        ds: String
    ): ApiResponse<ResGameRecordCard.Data> =
        apiService.getGameRecordCard(
            hoyolabUid = hoyolabUid,
            cookie = cookie,
            ds = ds
        )

    suspend fun checkIn(
        region: String,
        actId: String,
        cookie: String,
        ds: String
    ): ApiResponse<ResCheckIn.Data> =
        apiService.checkIn(
            region = region,
            actId = actId,
            cookie = cookie,
            ds = ds
        )
}
