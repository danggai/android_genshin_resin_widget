package danggai.app.presentation.ui.design.charaters

import android.widget.GridView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import danggai.app.presentation.ui.design.WidgetDesignViewModel
import danggai.app.presentation.util.log
import danggai.domain.local.LocalCharacter
import danggai.domain.network.character.entity.Avatar

object WidgetDesignCharacterBindingAdapter {

    @BindingAdapter(value = ["item", "viewModel", "refreshSwitch"], requireAll = true)
    @JvmStatic
    fun setCharacterListRecyclerView(
        view: GridView,
        item: MutableList<LocalCharacter>,
        viewModel: WidgetDesignViewModel,
        refreshSwitch: Boolean
    ) {
        log.e()
        view.adapter?.run {
            if (this is WidgetDesignCharacterAdapter) {
                log.e()
                this.setItemList(item)
            }
        } ?: run {
            WidgetDesignCharacterAdapter(viewModel).apply {
                log.e()
                view.adapter = this
                this.setItemList(item)
            }
        }
    }

    @BindingAdapter("isRefreshing")
    @JvmStatic
    fun SwipeRefreshLayout.refreshing(visible: Boolean) {
        isRefreshing = visible
    }
}