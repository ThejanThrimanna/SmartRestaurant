package com.thejan.proj.restaurant.admin.android.viewmodel

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Category

class AddUpdateCategoryViewModel : BaseViewModel() {
    val categories: MutableLiveData<ArrayList<Category>> by lazy {
        MutableLiveData<ArrayList<Category>>()
    }

    init {
        state = MutableLiveData()
    }

    fun uploadFile(
        isAdd: Boolean,
        id: String,
        imageUri: Uri,
        name: String
    ) {
        state!!.postValue(ViewModelState.loading())
        if (imageUri != null) {
            val fileReference =
                storage?.child("${System.currentTimeMillis()}")
            fileReference?.putFile(imageUri)?.addOnSuccessListener { task ->
                fileReference.downloadUrl.addOnSuccessListener {
                    if (isAdd)
                        addAllData(it.toString(), name)
                    else
                        getCategoryByName(true, id, name, it.toString())
                }
            }
                ?.addOnFailureListener {
                    state!!.postValue(ViewModelState.loading())
                }
                ?.addOnProgressListener { takeSnapshot ->
                    val progress =
                        (100.0 * takeSnapshot.bytesTransferred / takeSnapshot.totalByteCount)
                }

        } else {
            state!!.postValue(ViewModelState.error())
        }
    }

    private fun addAllData(
        url: String,
        name: String
    ) {
        val item: MutableMap<String, Any> = HashMap()
        item[COLUMN_NAME] = name.toUpperCase()
        item[COLUMN_IMAGE] = url
        item[COLUMN_ID] = System.currentTimeMillis().toString() + name

        database!!.collection(TABLE_MENU)
            .whereEqualTo(COLUMN_NAME, name)
            .get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    database!!.collection(TABLE_FOOD_CATEGORY)
                        .document(System.currentTimeMillis().toString() + name)
                        .set(item)
                        .addOnSuccessListener { documentReference ->
                            Log.d(
                                ContentValues.TAG,
                                "DocumentSnapshot added with ID: $documentReference"
                            )
                            state!!.postValue(ViewModelState.success())
                        }.addOnFailureListener {
                            println(it)
                            state!!.postValue(ViewModelState.error())
                        }
                } else {
                    state!!.postValue(ViewModelState.item_exists())
                }
            }
    }

    fun getCategories() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_FOOD_CATEGORY)
            .get()
            .addOnSuccessListener { documents ->
                val categoryList = ArrayList<Category>()
                for (doc in documents.documents) {
                    val category = Category(
                        doc.getString("id"),
                        doc.getString("name"),
                        doc.getString("image")
                    )
                    categoryList.add(category)
                }
                categories.postValue(categoryList)
                state!!.postValue(ViewModelState.sub_success1())
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    fun getCategoryByName(isImage: Boolean, id: String, name: String, url: String = "") {
        if (!isImage) state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_FOOD_CATEGORY)
            .whereEqualTo(COLUMN_ID, id)
            .get()
            .addOnSuccessListener { documents ->
                if (isImage) {
                    database!!.collection(TABLE_FOOD_CATEGORY)
                        .document(documents.documents[0].id)
                        .update(
                            mapOf(
                                COLUMN_NAME to name,
                                COLUMN_IMAGE to url
                            )
                        )
                        .addOnSuccessListener {
                            state!!.postValue(ViewModelState.sub_success2())
                        }
                } else {
                    database!!.collection(TABLE_FOOD_CATEGORY)
                        .document(documents.documents[0].id)
                        .update(
                            mapOf(
                                COLUMN_NAME to name
                            )
                        )
                        .addOnSuccessListener {
                            state!!.postValue(ViewModelState.sub_success2())
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

}