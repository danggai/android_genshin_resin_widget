package danggai.data.network.getgamerecordcard.remote.api

import com.skydoves.sandwich.ApiResponse
import danggai.domain.network.getgamerecordcard.entity.GetGameRecordCard
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface GetGameRecordCardApi {
    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://webstatic-sea.hoyolab.com/",
        "sec-ch-ua-mobile: ?1",
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @GET("/game_record/card/wapi/getGameRecordCard")
    suspend fun getGameRecordCard(
        @Query("uid") hoyolabUid: String,
        @Header("Cookie") cookie: String,
        @Header("DS") ds: String
    ): ApiResponse<GetGameRecordCard>
}