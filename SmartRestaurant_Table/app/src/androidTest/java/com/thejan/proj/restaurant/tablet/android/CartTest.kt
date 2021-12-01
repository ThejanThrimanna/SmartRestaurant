package com.thejan.proj.restaurant.tablet.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thejan.proj.restaurant.tablet.android.model.Food
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartTest {
    @Test
    fun cartSum(){
        var currentSum = 0.0
        val cartList = ArrayList<Food>()
        currentSum = 0.0

        val food1 = Food(price = 100.0, count = 10)
        val food2 = Food(price = 200.0, count = 20)
        val food3 = Food(price = 300.0, count = 30)
        cartList.add(food1)
        cartList.add(food2)
        cartList.add(food3)

        for (cl in cartList) {
            currentSum += (cl.price!! * cl.count)
            println(currentSum)
        }
        Assert.assertEquals("14000.0", currentSum.toString())
    }
}