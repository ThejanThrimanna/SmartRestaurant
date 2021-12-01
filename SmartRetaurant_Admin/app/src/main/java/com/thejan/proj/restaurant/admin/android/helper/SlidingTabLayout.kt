package com.thejan.proj.restaurant.admin.android.helper

/**
 * Created by Adrian on 9/8/15.
 */
/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager

/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 *
 *
 * To use the component, simply add it to your view hierarchy. Then in your
 * [android.app.Activity] or [android.support.v4.app.Fragment] call
 * [.setViewPager] providing it the ViewPager this layout is being used for.
 *
 *
 * The colors can be customized in two ways. The first and simplest is to provide an array of colors
 * via [.setSelectedIndicatorColors] and . The
 * alternative is via the [TabColorizer] interface which provides you complete control over
 * which color is used for any individual position.
 *
 *
 * The views used as tabs can be customized by calling [.setCustomTabView],
 * providing the layout ID of your custom layout.
 */
class SlidingTabLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : HorizontalScrollView(context, attrs, defStyle) {

    private val mTitleOffset: Int

    private var mTabViewLayoutId: Int = 0
    private var mTabViewTextViewId: Int = 0
    private var mTabViewImageViewId: Int = 0
    private var mDistributeEvenly: Boolean = false
    private var isMainMenu: Boolean = false

    private var mViewPager: ViewPager? = null
    //private SparseArray<String> mContentDescriptions = new SparseArray<>();
    private var mViewPagerPageChangeListener: ViewPager.OnPageChangeListener? = null

