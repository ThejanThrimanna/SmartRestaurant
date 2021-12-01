package com.thejan.proj.restaurant.admin.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.model.Category
import kotlinx.android.synthetic.main.layout_category_list_item.view.*
import kotlinx.android.synthetic.main.layout_food_item.view.ivImage
import kotlinx.android.synthetic.main.layout_food_item.view.tvName

class CategoryAdapter(
    private var list: List<Category>
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var listner: ClickSubItem? = null
    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_category_list_item,
                parent,
                false
            ), list, listner!!
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        AppUtils.loadImagePicaso(
            getItem(position).image!!,
            holder.image
        )

        holder.name.text = getItem(position).name
    }

    fun setItems(list: List<Category>) {
        this.list = list
    }

    fun setClick(listner: ClickSubItem) {
        this.listner = listner
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    fun getItem(pos: Int): Category {
        return list.get(pos)
    }


    class ViewHolder(
        itemView: View, list: List<Category>, listner: ClickSubItem
    ) : RecyclerView.ViewHolder(itemView) {

        val remove = itemView.btnRemove
        val edit = itemView.btnEdit
        val image = itemView.ivImage
        val name = itemView.tvName

        init {
            remove.setOnClickListener {
                listner.remove(adapterPosition)
            }
            edit.setOnClickListener {
                listner.edit(adapterPosition)
            }
        }
    }

    interface ClickSubItem {
        fun remove(position: Int)
        fun edit(position: Int)
    }
}


