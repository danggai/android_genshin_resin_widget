package danggai.data.dailynote.remote.api

import com.skydoves.sandwich.ApiResponse
import danggai.domain.dailynote.entity.DailyNote
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface DailyNoteApi {
    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://webstatic-sea.hoyolab.com/",
        "sec-ch-ua-mobile: ?1",
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @GET("/game_record/genshin/api/dailyNote")
    suspend fun dailyNote(
        @Query("role_id") uid: String,
        @Query("server") server: String,
        @Header("Cookie") cookie: String,
        @Header("DS") ds: String
    ): ApiResponse<DailyNote>
}