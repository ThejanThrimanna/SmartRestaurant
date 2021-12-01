package com.thejan.proj.restaurant.admin.android.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.model.Performance
import kotlinx.android.synthetic.main.layout_performance_report_item.view.*
/**
 * Created by Thejan Thrimanna on 8/19/21.
 */
class PerformanceReportAdapter(
    private var list: List<Performance>
) :
    RecyclerView.Adapter<PerformanceReportAdapter.ViewHolder>() {

    var status: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_performance_report_item,
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
        holder.empId.text = getItem(position).emp_id
        holder.name.text = getItem(position).name
        holder.numberofOderders.text = getItem(position).numberOfOrders.toString()
        holder.value.text =  String.format("%.2f", getItem(position).value!!.toDouble())
    }

    fun setItems(list: List<Performance>) {
        this.list = list
    }

    fun setCurrentStatus(status: String) {
        this.status = status
    }

    fun getItem(pos: Int): Performance {
        return list.get(pos)
    }


    class ViewHolder(
        itemView: View, list: List<Performance>,
    ) : RecyclerView.ViewHolder(itemView) {
        val empId = itemView.tvEmpId
        val name = itemView.tvName
        val numberofOderders = itemView.tvNumberOfOrders
        val value = itemView.tvValue
    }
}


