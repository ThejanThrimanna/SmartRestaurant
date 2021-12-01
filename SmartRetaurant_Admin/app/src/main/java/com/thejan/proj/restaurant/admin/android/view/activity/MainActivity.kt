package com.thejan.proj.restaurant.admin.android.view.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.SharedPref
import com.thejan.proj.restaurant.admin.android.helper.USER_ROLE_ADMIN
import com.thejan.proj.restaurant.admin.android.helper.USER_ROLE_CASHIER
import com.thejan.proj.restaurant.admin.android.helper.USER_ROLE_CHEF
import com.thejan.proj.restaurant.admin.android.view.fragment.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        initView()
    }

    private fun initView() {
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        loadFragment(HomeFragment.newInstance())
        val mbottomNavigationMenuView = bottom_navigation.getChildAt(0) as BottomNavigationMenuView

        bottom_navigation.inflateMenu(R.menu.bottom_navigation_menu)

        if(SharedPref.getString(SharedPref.USER_ROLE, "")== USER_ROLE_CASHIER){
            bottom_navigation.menu.removeItem(R.id.bottom_navigation_admin)
        }else if(SharedPref.getString(SharedPref.USER_ROLE, "")== USER_ROLE_CHEF){
            bottom_navigation.menu.removeItem(R.id.bottom_navigation_admin)
            bottom_navigation.menu.removeItem(R.id.bottom_navigation_home)
            bottom_navigation.selectedItemId = R.id.bottom_navigation_food
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flContent, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val fragment: Fragment
            when (item.itemId) {
                R.id.bottom_navigation_home -> {
                    fragment = HomeFragment.newInstance()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_navigation_food -> {
                    fragment = FoodFragment.newInstance()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_navigation_admin -> {
                    fragment = AdminFragment.newInstance()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_navigation_account -> {
                    fragment = AccountFragment.newInstance()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    override fun onBackPressed() {
    }
}