package com.thejan.proj.restaurant.tv.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.tv.android.helper.STATUS_PENDING
import com.thejan.proj.restaurant.tv.android.helper.STATUS_PROCESSING
import com.thejan.proj.restaurant.tv.android.R
import com.thejan.proj.restaurant.tv.android.model.Cart
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_cart_item.view.*

class PendingOrderAdapter(
    private var list: List<Cart>
) :
    RecyclerView.Adapter<PendingOrderAdapter.ViewHolder>() {

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

        holder.tableNumber.text = "Table " + getItem(position).tableNumber
        holder.name.text = getItem(position).chefName
        holder.btnAccept.visibility = View.VISIBLE

        holder.rvItems.setHasFixedSize(true)
        holder.rvItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvItems.adapter = ItemAdapter(getItem(position).items!!)
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
        val btnAccept = itemView.btnAccept
        val rvItems = itemView.rvItems

        init {
            btnAccept.setOnClickListener {
                listner.accept(adapterPosition, list[adapterPosition].status!!)
            }
        }
    }

    interface ClickSubItem {
        fun accept(position: Int, status: String)
        fun done(position: Int, status: String)
    }
}


