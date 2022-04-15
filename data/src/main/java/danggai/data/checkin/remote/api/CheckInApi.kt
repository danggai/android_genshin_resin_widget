package danggai.data.checkin.remote.api

import com.skydoves.sandwich.ApiResponse
import danggai.domain.checkin.entity.CheckIn
import danggai.domain.util.Constant
import retrofit2.http.*

interface CheckInApi {
    @Headers(
        "Accept: application/json, text/plain, */*",
        "Content-Type: application/json;charset=UTF-8",
        "Referer: https://webstatic-sea.hoyolab.com/",
        "sec-ch-ua-mobile: ?1"
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
        "Referer: https://webstatic-sea.hoyolab.com/",
        "sec-ch-ua-mobile: ?1"
    )
    @POST(Constant.OS_HONKAI_3RD_CHECK_IN_URL)
    suspend fun honkai3rd(
        @Query("lang") lang: String,
        @Query("act_id") actId: String,
        @Header("Cookie") cookie: String,
    ): ApiResponse<CheckIn>
}