package danggai.domain.network.character.usecase

import danggai.domain.core.ApiResult
import danggai.domain.network.character.entity.Character
import danggai.domain.network.character.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CharacterUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
    ) {
    suspend operator fun invoke(
        roleId: Int,
        server: Int,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<Character>> =
        characterRepository.character(
            roleId = roleId,
            server = server,
            cookie = cookie,
            ds = ds,
            onStart = onStart,
            onComplete = onComplete
        )
}