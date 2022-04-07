package danggai.domain.dailynote.usecase

import danggai.domain.base.ApiResult
import danggai.domain.dailynote.entity.DailyNote
import danggai.domain.dailynote.repository.DailyNoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyNoteUseCase @Inject constructor(
    private val dailyNoteRepository: DailyNoteRepository
    ) {
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