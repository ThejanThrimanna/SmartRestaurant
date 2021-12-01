package com.thejan.proj.restaurant.admin.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.FORMAT_1
import com.thejan.proj.restaurant.admin.android.model.Food
import kotlinx.android.synthetic.main.layout_sale_report_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Thejan Thrimanna on 8/18/21.
 */
class SalesReportAdapter(
    private var list: List<Food>
) :
    RecyclerView.Adapter<SalesReportAdapter.ViewHolder>() {

    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_sale_report_item,
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
        var formatter = SimpleDateFormat(FORMAT_1, Locale.UK)
        val dateString = formatter.format(Date(getItem(position).date!!.toLong()))
        holder.dateTime.text = dateString
        holder.orderId.text = getItem(position).cartId
        holder.itemId.text = getItem(position).foodId
        holder.name.text = getItem(position).name
        holder.quantity.text = getItem(position).count.toString()
        holder.cost.text =
            String.format("%.2f", getItem(position).cost!!.toDouble() * getItem(position).count)
        holder.price.text =
            String.format("%.2f", (getItem(position).price!!.toDouble() * getItem(position).count) - getItem(position).discount!!)
        holder.discount.text =  String.format("%.2f", getItem(position).discount!!)
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
        itemView: View, list: List<Food>,
    ) : RecyclerView.ViewHolder(itemView) {
        val dateTime = itemView.tvDateTime
        val orderId = itemView.tvOrderId
        val itemId = itemView.tvItemId
        val name = itemView.tvName
        val quantity = itemView.tvQuantity
        val cost = itemView.tvCost
        val price = itemView.tvPrice
        val discount = itemView.tvDiscount
    }
}


