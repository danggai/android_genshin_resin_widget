package danggai.app.presentation.util

import android.content.Context
import danggai.app.presentation.R
import danggai.domain.resource.repository.ResourceProviderRepository
import java.util.*

object DayTimeMapper {

    fun weekOfDayStringToInt(
        resource: ResourceProviderRepository,
        string: String
    ): Int {
        return when (string)  {
            resource.getString(R.string.mon) -> Calendar.MONDAY
            resource.getString(R.string.tue) -> Calendar.TUESDAY
            resource.getString(R.string.wed) -> Calendar.WEDNESDAY
            resource.getString(R.string.thu) -> Calendar.THURSDAY
            resource.getString(R.string.fri) -> Calendar.FRIDAY
            resource.getString(R.string.sat) -> Calendar.SATURDAY
            resource.getString(R.string.sun) -> Calendar.SUNDAY
            else -> Calendar.SUNDAY
        }
    }

    fun weekOfDayIntToString(
        context: Context,
        int: Int
    ): String {
        return when (int)  {
            Calendar.MONDAY -> context.getString(R.string.mon)
            Calendar.TUESDAY -> context.getString(R.string.tue)
            Calendar.WEDNESDAY -> context.getString(R.string.wed)
            Calendar.THURSDAY -> context.getString(R.string.thu)
            Calendar.FRIDAY -> context.getString(R.string.fri)
            Calendar.SATURDAY -> context.getString(R.string.sat)
            Calendar.SUNDAY -> context.getString(R.string.sun)
            else -> context.getString(R.string.sun)
        }
    }

    fun timeStringToInt(
        resource: ResourceProviderRepository,
        string: String
    ): Int {
        return when (string)  {
            resource.getString(R.string.clock_1) -> 1
            resource.getString(R.string.clock_2) -> 2
            resource.getString(R.string.clock_3) -> 3
            resource.getString(R.string.clock_4) -> 4
            resource.getString(R.string.clock_5) -> 5
            resource.getString(R.string.clock_6) -> 6
            resource.getString(R.string.clock_7) -> 7
            resource.getString(R.string.clock_8) -> 8
            resource.getString(R.string.clock_9) -> 9
            resource.getString(R.string.clock_10) -> 10
            resource.getString(R.string.clock_11) -> 11
            resource.getString(R.string.clock_12) -> 12
            resource.getString(R.string.clock_13) -> 13
            resource.getString(R.string.clock_14) -> 14
            resource.getString(R.string.clock_15) -> 15
            resource.getString(R.string.clock_16) -> 16
            resource.getString(R.string.clock_17) -> 17
            resource.getString(R.string.clock_18) -> 18
            resource.getString(R.string.clock_19) -> 19
            resource.getString(R.string.clock_20) -> 20
            resource.getString(R.string.clock_21) -> 21
            resource.getString(R.string.clock_22) -> 22
            resource.getString(R.string.clock_23) -> 23
            resource.getString(R.string.clock_24) -> 24
            else -> 21
        }
    }

    fun timeIntToString(
        context: Context,
        int: Int
    ): String {
        return when (int)  {
            1 -> context.getString(R.string.clock_1)
            2 -> context.getString(R.string.clock_2)
            3 -> context.getString(R.string.clock_3)
            4 -> context.getString(R.string.clock_4)
            5 -> context.getString(R.string.clock_5)
            6 -> context.getString(R.string.clock_6)
            7 -> context.getString(R.string.clock_7)
            8 -> context.getString(R.string.clock_8)
            9 -> context.getString(R.string.clock_9)
            10 -> context.getString(R.string.clock_10)
            11 -> context.getString(R.string.clock_11)
            12 -> context.getString(R.string.clock_12)
            13 -> context.getString(R.string.clock_13)
            14 -> context.getString(R.string.clock_14)
            15 -> context.getString(R.string.clock_15)
            16 -> context.getString(R.string.clock_16)
            17 -> context.getString(R.string.clock_17)
            18 -> context.getString(R.string.clock_18)
            19 -> context.getString(R.string.clock_19)
            20 -> context.getString(R.string.clock_20)
            21 -> context.getString(R.string.clock_21)
            22 -> context.getString(R.string.clock_22)
            23 -> context.getString(R.string.clock_23)
            24 -> context.getString(R.string.clock_24)
            else -> context.getString(R.string.clock_21)
        }
    }

}