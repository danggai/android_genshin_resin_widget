package danggai.domain.network.dailynote.entity

import com.google.gson.annotations.SerializedName

data class Transformer(
    @SerializedName("obtained") val obtained: Boolean,                      // 해금여부
    @SerializedName("recovery_time") val recoveryTime: TransformerTime
) {
    companion object {
        val EMPTY = Transformer(
            obtained = false,
            recoveryTime = TransformerTime.EMPTY
        )
    }
}