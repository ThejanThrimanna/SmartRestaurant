package com.thejan.proj.restaurant.admin.android.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.AdminUser

/**
 * Created by Thejan Thrimanna on 9/19/21.
 */
class AddUpdateUserViewModel : BaseViewModel() {

    val users: MutableLiveData<ArrayList<AdminUser>> by lazy {
        MutableLiveData<ArrayList<AdminUser>>()
    }

    val roles: MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>()
    }

    init {
        state = MutableLiveData()
    }

    fun getUsers() {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_ADMIN_USERS)
            .get()
            .addOnSuccessListener { documents ->
                val userList = ArrayList<AdminUser>()
                for (doc in documents.documents) {
                    val user = AdminUser(
                        doc.getString(COLUMN_NAME),
                        doc.getString(COLUMN_USER_ROLE),
                        doc.getString(COLUMN_EMP_ID),
                        doc.getString(COLUMN_PASSWORD),
                        doc.getString(COLUMN_PIN)

                    )
                    userList.add(user)
                }
                users.postValue(userList)
                database!!.collection(TABLE_ADMIN_USER_ROLE)
                    .get()
                    .addOnSuccessListener { docs ->
                        val roles = ArrayList<String>()
                        for (doc in docs) {
                            roles.add(doc.getString(COLUMN_ROLE_NAME)!!)
                        }
                        val sortedRoles = roles.sorted()
                        val finalRoles = ArrayList<String>(sortedRoles)
                        this.roles.postValue(finalRoles)
                        state!!.postValue(ViewModelState.sub_success1())
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                state!!.postValue(ViewModelState.error())
            }
    }

    fun addAllData(
        empId: String,
        name: String,
        password: String,
        role: String,
        pin: String = ""
    ) {
        state!!.postValue(ViewModelState.loading())
        val item: MutableMap<String, Any> = HashMap()
        item[COLUMN_EMP_ID] = empId
        item[COLUMN_NAME] = name
        item[COLUMN_PASSWORD] = AppUtils.encrypt(password)!!
        item[COLUMN_PIN] = if (pin.isEmpty()) "" else AppUtils.encrypt(pin)!!
        item[COLUMN_USER_ROLE] = role
        database!!.collection(TABLE_ADMIN_USERS)
            .whereEqualTo(COLUMN_EMP_ID, empId)
            .get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    database!!.collection(TABLE_ADMIN_USERS)
                        .document(empId)
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

    fun updateTable(
        empId: String,
        name: String,
        password: String,
        role: String,
        pin: String = ""
    ) {
        state!!.postValue(ViewModelState.loading())
        database!!.collection(TABLE_ADMIN_USERS)
            .whereEqualTo(COLUMN_EMP_ID, empId)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()) {
                    database!!.collection(TABLE_ADMIN_USERS)
                        .document(it.documents[0].id)
                        .update(
                            mapOf(
                                COLUMN_NAME to name,
                                COLUMN_PASSWORD to AppUtils.encrypt(password),
                                COLUMN_PIN to if(pin.isEmpty()) "" else AppUtils.encrypt(pin),
                                COLUMN_USER_ROLE to role,
                            )
                        )
                        .addOnSuccessListener {
                            state!!.postValue(ViewModelState.sub_success2())
                        }
                }
            }
    }
}