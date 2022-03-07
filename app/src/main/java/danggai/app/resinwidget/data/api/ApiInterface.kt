package danggai.app.resinwidget.data.api

import danggai.app.resinwidget.Constant
import danggai.app.resinwidget.data.res.ResCheckIn
import danggai.app.resinwidget.data.res.ResDailyNote
import danggai.app.resinwidget.data.res.ResDefault
import danggai.app.resinwidget.data.res.ResGameRecordCard
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @Headers(
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @GET("/game_record/genshin/api/dailyNote")
    fun dailyNote(@Query("role_id") uid: String, @Query("server") server: String,
                  @Header("Cookie") cookie: String, @Header("DS") ds: String): Observable<Response<ResDailyNote.Data>>

    @Headers(
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @POST("/game_record/card/wapi/changeDataSwitch")
    fun changeDataSwitch(@Query("game_id") gameId: Int, @Query("switch_id") switchId: Int, @Query("is_public") isPublic: Boolean,
                         @Header("Cookie") cookie: String, @Header("DS") ds: String): Observable<Response<ResDefault.Data>>
    /* game_id: 붕괴3rd->1, 원신->2, switch_id:1~4 */

    @Headers(
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @GET("/game_record/card/wapi/getGameRecordCard")
    fun getGameRecordCard(@Query("uid") hoyolabUid: String,
                          @Header("Cookie") cookie: String, @Header("DS") ds: String): Observable<Response<ResGameRecordCard.Data>>

    @Headers(
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @POST(Constant.OS_CHECK_IN_URL)
    fun checkIn(@Query("lang") region: String, @Query("act_id") actId: String,
                @Header("Cookie") cookie: String, @Header("DS") ds: String): Observable<Response<ResCheckIn.Data>>

}