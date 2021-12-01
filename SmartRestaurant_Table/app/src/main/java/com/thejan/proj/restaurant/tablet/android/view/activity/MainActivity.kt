package com.thejan.proj.restaurant.tablet.android.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thejan.proj.restaurant.tablet.android.R
import com.thejan.proj.restaurant.tablet.android.helper.RE_ORDER
import com.thejan.proj.restaurant.tablet.android.view.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_cart_count.*

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        init()
    }

    /**
     * MainActivity Initiated here
     */
    private fun init() {
        initView()
    }

    /**
     * Initiate the view to the Main Activity
     * Bottom navigation bar define and setup
     */
    private fun initView() {
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        loadFragment(HomeFragment.newInstance())

        val mbottomNavigationMenuView = bottom_navigation.getChildAt(0) as BottomNavigationMenuView
        var notificationsBadge: View? = null
        notificationsBadge = LayoutInflater.from(this).inflate(
            R.layout.layout_cart_count,
            mbottomNavigationMenuView, false
        )
        bottom_navigation?.addView(notificationsBadge)
    }

    /**
     * FragmentTransaction defined here for the bottom navigation bar tabs
     */
    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flContent, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**
     * Bottom Navigation bar navigation defines here. Item selection overrided
     */
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val fragment: Fragment
            when (item.itemId) {
                R.id.bottom_navigation_home -> {
                    fragment = HomeFragment.newInstance()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_navigation_orders -> {
                    fragment = OrderFragment.newInstance()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_navigation_cart -> {
                    fragment = CartFragment.newInstance()
                    loadFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_navigation_offers -> {
                    fragment = OfferFragment.newInstance()
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

    /**
     * Back press from main activity disabled by removing supre
     */
    override fun onBackPressed() {

    }

    /**
     * On activity results overrrided here
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RE_ORDER){
            if(resultCode == RESULT_OK){
                bottom_navigation.selectedItemId = R.id.bottom_navigation_cart
            }
        }
    }

}
