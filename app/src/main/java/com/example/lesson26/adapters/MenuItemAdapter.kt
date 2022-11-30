package com.example.lesson26.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson26.callbacks.DiffUtilsMenuCallBack
import com.example.lesson26.databinding.NameItemMenuBinding
import com.example.lesson26.interfaes.MenuNavigationListener
import com.example.lesson26.models.ItemMenu

class MenuItemAdapter(
    private val listenerForFragment: MenuNavigationListener,
) : RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {
    private var menuItemList = listOf<ItemMenu>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding =
            NameItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuItemViewHolder(binding, listenerForFragment)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val menuItem = menuItemList[position]
        holder.bind(menuItem)
    }

    fun setList(listMenu: List<ItemMenu>) {
        val result = DiffUtil.calculateDiff(
            DiffUtilsMenuCallBack(menuItemList, listMenu)
        )

        menuItemList = listMenu
        result.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return menuItemList.size
    }

    class MenuItemViewHolder(
        private val binding: NameItemMenuBinding,
        private val listenerForFragment: MenuNavigationListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val resources = binding.root.context.resources

        fun bind(menuItem: ItemMenu) {

            binding.name.text = resources.getString(menuItem.name)
            binding.image.setImageResource(menuItem.drawableId)

            binding.nameItemMenu.setOnClickListener {
                listenerForFragment.openFragment(menuItem.name)
            }
        }
    }
}