package com.thejan.proj.restaurant.admin.android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

open class BaseViewModel : ViewModel() {
    var state: MutableLiveData<ViewModelState>? = null
    private lateinit var disposables: CompositeDisposable

    var database: FirebaseFirestore? = null
    var storage: StorageReference? = null

    init {
        initRx()
        defineFireStore()
    }

    private fun defineFireStore() {
        database = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance().reference
    }

    val message: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
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
