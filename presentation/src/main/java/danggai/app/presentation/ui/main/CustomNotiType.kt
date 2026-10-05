package danggai.app.presentation.ui.main

import danggai.domain.util.Constant

enum class CustomNotiType(val max: Int) {
    RESIN(Constant.MAX_RESIN),
    TRAIL_POWER(Constant.MAX_TRAILBLAZE_POWER),
    BATTERY(Constant.MAX_BATTERY)
}