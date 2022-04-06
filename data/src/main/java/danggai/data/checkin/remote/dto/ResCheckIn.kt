package danggai.data.checkin.remote.dto

import danggai.domain.checkin.entity.CheckIn
import danggai.domain.base.Meta

data class ResCheckIn (
    val meta: Meta,
    val data: CheckIn
) {
}