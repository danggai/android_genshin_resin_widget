package danggai.app.presentation.ui.design.charaters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import danggai.app.presentation.R
import danggai.app.presentation.databinding.ItemCharacterBinding
import danggai.app.presentation.ui.design.WidgetDesignViewModel
import danggai.app.presentation.util.PreferenceManager
import danggai.app.presentation.util.log
import danggai.domain.local.Elements
import danggai.domain.local.LocalCharacter
import danggai.domain.util.Constant
import java.util.*

class WidgetDesignCharacterAdapter(val vm: WidgetDesignViewModel) :
    BaseAdapter() {

    private var items: MutableList<LocalCharacter> = arrayListOf()

    fun setItemList(_itemList: MutableList<LocalCharacter>) {
        log.e(_itemList.size)
        items.clear()
        _itemList.apply {
            sortBy { -it.id }
            sortBy { -it.rarity }

            while (this.size >= 1 && this[0].rarity >= 100) {
                add(this[0])
                removeFirst()
            }
            items.addAll(this)
        }
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var holder: ItemViewHolder
        if (convertView == null) {
            val itemBinding: ItemCharacterBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_character,
                parent,
                false
            )
            holder = ItemViewHolder(itemBinding)
            holder.view = itemBinding.root
            holder.view.tag = holder
        } else {
            holder = convertView.tag as ItemViewHolder
        }

        holder.binding.vm = vm
        holder.binding.item = items[position]

        for (character in vm.selectedCharacterIdList) {
            if (character == items[position].id) {
                items[position].isSelected = true
                break
            }
        }

        holder.binding.ivIcon.apply {
            this.setImageResource(items[position].icon)
        }

        holder.binding.tvName.apply {
            this.text =
                when (PreferenceManager.getString(context, Constant.PREF_LOCALE, Locale.getDefault().language)) {
                    Constant.Locale.ENGLISH.locale -> items[position].name_en
                    Constant.Locale.KOREAN.locale -> items[position].name_ko
                    else -> items[position].name_en
                }
        }

        holder.binding.ivElement.apply {
            this.setImageResource(when (items[position].element) {
                Elements.PYRO -> R.drawable.icon_element_pyro
                Elements.HYDRO -> R.drawable.icon_element_hydro
                Elements.ELECTRO -> R.drawable.icon_element_electro
                Elements.CYRO -> R.drawable.icon_element_cyro
                Elements.ANEMO -> R.drawable.icon_element_anemo
                Elements.GEO -> R.drawable.icon_element_geo
            })
        }

        when (items[position].rarity) {
            5 -> {
                holder.binding.ivBackground.setImageResource(R.drawable.bg_character_5stars)
                holder.binding.ivStars.setImageResource(R.drawable.icon_5stars)
            }
            4 -> {
                holder.binding.ivBackground.setImageResource(R.drawable.bg_character_4stars)
                holder.binding.ivStars.setImageResource(R.drawable.icon_4stars)
            }
            105 -> {  // 콜라보 5성
                holder.binding.ivBackground.setImageResource(R.drawable.bg_character_collabo)
                holder.binding.ivStars.setImageResource(R.drawable.icon_5stars_collabo)
            }
        }

        holder.binding.llRoot.setOnClickListener {
            items[position].isSelected = !items[position].isSelected
            vm.onClickCharacterItem(items[position])
            notifyDataSetChanged()
        }

        return holder.view
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int): Long = items[position].id.toLong()

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ItemViewHolder internal constructor(_binding: ItemCharacterBinding) {
        var view: View = _binding.root
        var binding: ItemCharacterBinding = _binding
    }
}