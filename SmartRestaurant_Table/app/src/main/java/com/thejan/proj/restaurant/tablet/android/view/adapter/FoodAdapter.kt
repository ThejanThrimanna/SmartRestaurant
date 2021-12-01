package com.thejan.proj.restaurant.tablet.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.*
import com.thejan.proj.restaurant.tablet.android.model.Food
import kotlinx.android.synthetic.main.layout_food_item.view.*

class FoodAdapter(
    private var list: List<Food>
) :
    RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    var listner: ClickSubItem? = null
    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_food_item,
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
        holder.count.text = getItem(position).count.toString()

        AppUtils.loadImageGlide(
            holder.itemView.context,
            getItem(position).image!!,
            holder.clotheImage
        )
        holder.name.text = getItem(position).name
        holder.price.text = CURRENCY + " " + String.format("%.2f", getItem(position).price!!.toDouble())
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
                holder.count.visibility = View.VISIBLE
            }else{
                holder.addToCart.visibility = View.GONE
                holder.count.visibility = View.GONE
            }
        }

        if(status == STATUS_PENDING || status == STATUS_PLACE_AN_ORDER){
            holder.add.visibility = View.VISIBLE
            holder.remove.visibility = View.VISIBLE
        }else{
            holder.add.visibility = View.GONE
            holder.remove.visibility = View.GONE
        }

        if(getItem(position).type == VEG){
            holder.vegNonVeg.setImageResource(R.drawable.ic_veg)
        }else{
            holder.vegNonVeg.setImageResource(R.drawable.ic_non_veg)
        }
    }

    fun setItems(list: List<Food>) {
        this.list = list
    }

    fun setClick(listner: ClickSubItem) {
        this.listner = listner
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    fun getItem(pos: Int): Food {
        return list.get(pos)
    }


    class ViewHolder(
        itemView: View, list: List<Food>, listner: ClickSubItem
    ) : RecyclerView.ViewHolder(itemView) {

        val add = itemView.ivAdd
        val remove = itemView.ivRemove
        val count = itemView.tvCount
        val addToCart = itemView.btnAddToCart
        val modifyItem = itemView.btnModifyItem
        val clotheImage = itemView.ivImage
        val name = itemView.tvName
        val price = itemView.tvPrice
        val vegNonVeg = itemView.ivVegNonVeg

        init {
            add.setOnClickListener {
                listner.add(adapterPosition)
            }
            remove.setOnClickListener {
                listner.remove(adapterPosition)
            }

            addToCart.setOnClickListener {
                listner.clickAddToCart(adapterPosition)
            }

            modifyItem.setOnClickListener {
                listner.clickModifyItem(adapterPosition)
            }
        }
    }

    interface ClickSubItem {
        fun clickAddToCart(position: Int)
        fun changeCount(position: Int, count: Int, added: Boolean)
        fun clickModifyItem(position: Int)
        fun add(position: Int)
        fun remove(position: Int)
    }
}


