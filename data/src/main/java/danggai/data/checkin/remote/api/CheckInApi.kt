package danggai.data.checkin.remote.api

import com.skydoves.sandwich.ApiResponse
import danggai.domain.checkin.entity.CheckIn
import danggai.domain.util.Constant
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface CheckInApi {
    @Headers(
        "x-rpc-client_type: 4",
        "x-rpc-app_version: 1.5.0",
        "x-rpc-language: ko-kr",
    )
    @POST(Constant.OS_CHECK_IN_URL)
    suspend fun checkIn(
        @Query("lang") region: String,
        @Query("act_id") actId: String,
        @Header("Cookie") cookie: String,
        @Header("DS") ds: String
    ): ApiResponse<CheckIn>
}