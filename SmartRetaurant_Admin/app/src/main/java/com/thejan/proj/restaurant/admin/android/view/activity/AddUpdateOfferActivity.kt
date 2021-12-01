package com.thejan.proj.restaurant.admin.android.view.activity

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Offer
import com.thejan.proj.restaurant.admin.android.viewmodel.AddUpdateOfferViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_add_update_offer.*
import kotlinx.android.synthetic.main.activity_add_update_offer.tvTitle
import java.util.*

class AddUpdateOfferActivity : BaseActivity() {

    private lateinit var mViewModel: AddUpdateOfferViewModel
    private lateinit var messageFromResponse: String
    private lateinit var offerResponse: ArrayList<Offer>
    private var isAdd: Boolean = true
    private var currentOffer: Offer? = null

    private var selectedStartTime: Long = 0L
    private var selectedEndTime: Long = 0L

    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var offers = Observer<ArrayList<Offer>> { value ->
        offerResponse = value!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update_offer)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initAction()
        initData()
    }

    private fun initAction() {
        etStartDate.setOnClickListener {
            datePicker(R.id.etStartDate)
        }

        etEndDate.setOnClickListener {
            datePicker(R.id.etEndDate)
        }

        btnSave.setOnClickListener {
            if (isAdd) {
                if (etOfferName.text.toString().isNotEmpty() && etDiscountPrecent.text.toString()
                        .isNotEmpty() && etStartDate.text.toString()
                        .isNotEmpty() && etEndDate.text.toString()
                        .isNotEmpty()
                ) {

                    var timeSlotAvailable = true

                    for (rs in offerResponse) {
                        if ((rs.startDate!! < selectedStartTime && rs.endDate!! > selectedStartTime) || (rs.startDate!! < selectedEndTime && rs.endDate!! > selectedEndTime)) {
                            timeSlotAvailable = false
                        }
                    }
                    if (timeSlotAvailable) {
                        mViewModel.addAllData(
                            etOfferName.text.toString(),
                            etDiscountPrecent.text.toString().toInt(),
                            selectedStartTime,
                            selectedEndTime
                        )
                    } else {
                        showToast(getString(R.string.please_select_different_time_slot))
                    }
                } else {
                    showToast(getString(R.string.please_provide_all_details))
                }
            } else {
                if (etOfferName.text.toString().isNotEmpty() && etDiscountPrecent.text.toString()
                        .isNotEmpty() && etStartDate.text.toString()
                        .isNotEmpty() && etEndDate.text.toString()
                        .isNotEmpty()
                ) {
                    mViewModel.updateOffer(
                        currentOffer!!.offerId!!,
                        etOfferName.text.toString(),
                        etDiscountPrecent.text.toString().toInt(),
                        selectedStartTime,
                        selectedEndTime
                    )
                } else {
                    showToast(getString(R.string.please_make_a_change_to_update))
                }
            }
        }

    }

    private fun initData() {
        mViewModel.getOffers()
        isAdd = intent.getBooleanExtra(IS_ADD, true)
        currentOffer = intent.getParcelableExtra(OBJECT)
        if (!isAdd) {
            btnSave.text = getString(R.string.update)
            tvTitle.text = getString(R.string.update_offer)
            etOfferName.setText(currentOffer!!.name)
            etDiscountPrecent.setText(currentOffer!!.presentage.toString())
            etStartDate.setText(
                AppUtils.convertTimeInMillisToDateString(
                    FORMAT_4,
                    currentOffer!!.startDate!!
                )
            )
            etEndDate.setText(
                AppUtils.convertTimeInMillisToDateString(
                    FORMAT_4,
                    currentOffer!!.endDate!!
                )
            )
            selectedStartTime = currentOffer!!.startDate!!
            selectedEndTime = currentOffer!!.endDate!!
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AddUpdateOfferViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.offers.observe(this, offers)
        mViewModel.state!!.observe(this, Observer<ViewModelState> {
            it?.let {
                update(it)
            }
        })
    }

    private fun update(state: ViewModelState) {
        when (state.status) {
            Status.LOADING -> {
                showProgress()
            }
            Status.SUB_SUCCESS1 -> {
                hideProgress()
            }
            Status.SUB_SUCCESS2 -> {
                hideProgress()
                showToast(getString(R.string.successfully_updated))
                finish()
            }
            Status.SUCCESS -> {
                hideProgress()
                showToast(getString(R.string.successfully_offer_added))
                finish()
            }
            Status.ERROR -> {
                hideProgress()
            }
            Status.TIMEOUT -> {
                hideProgress()
            }
            Status.LIST_EMPTY -> {
                hideProgress()
            }
            Status.ITEM_EXISTS -> {
                hideProgress()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }

    private fun datePicker(from: Int) {
        val calendar: Calendar = Calendar.getInstance()
        if (from != R.id.etStartDate) calendar.timeInMillis = selectedStartTime

        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                val date = FORMAT_2
                val formatDate =
                    year.toString() + "/" + (month + 1).toString() + "/" + dayOfMonth.toString()
                timePicker(calendar, formatDate, from)
            }, year, month, dayOfMonth
        )
        calendar.add(Calendar.MONTH, 1)
        val now = System.currentTimeMillis() - 1000
        val maxDate: Long = calendar.getTimeInMillis()
        datePickerDialog.datePicker.minDate = now
        datePickerDialog.datePicker.maxDate = maxDate //After one month from now
        datePickerDialog.show()
    }

    private fun timePicker(calendar: Calendar, date: String, from: Int) {
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)
        val timePickerDialog = RangeTimePickerDialog(
            this,
            { view, hourOfDay, minute ->
                val time = FORMAT_3
                val formatTime = hourOfDay.toString() + ":" + minute.toString()
                val dateTime = "$date $formatTime"

                if (from == R.id.etStartDate) {
                    selectedStartTime = AppUtils.convertStringToMiliSeconds(dateTime, FORMAT_4)
                    etStartDate.setText(dateTime)
                    selectedEndTime = 0L
                    etEndDate.setText("")
                } else {
                    selectedEndTime = AppUtils.convertStringToMiliSeconds(dateTime, FORMAT_4)
                    etEndDate.setText(dateTime)
                }
            }, hour, minute, false
        )
        timePickerDialog.setMin(hour, minute)
        timePickerDialog.show()
    }
}