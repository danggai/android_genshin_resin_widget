package danggai.domain.network.dailynote.repository

import danggai.domain.core.ApiResult
import danggai.domain.core.Repository
import danggai.domain.network.dailynote.entity.DailyNote
import kotlinx.coroutines.flow.Flow

interface DailyNoteRepository: Repository {
    suspend fun dailyNote(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<DailyNote>>
}