package danggai.app.presentation.ui.design.charaters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import danggai.app.presentation.ui.design.WidgetDesignViewModel
import danggai.app.presentation.util.log
import danggai.domain.local.LocalCharacter

object WidgetDesignCharacterBindingAdapter {
    @BindingAdapter(
        value = ["characters", "viewModel", "numColumns", "refreshSwitch"],
        requireAll = true
    )
    @JvmStatic
    fun setCharacterListRecyclerView(
        view: RecyclerView,
        characters: MutableList<LocalCharacter>,
        viewModel: WidgetDesignViewModel,
        numColumns: Int,
        refreshSwitch: Boolean,
    ) {
        log.e()

        if (view.layoutManager == null) {
            // numColumns에 컬럼 수 설정
            view.layoutManager = GridLayoutManager(view.context, numColumns)
        }

        view.adapter?.run {
            if (this is WidgetDesignCharacterAdapter) {
                log.e()
                this.hasStableIds()
                this.setItemList(characters)
            }
        } ?: run {
            WidgetDesignCharacterAdapter(viewModel).apply {
                log.e()
                view.adapter = this
                this.hasStableIds()
                this.setItemList(characters)
            }
        }
    }
}