package me.himanshusoni.touchconvirmview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.forEach
import androidx.core.view.setMargins
import androidx.core.view.setPadding


class TouchConfirmView : LinearLayout {

    private var mText: String = ""
    private var mTextAppearance: Int = 0
    private var mTextAllCaps: Boolean = true
    private var mTextSize: Int = 0
    private var mTextColor: Int = 0
    private var mConfirmedTextColor: Int = 0
    private var mTextBackground: Int = 0
    private var mConfirmedTextBackground: Int = 0
    private var mTextPadding: Int = 0
    private var mTextDividerWidth: Int = 0

    private var mOnTextConfirmedListener: (() -> Unit)? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyle,
        defStyleRes
    ) {
        init(attrs, defStyle, defStyleRes)
    }

    private fun init(attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0) {
        setPadding(4.dpToPx().toInt())

        val a = context.obtainStyledAttributes(attrs, R.styleable.TouchConfirmView, defStyle, defStyleRes)

        mTextAppearance = a.getResourceId(
            R.styleable.TouchConfirmView_textAppearance,
            R.style.TextAppearance_TouchConfirmView
        )

        mTextAllCaps = a.getBoolean(
            R.styleable.TouchConfirmView_textAllCaps,
            true
        )

        mTextSize = a.getDimensionPixelSize(
            R.styleable.TouchConfirmView_textSize,
            18.dpToPx().toInt()
        )

        mTextColor = a.getColor(
            R.styleable.TouchConfirmView_textColor,
            android.R.attr.textColorPrimary
        )

        val text = a.getString(R.styleable.TouchConfirmView_text) ?: ""
        setText(text)

        a.recycle()
    }

    private fun createTouchableViews() {
        removeAllViews()
        mText.forEach {
            val tv = createTextView(it)
            addView(tv)
        }
    }

    private fun createTextView(c: Char): TextView {
        val tv = TextView(context)

        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.setMargins(4.dpToPx().toInt())
        tv.layoutParams = lp

        tv.setPadding(16.dpToPx().toInt(), 8.dpToPx().toInt(), 16.dpToPx().toInt(), 8.dpToPx().toInt())

        val states = arrayOf(
            intArrayOf(-android.R.attr.state_selected), // not selected
            intArrayOf(android.R.attr.state_selected)  // selected
        )
        val colors = intArrayOf(Color.BLACK, Color.RED)
        tv.setTextColor(ColorStateList(states, colors))

        tv.setBackgroundResource(R.drawable.tcv_bg)
        tv.text = c.toString()

        tv.textSize = 24.dpToPx().toFloat()
        tv.minWidth = tv.measuredHeight

        tv.setOnClickListener {
            tv.isSelected = !tv.isSelected
            if (isTouchConfirmed()) {
                mOnTextConfirmedListener?.invoke()
            }
        }
        return tv
    }

    fun setText(text: String) {
        mText = text
        createTouchableViews()
    }

    fun isTouchConfirmed(): Boolean {
        var allConfirmed = childCount > 0
        forEach {
            if (!it.isSelected) {
                allConfirmed = false
            }
        }
        return allConfirmed
    }

    fun setOnTextConfirmedListener(otcl: () -> Unit) {
        mOnTextConfirmedListener = otcl
    }
}