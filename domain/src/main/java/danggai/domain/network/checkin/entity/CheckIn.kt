package danggai.domain.network.checkin.entity

//{"retcode":0,"message":"OK","data":{"code":"ok","first_bind":false,"gt_result":{"risk_code":0,"gt":"","challenge":"","success":0,"is_risk":false}}}
//↑ 출석 성공
//{"retcode":0,"message":"OK","data":{"code":"ok","first_bind":false,"gt_result":{"risk_code":5001,"gt":"some key","challenge":"some key","success":1,"is_risk":true}}}
//↑ 캡차 발생

data class CheckIn (
    val retcode: String,
    val message: String,
    val data: Data?
) {
    companion object {
        val EMPTY = CheckIn(
            retcode = "",
            message = "",
            Data.EMPTY
        )
    }

    data class Data(
        val code: String,
        val first_bind: Boolean?,
        val gt_result: GtResult?) {

        companion object {
            val EMPTY = Data(
                code = "",
                first_bind = null,
                gt_result = null)
        }
    }

    data class GtResult(
        val risk_code: Int,
        val gt: String,
        val challenge: String,
        val success: Int,
        val is_risk: Boolean,
    ) {
        companion object {
            val EMPTY = GtResult(
                risk_code = 0,
                gt = "",
                challenge = "",
                success = 0,
                is_risk = false
            )
        }
    }
}
