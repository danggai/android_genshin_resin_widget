package danggai.data.network.checkin.remote.api

import com.skydoves.sandwich.ApiResponse
import danggai.domain.network.checkin.entity.CheckIn
import danggai.domain.network.checkin.entity.CheckInZZZ
import danggai.domain.util.Constant
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

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


    @Headers(
        "accept: */*",
        "accept-encoding: gzip, deflate, br",
        "accept-language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        "content-type: application/json",
        "Origin: https://act.hoyolab.com",
        "Referer: https://act.hoyolab.com/",
        "sec-ch-ua-mobile: ?1",
        "sec-ch-ua-platform: \"Android\"",
        "sec-fetch-dest: empty",
        "sec-fetch-mode: cors",
        "sec-fetch-site: same-site",
        "Content-Type: application/json;charset=UTF-8",
        "Host: sg-public-api.hoyolab.com",
        "sec-ch-ua-mobile: ?1",
        "x-rpc-client_type: 5",
        "x-rpc-platform: 5",
        "x-rpc-signgame: zzz",
    )
    @POST(Constant.OS_ZZZ_CHECK_IN_URL)
    suspend fun zenlessZoneZero(
        @Body body: Map<String, String>,
        @Header("Cookie") cookie: String,
        @Header("Timestamp") timeStamp: Int = (System.currentTimeMillis() / 1000).toInt()
    ): ApiResponse<CheckInZZZ>
}

//verification: d69a5ee64100aca23432e55c12f42f1a1a15cd7e