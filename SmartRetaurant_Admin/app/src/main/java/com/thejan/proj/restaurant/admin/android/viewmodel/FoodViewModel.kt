package com.thejan.proj.restaurant.admin.android.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Category
import com.thejan.proj.restaurant.admin.android.model.Food
import com.thejan.proj.restaurant.admin.android.view.adapter.FoodAdapter
import com.thejan.proj.restaurant.admin.android.view.adapter.MenuCatAdapter
import kotlin.collections.ArrayList

class FoodViewModel : BaseViewModel() {
    var catAdapter = MenuCatAdapter(ArrayList())
    var itemAdapter = FoodAdapter(ArrayList())
    var currentSelectedCategoryIndex = -1
    var currentSelectedCategory = ""
    var currentCartItems = ArrayList<Food>()

    var allProducts = ArrayList<Food>()
    var allCats = ArrayList<Category>()

    val amount: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val count: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val status: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {
        state = MutableLiveData()
    }

    private fun setCategoryListIsEmpty() {
        state!!.postValue(ViewModelState.list_empty())
        catAdapter.setItems(ArrayList())
        catAdapter.notifyDataSetChanged()
    }

    fun getCategories() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_FOOD_CATEGORY)
            .get().addOnSuccessListener { documents ->
                var catList = ArrayList<Category>()
                var docs = documents.documents
                if (docs.isNotEmpty()) {
                    for (d in docs) {
                        val category =
                            Category(d.getString("id"), d.getString("name"), d.getString("image"))
                        catList.add(category)
                    }

                    if (currentSelectedCategoryIndex == -1) currentSelectedCategoryIndex = 0
                    if (currentSelectedCategory.isNullOrEmpty()) currentSelectedCategory =
                        catList!![0].catID!!
                }
                displayCategoryData(catList)
                allCats.clear()
                allCats.addAll(catList)
                getItems()
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    private fun getItems() {
        database!!.collection(TABLE_MENU)
            .addSnapshotListener { documents, error ->
                var docs = documents!!.documents
                var foodList = ArrayList<Food>()
                if (docs.isNotEmpty()) {
                    for (d in docs) {
                        val filterFromCurrentList =
                            currentCartItems.filter { it.foodId == d.getString("id") }
                        if (filterFromCurrentList.isEmpty()) {
                            val food =
                                Food(
                                    foodId = d.getString("id"),
                                    cat = d.getString("cat"),
                                    cost = d.getDouble("cost"),
                                    desc = d.getString("desc"),
                                    image = d.getString("image"),
                                    name = d.getString("name"),
                                    price = d.getDouble("price"),
                                    type = d.getString("type"),
                                    isAdded = false,
                                    isActive = d.getBoolean(COLUMN_IS_ACTIVE)!!
                                )
                            foodList.add(food)
                        } else {
                            val food =
                                Food(
                                    foodId = d.getString("id"),
                                    cat = d.getString("cat"),
                                    cost = d.getDouble("cost"),
                                    desc = d.getString("desc"),
                                    image = d.getString("image"),
                                    name = d.getString("name"),
                                    price = d.getDouble("price"),
                                    type = d.getString("type"),
                                    count = filterFromCurrentList[0].count,
                                    isAdded = true,
                                    isActive = d.getBoolean(COLUMN_IS_ACTIVE)!!
                                )
                            foodList.add(food)
                        }

                    }

                }
                allProducts.clear()
                allProducts.addAll(foodList)
                displayItemData(allProducts)
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

    fun displayItemData(response: ArrayList<Food>) {
        var filteredList = response.filter { it -> it.cat!! == currentSelectedCategory!! }
        if (filteredList.isNullOrEmpty()) {
            setItemListIsEmpty()
        } else {
            state!!.postValue(ViewModelState.success())
            itemAdapter.setItems(filteredList)
            itemAdapter.notifyDataSetChanged()
        }
    }

    private fun setItemListIsEmpty() {
        state!!.postValue(ViewModelState.list_empty())
        itemAdapter.setItems(ArrayList())
        itemAdapter.notifyDataSetChanged()
    }

    fun removeItem(cartItemId: String) {
        database!!.collection(TABLE_MENU).whereEqualTo(COLUMN_ID, cartItemId).get()
            .addOnSuccessListener { document ->
                database!!.collection(TABLE_MENU).document(document.documents[0].id).delete()
            }
    }

    fun updateIsActive(item: Food, active: Boolean) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_MENU)
            .whereEqualTo(COLUMN_ID, item.foodId)
            .get()
            .addOnSuccessListener { documents ->
                database!!.collection(TABLE_MENU)
                    .document(documents.documents[0].id)
                    .update(
                        mapOf(
                            COLUMN_IS_ACTIVE to active
                        )
                    )
                    .addOnSuccessListener {
                        state!!.postValue(ViewModelState.sub_success2())
                    }

            }
    }

}