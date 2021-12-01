package com.thejan.proj.restaurant.admin.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.CURRENCY
import com.thejan.proj.restaurant.admin.android.model.Food
import kotlinx.android.synthetic.main.layout_invoice_item.view.*

/**
 * Created by Thejan Thrimanna on 9/17/21.
 */
class CartItemAdapter(
    private var list: List<Food?>?
) :
    RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {
    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_invoice_item,
                parent,
                false
            ), list
        )
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = getItem(position).name
        holder.count.text = getItem(position).count.toString()
        holder.amount.text = " $CURRENCY " + String.format(
            "%.2f",
            (getItem(position).price!!.toDouble() * getItem(position).count!!.toInt())
        )
    }

    fun setItems(list: List<Food?>?) {
        this.list = list
    }

    fun getItem(pos: Int): Food {
        return list!!.get(pos)!!
    }

    fun getAll(): List<Food?> {
        return list!!
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    class ViewHolder(
        itemView: View, list: List<Food?>?
    ) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.tvName
        val amount = itemView.tvPrice
        val count = itemView.tvQuantity
    }
}


