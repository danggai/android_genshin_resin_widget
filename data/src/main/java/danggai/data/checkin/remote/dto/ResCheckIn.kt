package danggai.data.checkin.remote.dto

import danggai.domain.base.Meta
import danggai.domain.checkin.entity.CheckIn

data class ResCheckIn (
    val meta: Meta,
    val data: CheckIn
) {
}