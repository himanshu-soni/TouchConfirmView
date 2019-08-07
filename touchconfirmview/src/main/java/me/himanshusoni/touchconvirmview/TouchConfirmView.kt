package me.himanshusoni.touchconvirmview

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import me.himanshusoni.basicextensions.dpToPx

class TouchConfirmView : LinearLayout {

    private var mText: String = ""
    private var mTextAllCaps: Boolean = true
    private var mTextSize: Int = 0
    private var mTextColorNormal: Int = 0
    private var mTextColorConfirmed: Int = 0
    private var mTextBackgroundNormal: Drawable? = null
    private var mTextBackgroundConfirmed: Drawable? = null
    private var mTextPadding: Int = 0
    private var mTextMargin: Int = 0

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyle,
        defStyleRes
    ) {
        init(attrs, defStyle, defStyleRes)
    }

    private fun init(attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TouchConfirmView, defStyle, defStyleRes)

        mTextBackgroundConfirmed = a.getDrawable(R.styleable.TouchConfirmView_textBackgroundConfirmed)

        mTextBackgroundNormal = a.getDrawable(R.styleable.TouchConfirmView_textBackgroundNormal)

        mTextMargin = a.getDimension(
            R.styleable.TouchConfirmView_textMargin,
            4.dpToPx().toFloat()
        ).toInt()

        mTextPadding = a.getDimension(
            R.styleable.TouchConfirmView_textPadding,
            4.dpToPx().toFloat()
        ).toInt()

        mTextColorConfirmed = a.getColor(
            R.styleable.TouchConfirmView_textColorConfirmed,
            Color.RED
        )

        mTextColorNormal = a.getColor(
            R.styleable.TouchConfirmView_textColorNormal,
            Color.BLACK
        )

        mTextAllCaps = a.getBoolean(
            R.styleable.TouchConfirmView_textAllCaps,
            true
        )

        mTextSize = a.getDimensionPixelSize(
            R.styleable.TouchConfirmView_textSize,
            18.dpToPx().toInt()
        )

        val text = a.getString(R.styleable.TouchConfirmView_text) ?: ""
        setText(text)

        // setPadding(4.dpToPx().toInt(), 4.dpToPx().toInt(), 4.dpToPx().toInt(), 4.dpToPx().toInt())
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
        lp.setMargins(mTextMargin, mTextMargin, mTextMargin, mTextMargin)
        tv.layoutParams = lp

        tv.setPadding(mTextPadding, mTextPadding, mTextPadding, mTextPadding)

        val states = arrayOf(
            intArrayOf(-android.R.attr.state_selected), // not selected
            intArrayOf(android.R.attr.state_selected)  // selected
        )
        val colors = intArrayOf(mTextColorNormal, mTextColorConfirmed)
        tv.setTextColor(ColorStateList(states, colors))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tv.background = mTextBackgroundNormal
        } else {
            tv.setBackgroundDrawable(mTextBackgroundNormal)
        }

        tv.text = if (mTextAllCaps) c.toString().toUpperCase() else c.toString()

        tv.textSize = mTextSize.toFloat()

        tv.setOnClickListener {
            tv.isSelected = !tv.isSelected

            val back = if (tv.isSelected) mTextBackgroundConfirmed else mTextBackgroundNormal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                tv.background = back
            } else {
                tv.setBackgroundDrawable(back)
            }

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
        for (childIndex in 0 until childCount) {
            if (!getChildAt(childIndex).isSelected) {
                allConfirmed = false
            }
        }
        return allConfirmed
    }

    fun setOnTextConfirmedListener(otcl: () -> Unit) {
        mOnTextConfirmedListener = otcl
    }
}