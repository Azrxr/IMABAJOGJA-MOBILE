package com.imaba.imabajogja.data.utils

//noinspection SuspiciousImport
import android.R
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

object Dropdown {
    fun setSimpleDropdown(
        context: Context,
        view: AutoCompleteTextView,
        options: List<String>,
        selected: String? = null
    ) {
        val adapter = ArrayAdapter(context, R.layout.simple_dropdown_item_1line, options)
        view.setAdapter(adapter)

        selected?.let {
            options.find { it.equals(selected, ignoreCase = true) }?.let {
                view.setText(it, false)
            }
        }
    }

    fun setNumberDropdown(
    context: Context,
    view: AutoCompleteTextView,
    range: IntRange,
    selected: Int? = null
    ) {
        val options = range.toList()
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, options)
        view.setAdapter(adapter)

        selected?.let {
            if (options.contains(it)) {
                view.setText(it.toString(), false)
            }
        }
    }
}