package danggai.data.network.character.remote.api

import com.skydoves.sandwich.ApiResponse
import danggai.domain.network.character.entity.Character
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface CharacterApi {
    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://act.hoyolab.com/",
        "sec-ch-ua-mobile: ?1",
        "sec-ch-ua-platform: Android",
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
    )
    @POST("/game_record/genshin/api/character")
    suspend fun character(
        @Query("role_id") roleId: String,
        @Query("server") server: String,
        @Header ("x-rpc-language") lang: String,
        @Header("Cookie") cookie: String,
        @Header("DS") ds: String
    ): ApiResponse<Character>
}