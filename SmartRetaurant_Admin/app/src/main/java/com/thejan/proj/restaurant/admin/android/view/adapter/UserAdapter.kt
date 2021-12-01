package com.thejan.proj.restaurant.admin.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.model.AdminUser
import kotlinx.android.synthetic.main.layout_user_item.view.*

class UserAdapter(
    private var list: List<AdminUser>
) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    var listner: ClickSubItem? = null
    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_user_item,
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

        holder.name.text = getItem(position).name
        holder.tvEid.text = getItem(position).emp_id
        holder.tvRole.text = getItem(position).role
        if(getItem(position).pin.isNullOrEmpty()){
            holder.tvPin.text = "N/A"
        }else {
            holder.tvPin.text = getItem(position).pin
        }
        holder.tvPassword.text = getItem(position).password

    }

    fun setItems(list: List<AdminUser>) {
        this.list = list
    }

    fun setClick(listner: ClickSubItem) {
        this.listner = listner
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    fun getItem(pos: Int): AdminUser {
        return list.get(pos)
    }


    class ViewHolder(
        itemView: View, list: List<AdminUser>, listner: ClickSubItem
    ) : RecyclerView.ViewHolder(itemView) {

        val remove = itemView.btnRemove
        val edit = itemView.btnEdit
        val name = itemView.tvName
        val tvEid = itemView.tvEid
        val tvRole = itemView.tvRole
        val tvPin = itemView.tvPin
        val tvPassword = itemView.tvPassword

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


