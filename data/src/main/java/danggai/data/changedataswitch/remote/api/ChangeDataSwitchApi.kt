package danggai.data.changedataswitch.remote.api

import com.skydoves.sandwich.ApiResponse
import danggai.domain.changedataswitch.entity.ChangeDataSwitch
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ChangeDataSwitchApi {
    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://webstatic-sea.hoyolab.com/",
        "sec-ch-ua-mobile: ?1",
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @POST("/game_record/card/wapi/changeDataSwitch")
    suspend fun changeDataSwitch(
        @Query("game_id") gameId: Int,
        @Query("switch_id") switchId: Int,
        @Query("is_public") isPublic: Boolean,
        @Header("Cookie") cookie: String,
        @Header("DS") ds: String
    ): ApiResponse<ChangeDataSwitch>
    /* game_id: 붕괴3rd->1, 원신->2, switch_id:1~4 */
}