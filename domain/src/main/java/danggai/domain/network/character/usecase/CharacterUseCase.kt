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
        roleId: String,
        server: String,
        lang: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<Character>> =
        characterRepository.character(
            roleId = roleId,
            server = server,
            lang = lang,
            cookie = cookie,
            ds = ds,
            onStart = onStart,
            onComplete = onComplete
        )
}