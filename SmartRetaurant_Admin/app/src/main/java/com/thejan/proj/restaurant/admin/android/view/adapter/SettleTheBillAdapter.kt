package com.thejan.proj.restaurant.admin.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Cart
import kotlinx.android.synthetic.main.layout_cart_item.view.*

class SettleTheBillAdapter(
    private var list: List<Cart>
) :
    RecyclerView.Adapter<SettleTheBillAdapter.ViewHolder>() {

    var listner: ClickSubItem? = null
    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_cart_item,
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

        holder.tableNumber.text = "Table "+ getItem(position).tableNumber
        holder.name.text = getItem(position).name
        when (getItem(position).status) {
            STATUS_BILL_SETTLEMENT_PENDING -> {
                holder.btnSettle.visibility = View.VISIBLE
            }
        }

    }

    fun setItems(list: List<Cart>) {
        this.list = list
    }

    fun setClick(listner: ClickSubItem) {
        this.listner = listner
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    fun getItem(pos: Int): Cart {
        return list.get(pos)
    }


    class ViewHolder(
        itemView: View, list: List<Cart>, listner: ClickSubItem
    ) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.tvName
        val tableNumber = itemView.tvTableNumber
        val btnSettle = itemView.btnSettle
        val view = itemView.btnView

        init {
            btnSettle.setOnClickListener {
                listner.settle(adapterPosition, list[adapterPosition].status!!)
            }
            view.setOnClickListener {
                listner.view(adapterPosition, list[adapterPosition].status!!)
            }
        }
    }

    interface ClickSubItem {
        fun view(position: Int, status: String)
        fun settle(position: Int, status: String)
    }
}


