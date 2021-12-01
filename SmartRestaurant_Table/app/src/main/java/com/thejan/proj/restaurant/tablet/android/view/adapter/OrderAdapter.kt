package com.thejan.proj.restaurant.tablet.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.CURRENCY
import com.thejan.proj.restaurant.tablet.android.helper.FORMAT_1
import com.thejan.proj.restaurant.tablet.android.model.Cart
import kotlinx.android.synthetic.main.layout_orde_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Thejan Thrimanna on 9/17/21.
 */
class OrderAdapter(
    private var list: List<Cart?>?
) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_orde_item,
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
        holder.amount.text =
            CURRENCY + " " + String.format("%.2f", (getItem(position).amout!!.toDouble() - getItem(position).discount!!.toDouble()))
        var formatter = SimpleDateFormat(FORMAT_1, Locale.UK)
        val dateString = formatter.format(Date(getItem(position).date!!.toLong()))
        holder.dateAndTime.text = dateString
        holder.orderId.text = getItem(position).cartId
    }

    fun setItems(list: List<Cart?>?) {
        this.list = list
    }

    fun getItem(pos: Int): Cart {
        return list!!.get(pos)!!
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    class ViewHolder(
        itemView: View, list: List<Cart?>?
    ) : RecyclerView.ViewHolder(itemView) {

        val orderId = itemView.tvOrderId
        val amount = itemView.tvAmount
        val dateAndTime = itemView.tvDateTime

    }
}


