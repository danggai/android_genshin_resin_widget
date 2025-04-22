package danggai.domain.network.githubRaw.usecase

import danggai.domain.core.ApiResult
import danggai.domain.network.githubRaw.entity.RecentGenshinCharacters
import danggai.domain.network.githubRaw.repository.GithubRawRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GithubRawUseCase @Inject constructor(
    private val githubRawRepository: GithubRawRepository
) {
    suspend fun recentGenshinCharacters(
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<RecentGenshinCharacters>> =
        githubRawRepository.recentGenshinCharacters(
            onStart = onStart,
            onComplete = onComplete
        )
}

