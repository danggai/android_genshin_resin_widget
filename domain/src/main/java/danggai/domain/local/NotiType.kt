package danggai.domain.local

sealed class NotiType {
    sealed class Genshin : NotiType() {
        object StaminaEach40 : Genshin()
        object Stamina180 : Genshin()
        object StaminaCustom : Genshin()
        object ExpeditionDone : Genshin()
        object RealmCurrencyFull : Genshin()
        object ParametricTransformerReached : Genshin()
        object DailyCommissionNotDone : Genshin()
        object WeeklyBossNotDone : Genshin()
    }

    sealed class StarRail : NotiType() {
        object StaminaEach40 : StarRail()
        object Stamina290 : StarRail()
        object StaminaCustom : StarRail()
        object ExpeditionDone : StarRail()
    }

    sealed class ZZZ : NotiType() {
        object StaminaEach40 : ZZZ()
        object StaminaEach60 : ZZZ()
        object Stamina230 : ZZZ()
        object StaminaCustom : ZZZ()
    }

    sealed class CheckIn : NotiType() {
        sealed class _Genshin : CheckIn() {
            object Success : _Genshin()
            object Already : _Genshin()
            object Failed : _Genshin()
            object CaptchaOccured : _Genshin()
        }

        sealed class _Honkai3rd : CheckIn() {
            object Success : _Honkai3rd()
            object Already : _Honkai3rd()
            object Failed : _Honkai3rd()
        }

        sealed class _StarRail : CheckIn() {
            object Success : _StarRail()
            object Already : _StarRail()
            object Failed : _StarRail()
        }

        sealed class _ZZZ : CheckIn() {
            object Success : _ZZZ()
            object Already : _ZZZ()
            object Failed : _ZZZ()
        }

        sealed class NotFound : CheckIn() {
            object AccountGenshin : NotFound()
            object AccountHonkai3rd : NotFound()
            object AccountStarRail : NotFound()
            object AccountZZZ : NotFound()
        }
    }
}