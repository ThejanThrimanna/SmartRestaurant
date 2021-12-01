package com.thejan.proj.restaurant.admin.android.viewmodel

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Category
import com.thejan.proj.restaurant.admin.android.model.Type


class AddUpdateFoodViewModel : BaseViewModel() {
    val categories: MutableLiveData<ArrayList<Category>> by lazy {
        MutableLiveData<ArrayList<Category>>()
    }

    val types: MutableLiveData<ArrayList<Type>> by lazy {
        MutableLiveData<ArrayList<Type>>()
    }

    init {
        state = MutableLiveData()
    }

    fun getCategories() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_FOOD_CATEGORY)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    val categoryList = ArrayList<Category>()
                    for (doc in documents.documents) {
                        val category = Category(
                            doc.getString("id"),
                            doc.getString("name"),
                            doc.getString("image")
                        )
                        categoryList.add(category)
                    }
                    val sortedList = categoryList.sortedBy { it.name }
                    val sortedArrayList = ArrayList<Category>()
                    sortedArrayList.addAll(sortedList)
                    categories.postValue(sortedArrayList)
                    getTypes()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    private fun getTypes() {
        database!!.collection(TABLE_FOOD_TYPE)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    val typeList = ArrayList<Type>()
                    for (doc in documents.documents) {
                        val category = Type(
                            doc.getString("id"),
                            doc.getString("name")
                        )
                        typeList.add(category)
                    }
                    types.postValue(typeList)
                    state!!.postValue(ViewModelState.sub_success1())
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    fun saveData(
        isAdd: Boolean,
        imageUri: Uri,
        name: String,
        desc: String,
        cost: Double,
        price: Double,
        category: String,
        type: String,
        foodId: String = ""
    ) {
        uploadFile(isAdd, imageUri, name, desc, cost, price, category, type, foodId)
    }

    private fun uploadFile(
        isAdd: Boolean,
        imageUri: Uri,
        name: String,
        desc: String,
        cost: Double,
        price: Double,
        category: String,
        type: String,
        foodId: String = ""
    ) {
        state!!.postValue(ViewModelState.loading())
        if (imageUri != null) {
            val fileReference =
                storage?.child("${System.currentTimeMillis()}")
            fileReference?.putFile(imageUri!!)?.addOnSuccessListener { task ->
                fileReference.downloadUrl.addOnSuccessListener {
                    if (isAdd)
                        addAllData(it.toString(), name, desc, cost, price, category, type)
                    else
                        updateFood(
                            true,
                            name,
                            desc,
                            cost,
                            price,
                            category,
                            type,
                            foodId,
                            it.toString()
                        )
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
        name: String,
        desc: String,
        cost: Double,
        price: Double,
        category: String,
        type: String
    ) {
        val item: MutableMap<String, Any> = HashMap()
        item[COLUMN_CATEGORY] = category
        item[COLUMN_NAME] = name
        item[COLUMN_DESC] = desc
        item[COLUMN_COST] = cost
        item[COLUMN_PRICE] = price
        item[COLUMN_TYPE] = type
        item[COLUMN_MENU_ID] = System.currentTimeMillis().toString() + category + type
        item[COLUMN_IMAGE] = url
        item[COLUMN_IS_ACTIVE] = true

        database!!.collection(TABLE_MENU)
            .whereEqualTo(COLUMN_NAME, name)
            .get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    database!!.collection(TABLE_MENU)
                        .document(System.currentTimeMillis().toString() + category + type)
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

    fun updateFood(
        isImage: Boolean,
        name: String,
        desc: String,
        cost: Double,
        price: Double,
        category: String,
        type: String,
        foodId: String,
        url: String = ""
    ) {
        if (!isImage) state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_MENU)
            .whereEqualTo(COLUMN_ID, foodId)
            .get()
            .addOnSuccessListener { documents ->
                if (isImage) {
                    database!!.collection(TABLE_MENU)
                        .document(documents.documents[0].id)
                        .update(
                            mapOf(
                                COLUMN_NAME to name,
                                COLUMN_DESC to desc,
                                COLUMN_COST to cost,
                                COLUMN_PRICE to price,
                                COLUMN_CATEGORY to category,
                                COLUMN_TYPE to type,
                                COLUMN_IMAGE to url
                            )
                        )
                        .addOnSuccessListener {
                            state!!.postValue(ViewModelState.sub_success2())
                        }
                } else {
                    database!!.collection(TABLE_MENU)
                        .document(documents.documents[0].id)
                        .update(
                            mapOf(
                                COLUMN_NAME to name,
                                COLUMN_DESC to desc,
                                COLUMN_COST to cost,
                                COLUMN_PRICE to price,
                                COLUMN_CATEGORY to category,
                                COLUMN_TYPE to type
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