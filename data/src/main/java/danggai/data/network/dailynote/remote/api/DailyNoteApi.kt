package danggai.data.network.dailynote.remote.api

import com.skydoves.sandwich.ApiResponse
import danggai.domain.network.dailynote.entity.GenshinDailyNote
import danggai.domain.network.dailynote.entity.HonkaiSrDailyNote
import danggai.domain.network.dailynote.entity.HonkaiSrGrid
import danggai.domain.network.dailynote.entity.HonkaiSrRogue
import danggai.domain.network.dailynote.entity.ZZZDailyNote
import danggai.domain.util.Constant
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
    suspend fun dailyNoteGenshin(
        @Query("role_id") uid: String,
        @Query("server") server: String,
        @Header("Cookie") cookie: String,
        @Header("DS") ds: String
    ): ApiResponse<GenshinDailyNote>

    //    bbs-api-os.hoyolab.com/game_record/hkrpg/api/index?server=prod_official_asia&role_id=800000000
    // ↑ 보유중 캐릭터, 업적 등 나옴
    //    bbs-api-os.hoyolab.com/game_record/hkrpg/api/note?server=prod_official_asia&role_id=800000000
    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://webstatic-sea.hoyolab.com/",
        "sec-ch-ua-mobile: ?1",
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @GET("/game_record/hkrpg/api/note")
    suspend fun dailyNoteHonkaiSr(
        @Query("role_id") uid: String,
        @Query("server") server: String,
        @Header("Cookie") cookie: String,
        @Header("DS") ds: String
    ): ApiResponse<HonkaiSrDailyNote>


    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://webstatic-sea.hoyolab.com/",
        "sec-ch-ua-mobile: ?1",
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @GET("/game_record/hkrpg/api/rogue")
    suspend fun rogueHonkaiSr(
        @Query("role_id") uid: String,
        @Query("server") server: String,
        @Query("schedule_type") schedule_type: Int,
        @Query("need_detail") need_detail: Boolean,
        @Header("Cookie") cookie: String,
        @Header("DS") ds: String
    ): ApiResponse<HonkaiSrRogue>


    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://webstatic-sea.hoyolab.com/",
        "sec-ch-ua-mobile: ?1",
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @GET("/game_record/hkrpg/api/grid_fight")
    suspend fun gridHonkaiSr(
        @Query("role_id") uid: String,
        @Query("server") server: String,
        @Header("Cookie") cookie: String,
        @Header("DS") ds: String
    ): ApiResponse<HonkaiSrGrid>

    @Headers(
        "Accept: */*",
        "Accept-Encoding: gzip, deflate, br",
        "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        "Content-Type: application/json",
        "Origin: https://act.hoyolab.com",
        "Referer: https://act.hoyolab.com/",
        "Sec-Ch-Ua-Mobile: ?1",
        "Sec-Ch-Ua-Platform: \"Android\"",
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @GET(Constant.SG_ACT_NAP_API + "/event/game_record_zzz/api/zzz/note")
    suspend fun dailyNoteZZZ(
        @Query("role_id") uid: String,
        @Query("server") server: String,
        @Header("Cookie") cookie: String,
    ): ApiResponse<ZZZDailyNote>
}