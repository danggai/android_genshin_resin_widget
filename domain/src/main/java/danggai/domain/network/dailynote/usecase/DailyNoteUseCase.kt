package danggai.domain.network.dailynote.usecase

import danggai.domain.base.ApiResult
import danggai.domain.network.dailynote.entity.DailyNote
import danggai.domain.network.dailynote.repository.DailyNoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyNoteUseCase @Inject constructor(
    private val dailyNoteRepository: DailyNoteRepository
    ) {
    suspend operator fun invoke(
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