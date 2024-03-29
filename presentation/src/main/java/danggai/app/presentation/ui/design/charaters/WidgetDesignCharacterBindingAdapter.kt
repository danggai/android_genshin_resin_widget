package danggai.app.presentation.ui.design.charaters

import android.widget.GridView
import androidx.databinding.BindingAdapter
import danggai.app.presentation.ui.design.WidgetDesignViewModel
import danggai.app.presentation.util.log
import danggai.domain.local.LocalCharacter

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
                this.hasStableIds()
                this.setItemList(item)
            }
        } ?: run {
            WidgetDesignCharacterAdapter(viewModel).apply {
                log.e()
                view.adapter = this
                this.hasStableIds()
                this.setItemList(item)
            }
        }
    }
}