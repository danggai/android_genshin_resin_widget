package danggai.domain.local

enum class TalentDate {
    MON_THU,   // 월, 목
    TUE_FRI,   // 화, 금
    WED_SAT,   // 수, 토
    ALL;       // 매일
}

object TalentDays {
    val MON_THU = listOf(1, 2, 5)
    val TUE_FRI = listOf(1, 3, 6)
    val WED_SAT = listOf(1, 4, 7)
}