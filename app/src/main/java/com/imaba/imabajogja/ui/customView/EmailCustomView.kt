package com.imaba.imabajogja.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import java.util.regex.Pattern

class EmailCustomView : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val emailPattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\$"
                val isValidEmail = Pattern.matches(emailPattern, s.toString())

                if (!isValidEmail) {
                    setError("Email tidak valid ", null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        })
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "example@mail.com"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

}