package danggai.domain.network.character.repository

import danggai.domain.core.ApiResult
import danggai.domain.core.Repository
import danggai.domain.network.character.entity.Character
import kotlinx.coroutines.flow.Flow


interface CharacterRepository: Repository {
    suspend fun character(
        roleId: Int,
        server: Int,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<Character>>
}