package danggai.app.presentation.ui.widget.config

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.log
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.usecase.AccountDaoUseCase
import danggai.domain.resource.repository.ResourceProviderRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    private val resource: ResourceProviderRepository,
    private val accountDao: AccountDaoUseCase,
) : BaseViewModel() {

    val sfSelectAccount = MutableSharedFlow<Account>()

    val sfAccountList: StateFlow<List<Account>> =
        accountDao.selectAllAccountFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = listOf()
            )

    fun onClickCb(account: Account) {
        log.e()
        sfSelectAccount.emitInVmScope(account)
    }
}