package danggai.app.presentation.ui.widget.config

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.ui.widget.BatteryWidget
import danggai.app.presentation.ui.widget.HKSRDetailWidget
import danggai.app.presentation.ui.widget.MiniWidget
import danggai.app.presentation.ui.widget.TrailPowerWidget
import danggai.app.presentation.ui.widget.ZZZDetailWidget
import danggai.app.presentation.util.log
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.usecase.AccountDaoUseCase
import danggai.domain.resource.repository.ResourceProviderRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    private val resource: ResourceProviderRepository,
    private val accountDao: AccountDaoUseCase,
) : BaseViewModel() {

    val sfSelectAccount = MutableSharedFlow<Account>()
    val sfNoAccount = MutableSharedFlow<Boolean>()

    private var _widgetType = ""
    val widgetType: String
        get() = this._widgetType

    var widgetClassName = ""
    var sfSelectedAccount = MutableStateFlow<Account>(Account.EMPTY)

    val sfAccountList: StateFlow<List<Account>> =
        accountDao.selectAllAccountFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf(Account.GUEST)
            )

    fun onClickRoundButton(i: Int) {
        _widgetType = when (i) {
            R.id.rb_resin ->
                Constant.PREF_MINI_WIDGET_RESIN

            R.id.rb_parametric_transformer ->
                Constant.PREF_MINI_WIDGET_PARAMETRIC_TRANSFORMER

            R.id.rb_realm_currency ->
                Constant.PREF_MINI_WIDGET_REALM_CURRENCY

            R.id.rb_selected_chara ->
                Constant.PREF_TALENT_SELECTED_CHARACTERS

            R.id.rb_recent_chara ->
                Constant.PREF_TALENT_RECENT_CHARACTERS

            else -> ""
        }
    }

    fun onClickCb(account: Account) {
        log.e(account)

        if (sfSelectedAccount.value != account) sfSelectedAccount.value = account
        else sfSelectedAccount.value = Account.EMPTY
    }

    fun onClickConfirm() {
        if (confirmEnable()) sfSelectAccount.emitInVmScope(sfSelectedAccount.value)
    }

    fun getUid(account: Account): String =
        when {
            isHonkaiSrWidget() -> account.honkai_sr_uid
            isZZZWidget() -> account.zzz_uid
            else -> account.genshin_uid
        }

    fun getNickname(account: Account): String =
        when {
            isHonkaiSrWidget() -> account.honkai_sr_nickname
            isZZZWidget() -> account.zzz_nickname
            else -> account.nickname
        }

    fun isHonkaiSrWidget(): Boolean {
        return widgetClassName in listOf(
            TrailPowerWidget::class.java.name,
            HKSRDetailWidget::class.java.name
        )
    }

    fun isZZZWidget(): Boolean {
        return widgetClassName in listOf(
            BatteryWidget::class.java.name,
            ZZZDetailWidget::class.java.name
        )
    }

    private fun confirmEnable(): Boolean {
        if (widgetClassName == MiniWidget::class.java.name)
            if (this._widgetType == "") {
                makeToast(resource.getString(R.string.msg_toast_miniwidget_no_type))
                return false
            }

        return if (sfSelectedAccount.value == Account.EMPTY) {
            log.e()
            makeToast(resource.getString(R.string.msg_toast_widget_no_account_select))
            false
        } else true
    }
}