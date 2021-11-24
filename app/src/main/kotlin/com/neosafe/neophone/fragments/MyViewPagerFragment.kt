package com.neosafe.neophone.fragments

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.neosafe.commons.adapters.MyRecyclerViewAdapter
import com.neosafe.commons.extensions.getAdjustedPrimaryColor
import com.neosafe.neophone.activities.SimpleActivity
import com.neosafe.neophone.extensions.config
import com.neosafe.neophone.helpers.Config
import kotlinx.android.synthetic.main.fragment_letters_layout.view.*
import kotlinx.android.synthetic.main.fragment_recents.view.*

abstract class MyViewPagerFragment(context: Context, attributeSet: AttributeSet) : RelativeLayout(context, attributeSet) {
    protected var activity: SimpleActivity? = null

    private lateinit var config: Config

    fun setupFragment(activity: SimpleActivity) {
        config = activity.config
        if (this.activity == null) {
            this.activity = activity

            setupFragment()
            setupColors(config.textColor, config.primaryColor, activity.getAdjustedPrimaryColor())
        }
    }

    fun finishActMode() {
        (fragment_list?.adapter as? MyRecyclerViewAdapter)?.finishActMode()
        (recents_list?.adapter as? MyRecyclerViewAdapter)?.finishActMode()
    }

    abstract fun setupFragment()

    abstract fun setupColors(textColor: Int, primaryColor: Int, adjustedPrimaryColor: Int)

    abstract fun onSearchClosed()

    abstract fun onSearchQueryChanged(text: String)
}
