package danggai.app.presentation.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import danggai.app.presentation.R
import danggai.app.presentation.databinding.ItemMainAccountBinding
import danggai.app.presentation.util.log
import danggai.domain.db.account.entity.Account

class MainAdapter(
    val vm: MainViewModel
): RecyclerView.Adapter<MainAdapter.ItemViewHolder>() {

    private var items: MutableList<Account> = arrayListOf()

    fun setItemList(_itemList: List<Account>) {
        log.e(_itemList.size)
        items.clear()

        if (_itemList.isNotEmpty()) {
            _itemList.apply {
                items.addAll(this)
            }
        }

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (holder.binding) {
            is ItemMainAccountBinding -> {
                holder.binding.vm = vm
                holder.binding.item = items[position]

                holder.binding.tvUid.apply {
                    this.text =
                        if (!items[position].genshin_uid.contains("-") ) items[position].genshin_uid
                        else "Auto Check In Only"
                }

                holder.binding.tvNickname.apply {
                    this.text = items[position].nickname
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(R.layout.item_main_account, parent)
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