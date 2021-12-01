package com.thejan.proj.restaurant.admin.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.model.Table
import kotlinx.android.synthetic.main.layout_table_item.view.*
import kotlinx.android.synthetic.main.layout_user_item.view.btnRemove

/**
 * Created by Thejan Thrimanna on 9/1/21.
 */
class TableAdapter(
    private var list: List<Table>
) :
    RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    var listner: ClickSubItem? = null
    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_table_item,
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
        holder.tablenumber.text = getItem(position).tableNumber
        holder.numberofSeats.text = getItem(position).numberOfSeats.toString()
        holder.mac.text =
            if (getItem(position).deviceId.isNullOrEmpty()) "N/A" else getItem(position).deviceId
        holder.booking.text =
            if (getItem(position).booking.isNullOrEmpty()) "N/A" else getItem(position).booking
    }

    fun setItems(list: List<Table>) {
        this.list = list
    }

    fun setClick(listner: ClickSubItem) {
        this.listner = listner
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    fun getItem(pos: Int): Table {
        return list.get(pos)
    }


    class ViewHolder(
        itemView: View, list: List<Table>, listner: ClickSubItem
    ) : RecyclerView.ViewHolder(itemView) {

        val remove = itemView.btnRemove
        val edit = itemView.btnEdit
        val tablenumber = itemView.tvTableNumber
        val mac = itemView.tvMac
        val numberofSeats = itemView.tvNumberOfSeats
        val booking = itemView.tvBooking

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


