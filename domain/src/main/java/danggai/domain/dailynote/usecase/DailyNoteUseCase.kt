package danggai.domain.dailynote.usecase

import danggai.domain.base.ApiResult
import danggai.domain.dailynote.repository.DailyNoteRepository
import danggai.domain.dailynote.entity.DailyNote
import kotlinx.coroutines.flow.Flow

class DailyNoteUseCase (private val dailyNoteRepository: DailyNoteRepository) {
    suspend fun dailyNote(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<DailyNote>> =
        dailyNoteRepository.dailyNote(
            uid = uid,
            server = server,
            cookie = cookie,
            ds = ds,
            onStart = onStart,
            onComplete = onComplete
        )
}