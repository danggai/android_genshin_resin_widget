package danggai.domain.dailynote.repository

import danggai.domain.base.ApiResult
import danggai.domain.base.Repository
import danggai.domain.dailynote.entity.DailyNote
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