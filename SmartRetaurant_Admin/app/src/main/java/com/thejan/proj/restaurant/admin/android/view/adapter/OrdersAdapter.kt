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

class OrdersAdapter(
    private var list: List<Cart>
) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

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
        /* holder.count.text = getItem(position).count.toString()

         AppUtils.loadImagePicaso(
             getItem(position).image!!,
             holder.clotheImage
         )
         holder.name.text = getItem(position).name
         holder.price.text = getItem(position).price.toString()
         if(status == STATUS_PENDING || status == STATUS_PLACE_AN_ORDER) {
             if (getItem(position).isAdded) {
                 holder.addToCart.text = "Remove"
                 holder.addToCart.setBackgroundResource(R.drawable.drawable_button_background)
             } else {
                 holder.addToCart.text = "Add to cart"
                 holder.addToCart.setBackgroundResource(R.drawable.drawable_button_border_only)
             }
             holder.addToCart.setTextColor(holder.itemView.context.getColor(R.color.white))
             holder.addToCart.isEnabled = true
         }else{
             if(getItem(position).isAdded) {
                 holder.addToCart.isEnabled = false
                 holder.addToCart.text = "Added"
                 holder.addToCart.setBackgroundResource(R.drawable.drawable_button_border_only)
                 holder.addToCart.setTextColor(holder.itemView.context.getColor(R.color.green))
                 holder.addToCart.visibility = View.VISIBLE
             }else{
                 holder.addToCart.visibility = View.GONE
             }
         }

         if(status == STATUS_PENDING || status == STATUS_PLACE_AN_ORDER){
             holder.add.visibility = View.VISIBLE
             holder.remove.visibility = View.VISIBLE
         }else{
             holder.add.visibility = View.GONE
             holder.remove.visibility = View.GONE
         }*/
        holder.tableNumber.text = "Table "+ getItem(position).tableNumber
        holder.name.text = getItem(position).name
        holder.btnSettle.visibility = View.GONE
        holder.btnView.visibility = View.GONE

    }

    fun setItems(list: List<Cart>) {
        this.list = list
        notifyDataSetChanged()
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
        val tvAmount = itemView.tvAmount
        val btnSettle = itemView.btnSettle
        val btnView = itemView.btnView

        init {

        }
    }

    interface ClickSubItem {
        fun view(position: Int, status: String)
        fun settle(position: Int, status: String)
    }
}


