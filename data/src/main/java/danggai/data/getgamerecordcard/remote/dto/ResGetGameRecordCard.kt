package danggai.data.getgamerecordcard.remote.dto

import danggai.domain.base.Meta
import danggai.domain.getgamerecordcard.entity.GetGameRecordCard

data class ResGetGameRecordCard (
    val meta: Meta,
    val data: GetGameRecordCard
)