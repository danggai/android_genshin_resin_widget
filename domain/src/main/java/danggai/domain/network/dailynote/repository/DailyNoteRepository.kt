package danggai.domain.network.dailynote.repository

import danggai.domain.core.ApiResult
import danggai.domain.core.Repository
import danggai.domain.network.dailynote.entity.GenshinDailyNote
import danggai.domain.network.dailynote.entity.HonkaiSrDailyNote
import danggai.domain.network.dailynote.entity.HonkaiSrRogue
import danggai.domain.network.dailynote.entity.ZZZDailyNote
import kotlinx.coroutines.flow.Flow

interface DailyNoteRepository: Repository {
    suspend fun dailyNoteGenshin(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<GenshinDailyNote>>

    suspend fun dailyNoteHonkaiSr(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<HonkaiSrDailyNote>>

    suspend fun rogueHonkaiSr(
        uid: String,
        server: String,
        cookie: String,
        ds: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<HonkaiSrRogue>>

    suspend fun dailyNoteZZZ(
        uid: String,
        server: String,
        cookie: String,
        onStart: () -> Unit,
        onComplete: () -> Unit
    ): Flow<ApiResult<ZZZDailyNote>>
}