    private val mTabStrip: SlidingTabStrip

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * [//setCustomTabColorizer(com.google.samples.apps.iosched.ui.widget.SlidingTabLayout.TabColorizer)][.].
     */
    interface TabColorizer {

        /**
         * @return return the color of the indicator used when `position` is selected.
         */
        fun getIndicatorColor(position: Int): Int

    }

    interface TabIconProvider {
        fun getPageIconResId(position: Int): Int
    }

    init {

        // Disable the Scroll Bar
        isHorizontalScrollBarEnabled = false
        // Make sure that the Tab Strips fills this View
        isFillViewport = true

        mTitleOffset = (TITLE_OFFSET_DIPS * resources.displayMetrics.density).toInt()

        mTabStrip = SlidingTabStrip(context)

        addView(mTabStrip, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    /**
     * Set the custom [//com.google.samples.apps.iosched.ui.widget.SlidingTabLayout.TabColorizer] to be used.
     *
     * If you only require simple custmisation then you can use
     * [.setSelectedIndicatorColors] to achieve
     * similar effects.
     */
    fun setCustomTabColorizer(tabColorizer: TabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer)
    }

    fun setDistributeEvenly(distributeEvenly: Boolean) {
        mDistributeEvenly = distributeEvenly
    }

    fun setMainMenu(isMainMenu: Boolean) {
        this.isMainMenu = isMainMenu
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    fun setSelectedIndicatorColors(vararg colors: Int) {
        mTabStrip.setSelectedIndicatorColors(*colors)
    }

    fun setTitleTextColor(color: Int) {
        for (i in 0 until mTabStrip.childCount) {
            if (mTabStrip.getChildAt(i) != null && TextView::class.java.isInstance(mTabStrip.getChildAt(i))) {
                (mTabStrip.getChildAt(i) as TextView).setTextColor(color)
            }
        }
    }

    /**
     * Set the [android.support.v4.view.ViewPager.OnPageChangeListener]. When using [//com.google.samples.apps.iosched.ui.widget.SlidingTabLayout] you are
     * required to set any [android.support.v4.view.ViewPager.OnPageChangeListener] through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see android.support.v4.view.ViewPager.setOnPageChangeListener
     */
    fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        mViewPagerPageChangeListener = listener
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param layoutResId Layout id to be inflated
     * @param textViewId id of the [android.widget.TextView] in the inflated view
     */
    fun setCustomTabView(layoutResId: Int, imageViewId :Int, textViewId: Int) {
        mTabViewLayoutId = layoutResId
        mTabViewImageViewId = imageViewId
        mTabViewTextViewId = textViewId
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    fun setViewPager(viewPager: ViewPager?) {
        mTabStrip.removeAllViews()

        mViewPager = viewPager
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(InternalViewPagerListener())
            populateTabStrip()
        }
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * [.setCustomTabView].
     */
    protected fun createDefaultTabView(context: Context, ignorePadding: Boolean): TextView {
        val textView = TextView(context)
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP.toFloat())
        textView.typeface = Typeface.DEFAULT_BOLD
        val llp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        llp.gravity = Gravity.CENTER
        textView.layoutParams = llp

        val outValue = TypedValue()
        getContext().theme.resolveAttribute(android.R.attr.selectableItemBackground,
                outValue, true)
        textView.setBackgroundResource(outValue.resourceId)
        textView.setAllCaps(true)


        if (!ignorePadding) {
            val padding = (TAB_VIEW_PADDING_DIPS * resources.displayMetrics.density).toInt()
            //            textView.setPadding(padding, padding, padding, padding);
            textView.setPadding(padding, 0, padding, 0)
        }

        return textView
    }

    private fun populateTabStrip() {
        val adapter = mViewPager!!.adapter
        val tabClickListener = TabClickListener()

        for (i in 0 until adapter!!.count) {
            var tabView: View? = null
            var tabTitleView: View? = null
            var imageView: View? = null

            if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(context).inflate(mTabViewLayoutId, mTabStrip,
                        false)
                tabTitleView = tabView!!.findViewById(mTabViewTextViewId)
                imageView = tabView!!.findViewById(mTabViewImageViewId)
            }

            if (tabView == null) {
                tabView = createDefaultTabView(context, TextUtils.isEmpty(adapter.getPageTitle(i)))
                //     tabView = createDefaultTabView(getContext(), true);
            }

            if (tabTitleView == null && TextView::class.java.isInstance(tabView)) {
                tabTitleView = tabView
            }

            if (mDistributeEvenly) {
                val lp = tabView.layoutParams as LinearLayout.LayoutParams
                lp.width = 0
                lp.weight = 1f
            }

            if (isMainMenu && (i == 3 || i == 4)) {
                val lp = tabView.layoutParams as LinearLayout.LayoutParams
                lp.width = 0
                lp.height = 0
            }

            if (TextView::class.java.isInstance(tabTitleView)) {
                (tabTitleView as TextView).text = adapter.getPageTitle(i)
                val tabIconProvider = adapter as TabIconProvider?
                val icon = ContextCompat.getDrawable(context, tabIconProvider!!.getPageIconResId(i))
                icon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                imageView?.background = icon

            } else if (ImageView::class.java.isInstance(tabTitleView) && adapter is TabIconProvider) {

                val tabIconProvider = adapter as TabIconProvider?
                val icon = ContextCompat.getDrawable(context, tabIconProvider!!.getPageIconResId(i))
                icon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

                tabTitleView?.background = icon


                // adding titles as descriptions
                val desc = adapter.getPageTitle(i)
                if (desc != null)
                    tabView.contentDescription = desc

            }

            tabView.setOnClickListener(tabClickListener)


            /*  String desc = mContentDescriptions.get(i, null);
            if (desc != null) {
                tabView.setContentDescription(desc);
            } */

            mTabStrip.addView(tabView)
            if (i == mViewPager!!.currentItem) {
                tabView.isSelected = true
            }


        }
    }

    /*  public void setContentDescription(int i, String desc) {
        mContentDescriptions.put(i, desc);
    } */

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (mViewPager != null) {
            scrollToTab(mViewPager!!.currentItem, 0)
        }
    }

    private fun scrollToTab(tabIndex: Int, positionOffset: Int) {
        val tabStripChildCount = mTabStrip.childCount
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return
        }

        val selectedChild = mTabStrip.getChildAt(tabIndex)
        if (selectedChild != null) {
            var targetScrollX = selectedChild.left + positionOffset

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset
            }

            scrollTo(targetScrollX, 0)
        }
    }

    private inner class InternalViewPagerListener : ViewPager.OnPageChangeListener {
        private var mScrollState: Int = 0

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val tabStripChildCount = mTabStrip.childCount
            if (tabStripChildCount == 0 || position < 0 || position >= tabStripChildCount) {
                return
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset)

            val selectedTitle = mTabStrip.getChildAt(position)
            val extraOffset = if (selectedTitle != null)
                (positionOffset * selectedTitle.width).toInt()
            else
                0
            scrollToTab(position, extraOffset)

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageScrolled(position, positionOffset,
                        positionOffsetPixels)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            mScrollState = state

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageScrollStateChanged(state)
            }
        }

        override fun onPageSelected(position: Int) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f)
                scrollToTab(position, 0)
            }
            for (i in 0 until mTabStrip.childCount) {
                mTabStrip.getChildAt(i).isSelected = position == i
            }
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener!!.onPageSelected(position)
            }
        }

    }

    private inner class TabClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            for (i in 0 until mTabStrip.childCount) {
                if (v === mTabStrip.getChildAt(i)) {
                    mViewPager!!.currentItem = i
                    return
                }
            }
        }
    }

    companion object {

        private const val TITLE_OFFSET_DIPS = 24
        private const val TAB_VIEW_PADDING_DIPS = 16
        private const val TAB_VIEW_TEXT_SIZE_SP = 12
    }

}
