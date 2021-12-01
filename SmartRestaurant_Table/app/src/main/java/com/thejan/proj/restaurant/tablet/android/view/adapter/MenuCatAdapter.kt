package com.thejan.proj.restaurant.tablet.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.model.Category
import kotlinx.android.synthetic.main.layout_category_item.view.*

class MenuCatAdapter(private var list: List<Category>) :
    RecyclerView.Adapter<MenuCatAdapter.ViewHolder>() {

    var selectedItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_category_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = getItem(position).name
        AppUtils.loadImageGlide(holder.itemView.context, getItem(position).image!!, holder.image)

        if (selectedItem == position) {
            holder.image.setBackgroundResource(R.color.theme_color)
        } else {
            holder.image.setBackgroundResource(R.color.gray)
        }
    }

    fun setItems(list: List<Category>) {
        this.list = list
    }

    fun setCurrentSelectedItem(pos: Int) {
        this.selectedItem = pos
    }

    fun getItem(pos: Int): Category {
        return list.get(pos)
    }

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.tvName
        var image = itemView.ivImage
    }

}