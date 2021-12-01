package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.thejan.proj.restaurant.tablet.android.helper.AppUtils
import com.thejan.proj.restaurant.tablet.android.helper.RecyclerItemClickListenr
import com.thejan.proj.restaurant.tablet.android.viewmodel.Status
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.R
import com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel.ReserveTableViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_reserver_table.*
import kotlinx.android.synthetic.main.activity_reserver_table.toolbar

class ReserveTableActivity : BaseActivity() {

    private lateinit var mViewModel: ReserveTableViewModel
    private lateinit var messageFromResponse: String

    var message = Observer<String> { newName ->
        messageFromResponse = newName!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserver_table)
        init()
    }

    private fun init(){
        iniView()
        initAction()
        initViewModel()
        initSubscription()
        initRecyclerView()
    }

    private fun iniView() {
        toolbar.title = getString(R.string.reserve_a_table)
    }

    private fun initRecyclerView() {
        rvRecyclerView.setHasFixedSize(true)
        rvRecyclerView.layoutManager = GridLayoutManager(this, 3)
        rvRecyclerView.adapter = mViewModel.adapter
        rvRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListenr(
                this!!,
                rvRecyclerView,
                object : RecyclerItemClickListenr.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        AlertDialog.Builder(this@ReserveTableActivity)
                            .setMessage(getString(R.string.do_you_want_to_reserve_this_table))
                            .setIcon(R.drawable.ic_warning_black_transparent)
                            .setPositiveButton(
                                getString(R.string.yes)
                            ) { dialog, whichButton ->
                                mViewModel.reserveTable(mViewModel.adapter.getItem(position))
                            }
                            .setNegativeButton(getString(R.string.no), null).show()
                            .show()
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                    }
                })
        )
    }

    private fun initAction(){
        btnApply.setOnClickListener {
            if(etNumberOfSeats.text.toString().isNotEmpty()) {
                mViewModel.getTables(etNumberOfSeats.text.toString().toInt())
            }else{
                showToast(getString(R.string.please_enter_the_number_of_seats))
            }
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(ReserveTableViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
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

            Status.SUCCESS -> {
                hideProgress()
                tvReservationNote.text = getString(R.string.please_select_a_table_for_the_reservation)
                tvReservationNote.visibility = View.VISIBLE
            }

            Status.SUB_SUCCESS1 -> {
                hideProgress()
                finish()
                AppUtils.closeActivityLeftToRight(this)
            }

            Status.ERROR -> {
                hideProgress()
                showToast(getString(R.string.somthing_went_wrong))
            }

            Status.LIST_EMPTY -> {
                hideProgress()
                tvReservationNote.text = getString(R.string.no_table_is_avilable)
                tvReservationNote.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }
}