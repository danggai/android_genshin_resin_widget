package danggai.domain.network.dailynote.entity

import com.google.gson.annotations.SerializedName

data class TransformerTime(
    @SerializedName("Day") val day: Int,
    @SerializedName("Hour") val hour: Int,
    @SerializedName("Minute") val minute: Int,
    @SerializedName("Second") val second: Int,
    @SerializedName("reached") val reached: Boolean
) {
    companion object {
        val EMPTY = TransformerTime(
            0,
            0,
            0,
            0,
            false
        )

        val REACHED = TransformerTime(
            0,
            0,
            0,
            0,
            true
        )
    }
}