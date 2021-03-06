package com.thejan.proj.restaurant.admin.android.helper

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import java.lang.reflect.Field
import java.text.DateFormat
import java.util.*


/**
 * Created by Thejan Thrimanna on 9/24/21.
 */
class RangeTimePickerDialog(
    context: Context?,
    callBack: OnTimeSetListener?,
    hourOfDay: Int,
    minute: Int,
    is24HourView: Boolean
) :
    TimePickerDialog(context, callBack, hourOfDay, minute, is24HourView) {
    private var minHour = -1
    private var minMinute = -1
    private var maxHour = 25
    private var maxMinute = 25
    private var currentHour: Int
    private var currentMinute: Int
    private val calendar: Calendar = Calendar.getInstance()
    private val dateFormat: DateFormat
    fun setMin(hour: Int, minute: Int) {
        minHour = hour
        minMinute = minute
    }

    fun setMax(hour: Int, minute: Int) {
        maxHour = hour
        maxMinute = minute
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        var validTime = true
        if (hourOfDay < minHour || hourOfDay == minHour && minute < minMinute) {
            validTime = false
        }
        if (hourOfDay > maxHour || hourOfDay == maxHour && minute > maxMinute) {
            validTime = false
        }
        if (validTime) {
            currentHour = hourOfDay
            currentMinute = minute
        }
        updateTime(currentHour, currentMinute)
        updateDialogTitle(view, currentHour, currentMinute)
    }

    private fun updateDialogTitle(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val title: String = dateFormat.format(calendar.getTime())
        setTitle(title)
    }

    init {
        currentHour = hourOfDay
        currentMinute = minute
        dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
        try {
            val superclass: Class<*> = javaClass.superclass
            val mTimePickerField: Field = superclass.getDeclaredField("mTimePicker")
            mTimePickerField.isAccessible = true
            val mTimePicker = mTimePickerField.get(this) as TimePicker
            mTimePicker.setOnTimeChangedListener(this)
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalAccessException) {
        }
    }
}