package com.thejan.proj.restaurant.tv.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.tv.android.R
import com.thejan.proj.restaurant.tv.android.model.Food
import kotlinx.android.synthetic.main.layout_cart_item.view.tvName
import kotlinx.android.synthetic.main.layout_food_item.view.*

class ItemAdapter(
    private var list: List<Food>
) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_food_item,
                parent,
                false
            ), list
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text = getItem(position).name
        holder.count.text = getItem(position).count.toString()
    }

    fun setItems(list: List<Food>) {
        this.list = list
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    fun getItem(pos: Int): Food {
        return list.get(pos)
    }


    class ViewHolder(
        itemView: View, list: List<Food>
    ) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.tvName
        val count = itemView.tvCount


    }


}


