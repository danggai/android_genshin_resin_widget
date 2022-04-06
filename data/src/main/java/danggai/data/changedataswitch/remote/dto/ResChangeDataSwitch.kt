package danggai.data.changedataswitch.remote.dto

import danggai.domain.base.Meta
import danggai.domain.changedataswitch.entity.ChangeDataSwitch

data class ResChangeDataSwitch (
    val meta: Meta,
    val data: ChangeDataSwitch
)