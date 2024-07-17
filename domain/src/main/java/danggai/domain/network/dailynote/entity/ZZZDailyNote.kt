package danggai.domain.network.dailynote.entity

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
    val card_sign: String,
    val energy: ZZZEnergy,
    val vhs_sale: ZZZVhsSale,
    val vitality: ZZZProgress,
){
    companion object {
        val EMPTY = ZZZDailyNoteData(
            card_sign = "",
            energy = ZZZEnergy.EMPTY,
            vhs_sale = ZZZVhsSale.EMPTY,
            vitality = ZZZProgress.EMPTY,
        )
    }
}

data class ZZZEnergy(
    val progress: ZZZProgress,
    val restore: Int
) {
    companion object {
        val EMPTY = ZZZEnergy(
            progress = ZZZProgress.EMPTY,
            restore = -1,
        )
    }
}

data class ZZZProgress(
    val max: Int,
    val current: Int
) {
    companion object {
        val EMPTY = ZZZProgress(
            max = -1,
            current = -1,
        )
    }
}

data class ZZZVhsSale(
    val sale_state: String,
) {
    companion object {
        val EMPTY = ZZZVhsSale(
            sale_state = "",
        )
    }
}