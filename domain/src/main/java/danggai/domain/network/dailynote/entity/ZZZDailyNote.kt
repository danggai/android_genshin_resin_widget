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

/**
 * @param cardSign 복권
 * @param energy 에너지
 * @param vhsSale 비디오 가게 운영 여부
 * @param vitality 일일 임무
 * @param coffee 커피 (미구현인듯?)
 * @param bountyCommission 제로 공동 현상금 의뢰
 * @param surveyPoints 제로 공동 조사 포인트
 * @param weeklyTask 주간 리두
 * @param memberCard 월정액
 */
data class ZZZDailyNoteData(
    @SerializedName("card_sign") val cardSign: String,
    @SerializedName("energy") val energy: ZZZEnergy,
    @SerializedName("vhs_sale") val vhsSale: ZZZVhsSale,
    @SerializedName("vitality") val vitality: ZZZProgress,
    @SerializedName("coffee") val coffee: Any?,  // 아직 미구현 인듯? null로만 옴
    @SerializedName("bounty_commission") val bountyCommission: ZZZNumTotal?,
    @SerializedName("survey_points") val surveyPoints: ZZZNumTotal?,
    @SerializedName("weekly_task") val weeklyTask: ZZZWeeklyTask?,
    @SerializedName("member_card") val memberCard: ZZZMemberCard?
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
            weeklyTask = ZZZWeeklyTask.EMPTY,
            memberCard = ZZZMemberCard.EMPTY
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

data class ZZZMemberCard(
    @SerializedName("is_open") val isOpen: Boolean,
    @SerializedName("member_card_state") val memberCardState: String,
    @SerializedName("exp_time") val expTime: Int
) {
    companion object {
        val EMPTY = ZZZMemberCard(
            isOpen = false,
            memberCardState = "",
            expTime = 0
        )
    }
}
