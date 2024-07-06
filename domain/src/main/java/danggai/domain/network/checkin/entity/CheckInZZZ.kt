package danggai.domain.network.checkin.entity

//{
//    "retcode": 0,
//    "message": "OK",
//    "data": {
//        "code": "",
//        "risk_code": 0,
//        "gt": "",
//        "challenge": "",
//        "success": 0,
//        "is_risk": false
//    }
//}
// ↑ 출석 성공
//
// ↑ 캡차 발생

data class CheckInZZZ(
    val retcode: String,
    val message: String,
    val data: Data
) {
    companion object {
        val EMPTY = CheckInZZZ(
            retcode = "",
            message = "",
            Data.EMPTY
        )
    }

    data class Data(
        val code: String,
        val risk_code: Int,
        val gt: String,
        val challenge: String,
        val success: Int,
        val is_risk: Boolean
    ) {

        companion object {
            val EMPTY = Data(
                code = "",
                risk_code = 0,
                gt = "",
                challenge = "",
                success = 0,
                is_risk = false
            )
        }
    }
}
