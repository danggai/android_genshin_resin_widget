package danggai.domain.network.dailynote.entity

import com.google.gson.annotations.SerializedName

data class Transformer(
    @SerializedName("obtained") val obtained: Boolean,                      // 해금여부
    @SerializedName("recovery_time") val recoveryTime: TransformerTime,
    @SerializedName("wiki") val wiki: String? = null,                          // 참량 질변기 위키 링크 (전적 페이지 미표시)
    @SerializedName("noticed") val noticed: Boolean = false,                // 알림 확인 여부
    @SerializedName("latest_job_id") val latestJobId: String? = null          // 최근 작업 id
) {
    companion object {
        val EMPTY = Transformer(
            obtained = false,
            recoveryTime = TransformerTime.EMPTY
        )
    }
}