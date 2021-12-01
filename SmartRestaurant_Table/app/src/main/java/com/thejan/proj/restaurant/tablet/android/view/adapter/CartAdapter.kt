package com.thejan.proj.restaurant.tablet.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.helper.CURRENCY
import com.thejan.proj.restaurant.tablet.android.helper.STATUS_PENDING
import com.thejan.proj.restaurant.tablet.android.helper.STATUS_PLACE_AN_ORDER
import com.thejan.proj.restaurant.tablet.android.model.Food
import kotlinx.android.synthetic.main.layout_cart_list_item.view.*

/**
 * Created by thejanthrimanna on 2020-09-15.
 */
class CartAdapter(
    private var list: List<Food?>?
) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    var listner: ClickSubItem? = null
    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_cart_list_item,
                parent,
                false
            ), list, listner!!
        )
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.count.text = getItem(position).count.toString()

        AppUtils.loadImageGlide(
            holder.itemView.context,
            getItem(position).image!!,
            holder.image
        )

        holder.name.text = getItem(position).name

        holder.itemTotal.text = " $CURRENCY " + String.format(
            "%.2f",
            (getItem(position).price!!.toDouble() * getItem(position).count!!.toInt())
        )

        if(status == STATUS_PENDING || status == STATUS_PLACE_AN_ORDER) {
            holder.add.visibility = View.VISIBLE
            holder.remove.visibility = View.VISIBLE
            holder.delete.visibility = View.VISIBLE
        }else{
            holder.add.visibility = View.GONE
            holder.remove.visibility = View.GONE
            holder.delete.visibility = View.GONE
        }

    }

    fun setItems(list: List<Food?>?) {
        this.list = list
    }

    fun setClick(listner: ClickSubItem) {
        this.listner = listner
    }

    fun getItem(pos: Int): Food {
        return list!!.get(pos)!!
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    class ViewHolder(
        itemView: View, list: List<Food?>?, listner: ClickSubItem
    ) : RecyclerView.ViewHolder(itemView) {

        val add = itemView.ivAdd
        val remove = itemView.ivRemove
        val count = itemView.tvCount
        val image = itemView.ivImage
        val name = itemView.tvName
        val delete = itemView.btnRemove
        val itemTotal = itemView.tvItemTotal

        init {
            add.setOnClickListener {
                listner.add(adapterPosition)
            }
            remove.setOnClickListener {
                listner.remove(adapterPosition)
            }

            delete.setOnClickListener {
                listner.clickRemoveFromCart(adapterPosition)
            }
        }
    }

    interface ClickSubItem {
        fun clickRemoveFromCart(position: Int)
        fun changeCount(position: Int, count: Int)
        fun add(position: Int)
        fun remove(position: Int)
    }
}


