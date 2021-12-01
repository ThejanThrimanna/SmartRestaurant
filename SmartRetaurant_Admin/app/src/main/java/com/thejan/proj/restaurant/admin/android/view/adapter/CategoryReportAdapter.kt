package com.thejan.proj.restaurant.admin.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.model.CatReport
import kotlinx.android.synthetic.main.layout_category_report_item.view.*

/**
 * Created by Thejan Thrimanna on 8/20/21.
 */
class CategoryReportAdapter(
    private var list: List<CatReport>
) :
    RecyclerView.Adapter<CategoryReportAdapter.ViewHolder>() {

    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_category_report_item,
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
        holder.catId.text = getItem(position).catId
        holder.name.text = getItem(position).name
        holder.quantity.text = getItem(position).quantity.toString()
        holder.value.text = String.format("%.2f", getItem(position).value!!.toDouble())
    }

    fun setItems(list: List<CatReport>) {
        this.list = list
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    fun getItem(pos: Int): CatReport {
        return list.get(pos)
    }


    class ViewHolder(
        itemView: View, list: List<CatReport>,
    ) : RecyclerView.ViewHolder(itemView) {
        val catId = itemView.tvCatId
        val name = itemView.tvName
        val quantity = itemView.tvQuantity
        val value = itemView.tvValue
    }
}


