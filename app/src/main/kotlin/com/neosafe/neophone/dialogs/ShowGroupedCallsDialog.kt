package com.neosafe.neophone.dialogs

import androidx.appcompat.app.AlertDialog
import com.neosafe.commons.activities.BaseSimpleActivity
import com.neosafe.commons.extensions.setupDialogStuff
import com.neosafe.neophone.R
import com.neosafe.neophone.activities.SimpleActivity
import com.neosafe.neophone.adapters.RecentCallsAdapter
import com.neosafe.neophone.helpers.RecentsHelper
import com.neosafe.neophone.models.RecentCall
import kotlinx.android.synthetic.main.dialog_show_grouped_calls.view.*
import java.util.*

class ShowGroupedCallsDialog(val activity: BaseSimpleActivity, callIds: ArrayList<Int>) {
    private var dialog: AlertDialog? = null
    private var view = activity.layoutInflater.inflate(R.layout.dialog_show_grouped_calls, null)

    init {
        view.apply {
            RecentsHelper(activity).getRecentCalls(false) { allRecents ->
                val recents = allRecents.filter { callIds.contains(it.id) }.toMutableList() as ArrayList<RecentCall>
                activity.runOnUiThread {
                    RecentCallsAdapter(activity as SimpleActivity, recents, select_grouped_calls_list, null) {

                    }.apply {
                        select_grouped_calls_list.adapter = this
                    }
                }
            }
        }

        dialog = AlertDialog.Builder(activity)
            .create().apply {
                activity.setupDialogStuff(view, this)
            }
    }
}
