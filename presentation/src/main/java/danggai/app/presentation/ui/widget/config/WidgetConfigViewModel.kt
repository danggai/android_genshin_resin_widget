package danggai.app.presentation.ui.widget.config

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.R
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.ui.widget.MiniWidget
import danggai.app.presentation.ui.widget.TrailPowerWidget
import danggai.app.presentation.util.log
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.usecase.AccountDaoUseCase
import danggai.domain.resource.repository.ResourceProviderRepository
import danggai.domain.util.Constant
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    private val resource: ResourceProviderRepository,
    private val accountDao: AccountDaoUseCase,
) : BaseViewModel() {

    val sfSelectAccount = MutableSharedFlow<Account>()
    val sfNoAccount = MutableSharedFlow<Boolean>()

    private var _miniWidgetType = ""
    val miniWidgetType: String
        get() = this._miniWidgetType

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
        _miniWidgetType = when (i) {
            R.id.rb_resin ->
                Constant.PREF_MINI_WIDGET_RESIN
            R.id.rb_parametric_transformer ->
                Constant.PREF_MINI_WIDGET_PARAMETRIC_TRANSFORMER
            R.id.rb_realm_currency ->
                Constant.PREF_MINI_WIDGET_REALM_CURRENCY
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
        if (isHonkaiSrWidget()) account.honkai_sr_uid else account.genshin_uid

    fun getNickname(account: Account): String =
        if (isHonkaiSrWidget()) account.honkai_sr_nickname else account.nickname

    fun isHonkaiSrWidget(): Boolean {
        return widgetClassName == TrailPowerWidget::class.java.name
    }

    private fun confirmEnable(): Boolean {
        if (widgetClassName == MiniWidget::class.java.name)
            if (this._miniWidgetType == "") {
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