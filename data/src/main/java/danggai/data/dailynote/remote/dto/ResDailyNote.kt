package danggai.data.dailynote.remote.dto

import danggai.domain.base.Meta
import danggai.domain.dailynote.entity.DailyNote

data class ResDailyNote (
    val meta: Meta,
    val data: DailyNote
) {
}