package com.thejan.proj.restaurant.admin.android.view.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Food
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

        AppUtils.loadImagePicaso(
            getItem(position).image!!,
            holder.clotheImage
        )
        holder.name.text = getItem(position).name
        holder.price.text = CURRENCY + " " + String.format("%.2f", getItem(position).price!!.toDouble())

        if (SharedPref.getString(SharedPref.USER_ROLE, "") == USER_ROLE_ADMIN) {
            holder.edit.visibility = View.VISIBLE
            holder.delete.visibility = View.VISIBLE
            holder.isActive.visibility = View.VISIBLE
        } else if(SharedPref.getString(SharedPref.USER_ROLE, "") == ROLE_CHEF){
            holder.edit.visibility = View.GONE
            holder.delete.visibility = View.GONE
            holder.isActive.visibility = View.VISIBLE
        }else {
            holder.edit.visibility = View.GONE
            holder.delete.visibility = View.GONE
            holder.isActive.visibility = View.GONE
        }

        if (getItem(position).type == VEG) {
            holder.vegNonVeg.setImageResource(R.drawable.ic_veg)
        } else {
            holder.vegNonVeg.setImageResource(R.drawable.ic_non_veg)
        }

        if(getItem(position).isActive){
            holder.isActive.setBackgroundResource(R.drawable.drawable_button_background)
            holder.isActive.text = IS_ACTIVE_AVAILABLE
        }else{
            holder.isActive.setBackgroundResource(R.drawable.drawable_button_border_only)
            holder.isActive.text = IS_ACTIVE_NOT_AVAILABLE
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
        val clotheImage = itemView.ivImage
        val name = itemView.tvName
        val price = itemView.tvPrice
        val edit = itemView.btnEdit
        val delete = itemView.btnDelete
        val vegNonVeg = itemView.ivVegNonVeg
        val isActive = itemView.btnActive

        init {
            edit.setOnClickListener {
                listner.edit(adapterPosition)
            }

            delete.setOnClickListener {
                listner.remove(adapterPosition)
            }

            isActive.setOnClickListener {
                listner.setActive(adapterPosition)
            }
        }
    }

    interface ClickSubItem {
        fun remove(position: Int)
        fun edit(position: Int)
        fun setActive(position: Int)
    }
}


