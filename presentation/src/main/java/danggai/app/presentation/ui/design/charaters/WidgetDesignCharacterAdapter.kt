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
import java.util.Locale

class WidgetDesignCharacterAdapter(
    val vm: WidgetDesignViewModel
) : BaseAdapter() {

    private var items: MutableList<LocalCharacter> = arrayListOf()

    fun setItemList(_itemList: MutableList<LocalCharacter>) {
        log.e(_itemList.size)
        items.clear()

        if (_itemList.isNotEmpty()) {
            _itemList.apply {
                sortBy { -it.id }
                sortBy { -it.rarity }

                if (this.isNotEmpty() && this[0].rarity >= 105) {  // 콜라보캐릭 맨뒤로
                    this.add(this[0])
                    this.removeFirst()
                }

                items.addAll(this)
            }
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

        bindCharacterData(holder, position)

        holder.binding.llRoot.setOnClickListener {
            items[position].isSelected = !items[position].isSelected
            vm.onClickCharacterItem(items[position])
            notifyDataSetChanged()
        }

        return holder.view
    }

    private fun bindCharacterData(holder: ItemViewHolder, position: Int) {
        holder.binding.vm = vm
        holder.binding.item = items[position]

        // 각 아이템의 선택 상태를 동기화
        updateSelectionState(position)

        setIcon(holder, position)
        setName(holder, position)
        setElement(holder, position)
        setRarity(holder, position)
    }

    private fun updateSelectionState(position: Int) {
        items[position].isSelected = vm.selectedCharacterIdList.contains(items[position].id)
    }

    private fun setIcon(holder: ItemViewHolder, position: Int) {
        holder.binding.ivIcon.setImageResource(items[position].icon)
    }
    
    private fun setName(holder: ItemViewHolder, position: Int) {
        holder.binding.tvName.text = when (PreferenceManager.getString(
            holder.view.context,
            Constant.PREF_LOCALE,
            Locale.getDefault().language
        )) {
            Constant.Locale.ENGLISH.locale -> items[position].name_en
            Constant.Locale.KOREAN.locale -> items[position].name_ko
            else -> items[position].name_en
        }
    }

    private fun setElement(holder: ItemViewHolder, position: Int) {
        holder.binding.ivElement.setImageResource(
            when (items[position].element) {
                Elements.PYRO -> R.drawable.icon_element_pyro
                Elements.HYDRO -> R.drawable.icon_element_hydro
                Elements.ELECTRO -> R.drawable.icon_element_electro
                Elements.CYRO -> R.drawable.icon_element_cyro
                Elements.ANEMO -> R.drawable.icon_element_anemo
                Elements.GEO -> R.drawable.icon_element_geo
                Elements.DENDRO -> R.drawable.icon_element_dendro
            }
        )
    }

    private fun setRarity(holder: ItemViewHolder, position: Int) {
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