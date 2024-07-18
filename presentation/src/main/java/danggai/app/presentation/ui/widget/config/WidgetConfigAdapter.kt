package danggai.app.presentation.ui.widget.config

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import danggai.app.presentation.R
import danggai.app.presentation.databinding.ItemWidgetConfigAccountBinding
import danggai.app.presentation.util.log
import danggai.domain.db.account.entity.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetConfigAdapter(
    val vm: WidgetConfigViewModel
) : RecyclerView.Adapter<WidgetConfigAdapter.ItemViewHolder>() {

    private var items: MutableList<Account> = arrayListOf()

    fun setItemList(_itemList: List<Account>) {
        log.e(_itemList.size)
        if (_itemList.size == 1 && _itemList[0] == Account.GUEST) return
        items.clear()

        val validAccountList =
            when {
                vm.isHonkaiSrWidget() -> _itemList.filter { it.honkai_sr_uid.isNotEmpty() }
                vm.isZZZWidget() -> _itemList.filter { it.zzz_uid.isNotEmpty() }
                else -> _itemList.filter { !it.genshin_uid.contains("-") }
            }

        if (validAccountList.isNotEmpty()) {
            items.addAll(validAccountList)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                vm.sfNoAccount.emit(true)
            }
        }

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (holder.binding) {
            is ItemWidgetConfigAccountBinding -> {
                holder.binding.vm = vm
                holder.binding.item = items[position]

                items[position].run {
                    val uid = vm.getUid(this)
                    val nickname = vm.getNickname(this)

                    holder.binding.tvUid.apply { this.text = uid }
                    holder.binding.tvNickname.apply { this.text = nickname }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    vm.sfSelectedAccount.collect {
                        holder.binding.cb.isChecked = vm.sfSelectedAccount.value == items[position]
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(R.layout.item_widget_config_account, parent)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = items[position].genshin_uid.toLong()

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ItemViewHolder(
        layoutId: Int,
        parent: ViewGroup,
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(binding.root)
}