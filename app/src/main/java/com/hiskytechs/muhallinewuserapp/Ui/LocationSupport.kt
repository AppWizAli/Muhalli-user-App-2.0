package com.hiskytechs.muhallinewuserapp.Ui

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.hiskytechs.muhallinewuserapp.R

object LocationSupport {
    val suggestions = listOf(
        "Karachi",
        "Saddar, Karachi",
        "Gulshan-e-Iqbal, Karachi",
        "North Nazimabad, Karachi",
        "Korangi, Karachi",
        "Landhi, Karachi",
        "Malir, Karachi",
        "Clifton, Karachi",
        "DHA Karachi",
        "Shah Faisal Colony, Karachi",
        "Federal B Area, Karachi",
        "New Karachi",
        "Liaquatabad, Karachi",
        "Gulistan-e-Jauhar, Karachi",
        "SITE Area, Karachi",
        "Baldia Town, Karachi",
        "Orangi Town, Karachi",
        "Nazimabad, Karachi",
        "PECHS, Karachi",
        "Bahadurabad, Karachi",
        "Lahore",
        "Islamabad",
        "Rawalpindi",
        "Hyderabad",
        "Faisalabad",
        "Multan",
        "Peshawar",
        "Quetta"
    )

    fun bindSuggestions(context: Context, view: AutoCompleteTextView) {
        view.setAdapter(
            ArrayAdapter(
                context,
                android.R.layout.simple_dropdown_item_1line,
                suggestions
            )
        )
        view.threshold = 1
        view.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && view.text.isNullOrBlank()) {
                view.showDropDown()
            }
        }
        view.setOnClickListener {
            if (view.text.isNullOrBlank()) {
                view.showDropDown()
            }
        }
        view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_on_24, 0, 0, 0)
    }
}
