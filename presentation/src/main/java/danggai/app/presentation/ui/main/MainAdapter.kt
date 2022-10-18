package danggai.app.presentation.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import danggai.app.presentation.R
import danggai.app.presentation.databinding.ItemAccountBinding
import danggai.app.presentation.databinding.ItemCharacterBinding
import danggai.app.presentation.ui.design.WidgetDesignViewModel
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.domain.db.account.entity.Account
import danggai.domain.local.Elements
import danggai.domain.local.LocalCharacter
import danggai.domain.util.Constant
import java.util.*

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
            is ItemAccountBinding -> {
                holder.binding.vm = vm
                holder.binding.item = items[position]

                holder.binding.tvUid.apply {
                    this.text = items[position].genshin_uid
                }

                holder.binding.tvNickname.apply {
                    this.text = items[position].nickname
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(R.layout.item_account, parent)
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