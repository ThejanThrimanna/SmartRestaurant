package com.thejan.proj.restaurant.admin.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.AppUtils
import com.thejan.proj.restaurant.admin.android.helper.FORMAT_1
import com.thejan.proj.restaurant.admin.android.model.Offer
import kotlinx.android.synthetic.main.layout_offer_item.view.*

/**
 * Created by Thejan Thrimanna on 9/24/21.
 */
class OfferAdapter(
    private var list: List<Offer>
) :
    RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    var listner: ClickSubItem? = null
    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_offer_item,
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
        holder.id.text = getItem(position).offerId
        holder.percentage.text = getItem(position).presentage.toString()
        holder.startTime.text = AppUtils.convertTimeInMillisToDateString(FORMAT_1, getItem(position).startDate!!)
        holder.endTime.text = AppUtils.convertTimeInMillisToDateString(FORMAT_1, getItem(position).endDate!!)
    }

    fun setItems(list: List<Offer>) {
        this.list = list
    }

    fun setClick(listner: ClickSubItem) {
        this.listner = listner
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    fun getItem(pos: Int): Offer {
        return list.get(pos)
    }


    class ViewHolder(
        itemView: View, list: List<Offer>, listner: ClickSubItem
    ) : RecyclerView.ViewHolder(itemView) {

        val remove = itemView.btnRemove
        val edit = itemView.btnEdit
        val id = itemView.tvOfferId
        val name = itemView.tvName
        val percentage = itemView.tvPercentage
        val startTime = itemView.tvStartTime
        val endTime = itemView.tvEndTIme

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


