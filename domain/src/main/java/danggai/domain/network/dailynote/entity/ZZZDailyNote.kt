package danggai.domain.network.dailynote.entity

import com.google.gson.annotations.SerializedName

data class ZZZDailyNote(
    val data: ZZZDailyNoteData,
    val message: String,
    val retcode: Int
) {
    companion object {
        val EMPTY = ZZZDailyNote(
            retcode = 0,
            message = "",
            data = ZZZDailyNoteData.EMPTY
        )
    }
}

data class ZZZDailyNoteData(
    @SerializedName("card_sign") val cardSign: String,
    @SerializedName("energy") val energy: ZZZEnergy,
    @SerializedName("vhs_sale") val vhsSale: ZZZVhsSale,
    @SerializedName("vitality") val vitality: ZZZProgress,
    @SerializedName("coffee") val coffee: Any?,
    @SerializedName("bounty_commission") val bountyCommission: ZZZNumTotal,
    @SerializedName("survey_points") val surveyPoints: ZZZNumTotal,
    @SerializedName("weekly_task") val weeklyTask: ZZZWeeklyTask
) {
    companion object {
        val EMPTY = ZZZDailyNoteData(
            cardSign = "",
            energy = ZZZEnergy.EMPTY,
            vhsSale = ZZZVhsSale.EMPTY,
            vitality = ZZZProgress.EMPTY,
            coffee = null,
            bountyCommission = ZZZNumTotal.EMPTY,
            surveyPoints = ZZZNumTotal.EMPTY,
            weeklyTask = ZZZWeeklyTask.EMPTY
        )
    }
}

data class ZZZEnergy(
    @SerializedName("progress") val progress: ZZZProgress,
    @SerializedName("restore") val restore: Int
) {
    companion object {
        val EMPTY = ZZZEnergy(
            progress = ZZZProgress.EMPTY,
            restore = -1
        )
    }
}

data class ZZZProgress(
    @SerializedName("max") val max: Int,
    @SerializedName("current") val current: Int
) {
    companion object {
        val EMPTY = ZZZProgress(
            max = -1,
            current = -1
        )
    }
}

data class ZZZVhsSale(
    @SerializedName("sale_state") val saleState: String
) {
    companion object {
        val EMPTY = ZZZVhsSale(
            saleState = ""
        )
    }
}

data class ZZZNumTotal(
    @SerializedName("num") val num: Int,
    @SerializedName("total") val total: Int
) {
    companion object {
        val EMPTY = ZZZNumTotal(
            num = -1,
            total = -1
        )
    }
}

data class ZZZWeeklyTask(
    @SerializedName("cur_point") val curPoint: Int,
    @SerializedName("max_point") val maxPoint: Int
) {
    companion object {
        val EMPTY = ZZZWeeklyTask(
            curPoint = -1,
            maxPoint = -1
        )
    }
}
