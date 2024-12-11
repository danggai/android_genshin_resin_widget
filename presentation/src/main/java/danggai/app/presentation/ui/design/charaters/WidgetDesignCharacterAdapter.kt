package danggai.app.presentation.ui.design.charaters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
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
    private val vm: WidgetDesignViewModel
) : RecyclerView.Adapter<WidgetDesignCharacterAdapter.ItemViewHolder>() {

    init {
        setHasStableIds(true)  // 여기에 넣어서 초기화 시점에 안정적인 ID 설정
    }


    private var items: MutableList<LocalCharacter> = arrayListOf()

    fun setItemList(_itemList: MutableList<LocalCharacter>) {
        log.e(_itemList.size)

        val oldItems = items.toList() // 기존 아이템 리스트 저장
        val oldSize = items.size

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

        // 비교 후 변경된 항목만 갱신
        val newSize = items.size
        if (newSize > oldSize) {
            for (i in oldSize until newSize) {
                notifyItemInserted(i)
            }
        } else if (newSize < oldSize) {
            for (i in newSize until oldSize) {
                notifyItemRemoved(i)
            }
        }

        // 변경된 항목만 갱신
        for (i in 0 until minOf(oldSize, newSize)) {
            if (oldItems[i] != items[i]) {
                notifyItemChanged(i) // 해당 항목만 갱신
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(R.layout.item_character, parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (holder.binding) {
            is ItemCharacterBinding -> {
                holder.binding.vm = vm
                holder.binding.item = items[position]

                bindCharacterData(holder, position)

                holder.binding.llRoot.setOnClickListener {
                    items[position].isSelected = !items[position].isSelected
                    vm.onClickCharacterItem(items[position])
                    notifyItemChanged(position)
                }
            }
        }
    }

    private fun bindCharacterData(holder: ItemViewHolder, position: Int) {
        when (holder.binding) {
            is ItemCharacterBinding -> {
                holder.binding.vm = vm
                holder.binding.item = items[position]

                // 각 아이템의 선택 상태를 동기화
                updateSelectionState(position)

                setIcon(holder.binding, position)
                setName(holder.binding, position)
                setElement(holder.binding, position)
                setRarity(holder.binding, position)
            }
        }
    }

    private fun updateSelectionState(position: Int) {
        items[position].isSelected = vm.selectedCharacterIdList.contains(items[position].id)
    }

    private fun setIcon(binding: ItemCharacterBinding, position: Int) {
        binding.ivIcon.setImageResource(items[position].icon)
    }

    private fun setName(binding: ItemCharacterBinding, position: Int) {
        binding.tvName.text = when (PreferenceManager.getString(
            binding.root.context,
            Constant.PREF_LOCALE,
            Locale.getDefault().language
        )) {
            Constant.Locale.ENGLISH.locale -> items[position].name_en
            Constant.Locale.KOREAN.locale -> items[position].name_ko
            else -> items[position].name_en
        }
    }

    private fun setElement(binding: ItemCharacterBinding, position: Int) {
        binding.ivElement.setImageResource(
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

    private fun setRarity(binding: ItemCharacterBinding, position: Int) {
        when (items[position].rarity) {
            5 -> {
                binding.ivBackground.setImageResource(R.drawable.bg_character_5stars)
                binding.ivStars.setImageResource(R.drawable.icon_5stars)
            }

            4 -> {
                binding.ivBackground.setImageResource(R.drawable.bg_character_4stars)
                binding.ivStars.setImageResource(R.drawable.icon_4stars)
            }

            105 -> {  // 콜라보 5성
                binding.ivBackground.setImageResource(R.drawable.bg_character_collabo)
                binding.ivStars.setImageResource(R.drawable.icon_5stars_collabo)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()  // 고유 ID를 반환
    }

    override fun getItemCount(): Int = items.size

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
