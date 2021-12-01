package com.thejan.proj.restaurant.admin.android.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.model.AdminModel


/**
 * Created by Thejan Thrimanna on 8/27/21.
 */
internal class AdminAdapter(
    private val context: Context,
    private val numbersInWords: ArrayList<AdminModel>
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    override fun getCount(): Int {
        return numbersInWords.size
    }
    override fun getItem(position: Int): Any? {
        return null
    }
    override fun getItemId(position: Int): Long {
        return 0
    }
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.layout_admin_item, null)
        }
        imageView = convertView!!.findViewById(R.id.ivImage)
        textView = convertView.findViewById(R.id.tvName)
        imageView.setImageResource(numbersInWords[position].imgid!!)
        textView.text = numbersInWords[position].itemName
        return convertView
    }
}