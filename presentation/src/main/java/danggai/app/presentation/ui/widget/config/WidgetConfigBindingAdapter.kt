package danggai.app.presentation.ui.widget.config

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import danggai.app.presentation.util.log
import danggai.domain.db.account.entity.Account

object WidgetConfigBindingAdapter {
    @BindingAdapter(value = ["item", "viewModel"], requireAll = true)
    @JvmStatic
    fun setAccountListRecyclerView(
        view: RecyclerView,
        item: List<Account>,
        viewModel: WidgetConfigViewModel
    ) {
        log.e()
        view.adapter?.run {
            if (this is WidgetConfigAdapter) {
                log.e()
                this.hasStableIds()
                this.setItemList(item)
            }
        } ?: run {
            WidgetConfigAdapter(viewModel).apply {
                log.e()
                view.adapter = this
                this.hasStableIds()
                this.setItemList(item)
            }
        }
    }
}