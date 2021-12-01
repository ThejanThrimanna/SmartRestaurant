package com.thejan.proj.restaurant.tv.smartrestaurant_mobile.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.thejan.proj.restaurant.tablet.android.viewmodel.ViewModelState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

open class BaseViewModel : ViewModel() {
//    var retrofit = ServiceGenerator.getClient()
//    var apiCall = retrofit.create(ApiInterface::class.java)!!
    var state: MutableLiveData<ViewModelState>? = null
    private lateinit var disposables: CompositeDisposable

    var database: FirebaseFirestore? = null

    init {
        initRx()
        defineFireStore()
    }

    private fun defineFireStore() {
        database = FirebaseFirestore.getInstance()
    }

    val message: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    fun updateClient() {
//        retrofit = ServiceGenerator.getClient()
//        apiCall = retrofit.create(ApiInterface::class.java)!!
    }

    private fun initRx() {
        disposables = CompositeDisposable()
    }

    @Synchronized
    fun addDisposable(disposable: Disposable?) {
        if (disposable == null) return
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    fun setMessage(x: HttpException) {
        if (x.response().headers() != null && x.response().headers().get("message") != null) {
            message.postValue(x.response().headers().get("message"))
        } else {
            message.postValue("${x.code()} Error")
        }
    }
}
