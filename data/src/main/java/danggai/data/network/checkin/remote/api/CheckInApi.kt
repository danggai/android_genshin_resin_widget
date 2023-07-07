package danggai.data.network.checkin.remote.api

import com.skydoves.sandwich.ApiResponse
import danggai.domain.network.checkin.entity.CheckIn
import danggai.domain.util.Constant
import retrofit2.http.*

interface CheckInApi {
    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://act.hoyolab.com/",
        "sec-ch-ua: \"Chromium\";v=\"112\", \"Not:A-Brand\";v=\"24\"",
        "sec-ch-ua-mobile: ?1",
        "sec-ch-ua-platform: \"Android\"",
        "User-Agent: Mozilla/5.0; AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36",
        "x-rpc-app_version: ",
        "x-rpc-device_id: ",
        "x-rpc-device_name: ",
        "x-rpc-platform: 5"
    )
    @POST(Constant.OS_GENSHIN_CHECK_IN_URL)
    suspend fun genshinImpact(
        @Query("lang") lang: String,
        @Query("act_id") actId: String,
        @Header("Cookie") cookie: String
    ): ApiResponse<CheckIn>

    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://act.hoyolab.com/",
        "sec-ch-ua: \"Chromium\";v=\"112\", \"Not:A-Brand\";v=\"24\"",
        "sec-ch-ua-mobile: ?1",
        "sec-ch-ua-platform: \"Android\"",
        "User-Agent: Mozilla/5.0; AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36",
        "x-rpc-app_version: ",
        "x-rpc-device_id: ",
        "x-rpc-device_name: ",
        "x-rpc-platform: 5"
    )
    @POST(Constant.OS_GENSHIN_CHECK_IN_URL)
    suspend fun genshinImpactRetry(
        @Query("lang") lang: String,
        @Query("act_id") actId: String,
        @Header("Cookie") cookie: String,
        @Header("x-rpc-challenge") challenge: String
    ): ApiResponse<CheckIn>

    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://webstatic-sea.hoyolab.com/",
        "sec-ch-ua-mobile: ?1"
    )
    @POST(Constant.OS_HONKAI_3RD_CHECK_IN_URL)
    suspend fun honkai3rd(
        @Query("lang") lang: String,
        @Query("act_id") actId: String,
        @Header("Cookie") cookie: String,
    ): ApiResponse<CheckIn>

    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://webstatic-sea.hoyolab.com/",
        "sec-ch-ua-mobile: ?1"
    )
    @POST(Constant.OS_HONKAI_SR_CHECK_IN_URL)
    suspend fun honkaiSR(
        @Query("lang") lang: String,
        @Query("act_id") actId: String,
        @Header("Cookie") cookie: String,
    ): ApiResponse<CheckIn>
}