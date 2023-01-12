package danggai.app.presentation.ui.main

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import danggai.app.presentation.util.log
import danggai.domain.db.account.entity.Account

object MainBindingAdapter {

    @BindingAdapter(value = ["item", "viewModel", "refreshSwitch"], requireAll = true)
    @JvmStatic
    fun setAccountListRecyclerView(
        view: RecyclerView,
        item: List<Account>,
        viewModel: MainViewModel,
        refreshSwitch: Boolean
    ) {
        log.e()
        view.adapter?.run {
            if (this is MainAdapter) {
                log.e()
                this.hasStableIds()
                this.setItemList(item)
            }
        } ?: run {
            MainAdapter(viewModel).apply {
                log.e()
                view.adapter = this
                this.hasStableIds()
                this.setItemList(item)
            }
        }
    }
}