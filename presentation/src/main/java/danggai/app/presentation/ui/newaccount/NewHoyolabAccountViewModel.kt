package danggai.app.presentation.ui.newaccount

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import danggai.app.presentation.core.BaseViewModel
import danggai.app.presentation.util.log
import danggai.domain.db.account.entity.Account
import danggai.domain.db.account.usecase.AccountDaoUseCase
import danggai.domain.network.character.usecase.CharacterUseCase
import danggai.domain.resource.repository.ResourceProviderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewHoyolabAccountViewModel @Inject constructor(
    private val accountDaoUseCase: AccountDaoUseCase,
) : BaseViewModel() {

    val sfDeleteTargetId = MutableStateFlow("")

    var count: Int = 0

    fun insertDummyAccount() {
        viewModelScope.launch {
            val account = Account(count.toString(), count.toString(),false, false, false)
            count++

            accountDaoUseCase.insertAccount(account).collect {
                log.e(it)
            }
        }
    }

    fun getAllAccounts() {
        viewModelScope.launch {
            accountDaoUseCase.getAllAccount().collect {
                log.e("accounts -> \n$it")
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            accountDaoUseCase.deleteAccount(sfDeleteTargetId.value).collect {
                log.e("deleted $it account(s)")
            }
        }
    }

    fun deleteAllAccounts() {
        viewModelScope.launch {
            accountDaoUseCase.deleteAllAccounts().collect {
                log.e("deleted $it account(s)")
            }
        }
    }
}