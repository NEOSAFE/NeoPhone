package com.neosafe.neophone.dialogs

import androidx.appcompat.app.AlertDialog
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.neosafe.commons.extensions.getAdjustedPrimaryColor
import com.neosafe.commons.extensions.getColorStateList
import com.neosafe.commons.extensions.getContrastColor
import com.neosafe.commons.extensions.setupDialogStuff
import com.neosafe.commons.models.SimpleContact
import com.neosafe.neophone.R
import com.neosafe.neophone.activities.SimpleActivity
import com.neosafe.neophone.adapters.ContactsAdapter
import com.neosafe.neophone.extensions.config
import kotlinx.android.synthetic.main.dialog_select_contact.view.*
import java.util.*

class SelectContactDialog(val activity: SimpleActivity, contacts: ArrayList<SimpleContact>, val callback: (selectedContact: SimpleContact) -> Unit) {
    private var dialog: AlertDialog? = null
    private var view = activity.layoutInflater.inflate(R.layout.dialog_select_contact, null)

    init {
        view.apply {
            letter_fastscroller.textColor = context.config.textColor.getColorStateList()
            letter_fastscroller_thumb.setupWithFastScroller(letter_fastscroller)
            letter_fastscroller_thumb.textColor = context.getAdjustedPrimaryColor().getContrastColor()
            letter_fastscroller_thumb.thumbColor = context.getAdjustedPrimaryColor().getColorStateList()

            letter_fastscroller.setupWithRecyclerView(select_contact_list, { position ->
                try {
                    val name = contacts[position].name
                    val character = if (name.isNotEmpty()) name.substring(0, 1) else ""
                    FastScrollItemIndicator.Text(character.toUpperCase(Locale.getDefault()))
                } catch (e: Exception) {
                    FastScrollItemIndicator.Text("")
                }
            })

            select_contact_list.adapter = ContactsAdapter(activity, contacts, select_contact_list) {
                callback(it as SimpleContact)
                dialog?.dismiss()
            }
        }

        dialog = AlertDialog.Builder(activity)
            .setNegativeButton(R.string.cancel, null)
            .create().apply {
                activity.setupDialogStuff(view, this)
            }
    }
}
