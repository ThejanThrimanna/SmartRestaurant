package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.R
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.model.Table
import kotlinx.android.synthetic.main.layout_table_item.view.*

/**
 * Created by Thejan Thrimanna on 9/1/21.
 */
class TableAdapter(
    private var list: List<Table>
) :
    RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_table_item,
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
        holder.tableNumber.text = getItem(position).tableNumber
        holder.numberOfSeats.text = getItem(position).numberOfSeats.toString() + " Seats"
    }

    fun setItems(list: List<Table>) {
        this.list = list
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    fun getItem(pos: Int): Table {
        return list.get(pos)
    }


    class ViewHolder(
        itemView: View, list: List<Table>
    ) : RecyclerView.ViewHolder(itemView) {

        val tableNumber = itemView.tvTableNumber
        val numberOfSeats = itemView.tvNumberOfSeats

    }

}


