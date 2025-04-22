package danggai.data.network.githubRaw.remote.api

import com.skydoves.sandwich.ApiResponse
import danggai.domain.network.githubRaw.entity.RecentGenshinCharacters
import danggai.domain.util.Constant
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubRawApi {
    @GET(Constant.NEW_CHARA_GENSHIN_BASE_URL + "characters.json")
    suspend fun recentGenshinCharacters(
        @Query("timestamp") timestamp: Long = System.currentTimeMillis()
    ): ApiResponse<RecentGenshinCharacters>
}