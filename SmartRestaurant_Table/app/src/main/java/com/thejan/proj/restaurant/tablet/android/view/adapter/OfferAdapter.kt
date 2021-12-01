package com.thejan.proj.restaurant.tablet.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.helper.FORMAT_1
import com.thejan.proj.restaurant.tablet.android.helper.FORMAT_3
import com.thejan.proj.restaurant.tablet.android.model.Offer
import kotlinx.android.synthetic.main.layout_offer_item.view.*
import kotlinx.android.synthetic.main.layout_orde_item.view.*

/**
 * Created by Thejan Thrimanna on 9/24/21.
 */
class OfferAdapter(
    private var list: List<Offer?>?
) :
    RecyclerView.Adapter<OfferAdapter.ViewHolder>() {
    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_offer_item,
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
      /*  holder.amount.text =
            CURRENCY + " " + String.format("%.2f", getItem(position).amout!!.toDouble())
        var formatter = SimpleDateFormat(FORMAT_1, Locale.UK)
        val dateString = formatter.format(Date(getItem(position).date!!.toLong()))
        holder.dateAndTime.text = dateString
        holder.orderId.text = getItem(position).cartId*/

        holder.title.text = getItem(position).name
        holder.dec.text = "Place an order between " + AppUtils.convertTimeInMillisToDateString(
            FORMAT_1 ,getItem(position).startDate!!) +" and " +AppUtils.convertTimeInMillisToDateString(
        FORMAT_1 ,getItem(position).endDate!!) +" to get "+getItem(position).presentage+"% dicount!"


    }

    fun setItems(list: List<Offer?>?) {
        this.list = list
    }

    fun getItem(pos: Int): Offer {
        return list!!.get(pos)!!
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    class ViewHolder(
        itemView: View, list: List<Offer?>?
    ) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.tvTitle
        val dec = itemView.tvDec

    }
}


