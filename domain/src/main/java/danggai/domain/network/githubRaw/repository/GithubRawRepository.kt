package danggai.domain.network.githubRaw.repository

import danggai.domain.core.ApiResult
import danggai.domain.core.Repository
import danggai.domain.network.githubRaw.entity.RecentGenshinCharacters
import kotlinx.coroutines.flow.Flow

interface GithubRawRepository : Repository {
    suspend fun recentGenshinCharacters(
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<RecentGenshinCharacters>>
}