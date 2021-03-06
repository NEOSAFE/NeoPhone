package com.neosafe.neophone.fragments

import android.content.Context
import android.util.AttributeSet
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.neosafe.commons.adapters.MyRecyclerViewAdapter
import com.neosafe.commons.dialogs.CallConfirmationDialog
import com.neosafe.commons.dialogs.RadioGroupDialog
import com.neosafe.commons.extensions.*
import com.neosafe.commons.helpers.MyContactsContentProvider
import com.neosafe.commons.helpers.PERMISSION_READ_CONTACTS
import com.neosafe.commons.helpers.SimpleContactsHelper
import com.neosafe.commons.models.RadioItem
import com.neosafe.commons.models.SimpleContact
import com.neosafe.neophone.R
import com.neosafe.neophone.activities.SimpleActivity
import com.neosafe.neophone.adapters.ContactsAdapter
import com.neosafe.neophone.extensions.config
import com.neosafe.neophone.interfaces.RefreshItemsListener
import kotlinx.android.synthetic.main.fragment_letters_layout.view.*
import java.util.*

class FavoritesFragment(context: Context, attributeSet: AttributeSet) : MyViewPagerFragment(context, attributeSet), RefreshItemsListener {
    private var allContacts = ArrayList<SimpleContact>()

    override fun setupFragment() {
        val placeholderResId = if (context.hasPermission(PERMISSION_READ_CONTACTS)) {
            R.string.no_contacts_found
        } else {
            R.string.could_not_access_contacts
        }

        fragment_placeholder.text = context.getString(placeholderResId)
        fragment_fab.beGone()
        fragment_placeholder_2.beGone()
    }

    override fun setupColors(textColor: Int, primaryColor: Int, adjustedPrimaryColor: Int) {
        fragment_placeholder.setTextColor(textColor)
        (fragment_list?.adapter as? MyRecyclerViewAdapter)?.updateTextColor(textColor)

        letter_fastscroller.textColor = textColor.getColorStateList()
        letter_fastscroller.pressedTextColor = adjustedPrimaryColor
        letter_fastscroller_thumb.setupWithFastScroller(letter_fastscroller)
        letter_fastscroller_thumb.textColor = adjustedPrimaryColor.getContrastColor()
        letter_fastscroller_thumb.thumbColor = adjustedPrimaryColor.getColorStateList()
    }

    override fun refreshItems() {
        val privateCursor = context?.getMyContactsCursor(true, true)?.loadInBackground()
        SimpleContactsHelper(context).getAvailableContacts(true) { contacts ->
            allContacts = contacts

            val privateContacts = MyContactsContentProvider.getSimpleContacts(context, privateCursor)
            if (privateContacts.isNotEmpty()) {
                allContacts.addAll(privateContacts)
                allContacts.sort()
            }

            activity?.runOnUiThread {
                gotContacts(contacts)
            }
        }
    }

    private fun gotContacts(contacts: ArrayList<SimpleContact>) {
        setupLetterFastscroller(contacts)
        if (contacts.isEmpty()) {
            fragment_placeholder.beVisible()
            fragment_list.beGone()
        } else {
            fragment_placeholder.beGone()
            fragment_list.beVisible()

            val currAdapter = fragment_list.adapter
            if (currAdapter == null) {
                ContactsAdapter(activity as SimpleActivity, contacts, fragment_list, this, showDeleteButton = false) {
                    if (context.config.showCallConfirmation) {
                        CallConfirmationDialog(activity as SimpleActivity, (it as SimpleContact).name) {
                            callContact(it)
                        }
                    } else {
                        callContact(it as SimpleContact)
                    }
                }.apply {
                    fragment_list.adapter = this
                }
            } else {
                (currAdapter as ContactsAdapter).updateItems(contacts)
            }
        }
    }

    private fun callContact(simpleContact: SimpleContact) {
        val phoneNumbers = simpleContact.phoneNumbers
        if (phoneNumbers.size <= 1) {
            activity?.launchCallIntent(phoneNumbers.first())
        } else {
            val items = ArrayList<RadioItem>()
            phoneNumbers.forEachIndexed { index, phoneNumber ->
                items.add(RadioItem(index, phoneNumber))
            }

            RadioGroupDialog(activity!!, items) {
                activity?.launchCallIntent(phoneNumbers[it as Int])
            }
        }
    }

    private fun setupLetterFastscroller(contacts: ArrayList<SimpleContact>) {
        letter_fastscroller.setupWithRecyclerView(fragment_list, { position ->
            try {
                val name = contacts[position].name
                val character = if (name.isNotEmpty()) name.substring(0, 1) else ""
                FastScrollItemIndicator.Text(character.toUpperCase(Locale.getDefault()).normalizeString())
            } catch (e: Exception) {
                FastScrollItemIndicator.Text("")
            }
        })
    }

    override fun onSearchClosed() {
        fragment_placeholder.beVisibleIf(allContacts.isEmpty())
        (fragment_list.adapter as? ContactsAdapter)?.updateItems(allContacts)
        setupLetterFastscroller(allContacts)
    }

    override fun onSearchQueryChanged(text: String) {
        val contacts = allContacts.filter {
            it.name.contains(text, true) || it.doesContainPhoneNumber(text)
        }.sortedByDescending {
            it.name.startsWith(text, true)
        }.toMutableList() as ArrayList<SimpleContact>

        fragment_placeholder.beVisibleIf(contacts.isEmpty())
        (fragment_list.adapter as? ContactsAdapter)?.updateItems(contacts, text)
        setupLetterFastscroller(contacts)
    }
}
