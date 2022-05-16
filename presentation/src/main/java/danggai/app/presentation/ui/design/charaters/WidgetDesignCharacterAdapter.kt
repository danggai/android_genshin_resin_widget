package danggai.app.presentation.ui.design.charaters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import danggai.app.presentation.R
import danggai.app.presentation.databinding.ItemCharacterBinding
import danggai.app.presentation.ui.design.WidgetDesignViewModel
import danggai.domain.network.character.entity.Avatar

class WidgetDesignCharacterAdapter(val vm: WidgetDesignViewModel) :
    BaseAdapter() {

    private var items: MutableList<Avatar> = arrayListOf()

    fun clearData() {
        items.clear()
        notifyDataSetChanged()
    }

    fun setItemList(_itemList: MutableList<Avatar>) {
        items.clear()
        items.addAll(_itemList)
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

            holder.binding.item = items[position]
            holder.binding.vm = vm

            Glide.with(holder.view.context)
                .load(items[position].icon)
                .apply(RequestOptions().fitCenter())
                .into(holder.binding.ivIcon)

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
        } else {
            holder = convertView.tag as ItemViewHolder
        }
        holder.binding.item = items[position]

        return holder.view
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ItemViewHolder internal constructor(binding: ItemCharacterBinding) {
        var view: View = binding.root
        var binding: ItemCharacterBinding = binding
    }
}