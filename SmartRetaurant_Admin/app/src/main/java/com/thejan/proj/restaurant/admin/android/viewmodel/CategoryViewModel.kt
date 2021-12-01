package com.thejan.proj.restaurant.admin.android.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Category
import com.thejan.proj.restaurant.admin.android.view.adapter.CategoryAdapter

class CategoryViewModel : BaseViewModel() {

    var catAdapter = CategoryAdapter(ArrayList())
    val categories: MutableLiveData<ArrayList<Category>> by lazy {
        MutableLiveData<ArrayList<Category>>()
    }

    init {
        state = MutableLiveData()
    }

    fun getCategories() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_FOOD_CATEGORY)
            .addSnapshotListener { documents, error ->
                if (documents!!.isEmpty) {
                    state!!.postValue(ViewModelState.error())
                } else {
                    val categoryList = ArrayList<Category>()
                    for (doc in documents.documents) {
                        val category = Category(
                            doc.getString(COLUMN_ID),
                            doc.getString(COLUMN_NAME),
                            doc.getString(COLUMN_IMAGE)
                        )
                        categoryList.add(category)
                    }
                    categories.postValue(categoryList)
                    displayCategoryData(categoryList)
                }
            }
    }

    private fun displayCategoryData(response: ArrayList<Category>) {
        if (response.isNullOrEmpty()) {
            setCategoryListIsEmpty()
        } else {
            state!!.postValue(ViewModelState.sub_success1())
            catAdapter.setItems(response)
            catAdapter.notifyDataSetChanged()
        }
    }

    private fun setCategoryListIsEmpty() {
        state!!.postValue(ViewModelState.list_empty())
        catAdapter.setItems(ArrayList())
        catAdapter.notifyDataSetChanged()
    }

    fun removeItem(id: String) {
        database!!.collection(TABLE_FOOD_CATEGORY).whereEqualTo(COLUMN_ID, id).get()
            .addOnSuccessListener { document ->
                database!!.collection(TABLE_FOOD_CATEGORY).document(document.documents[0].id).delete()
            }
    }
}