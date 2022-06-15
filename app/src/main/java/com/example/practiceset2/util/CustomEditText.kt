package com.example.practiceset2.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.example.practiceset2.R


/*
To fix the memory leak with edit text
 */
@SuppressLint("AppCompatCustomView", "ClickableViewAccessibility")
class CustomEditText : EditText {

//    private var isLight = true



    companion object{

        @SuppressLint("SoonBlockedPrivateApi")
        var mParent: java.lang.reflect.Field? =
            try{
                View::class.java.getDeclaredField("mParent").apply {
                    isAccessible = true
                }
            } catch (ex: NoSuchFieldException){
                ex.printStackTrace()
                null
            }

        fun getThemeBasedOnNightMode(context: Context): Int{
//            val result = PreferenceManager.getDefaultSharedPreferences(context)
//                .getBoolean("colorMode", true)
//            return if(!result) {
//                R.style.MyTextInputLayoutStyleLight
//            } else {
//                R.style.MyTextInputLayoutStyleDark
//            }
            val result = AppCompatDelegate.getDefaultNightMode()
            return if (result == MODE_NIGHT_NO){
                //R.style.MyTextInputLayoutStyleDark
                R.style.MyTextInputLayoutStyleLight
                //R.style.MyTextInputLayoutStyleDark
            } else {
                R.style.MyTextInputLayoutStyleDark
                //R.style.MyTextInputLayoutStyleDark
            }
        }
    }

    constructor(context: Context): super(ContextThemeWrapper(context.applicationContext, getThemeBasedOnNightMode(context.applicationContext)))

    constructor(context: Context, attrs: AttributeSet): super(ContextThemeWrapper(context.applicationContext, getThemeBasedOnNightMode(context.applicationContext)), attrs){
//        val a = context.theme.obtainStyledAttributes(
//            attrs,
//            R.styleable.myCustomComponents,
//            0, 0)
//        val b: Int = a.getInt(R.styleable.myCustomComponents_myCustomAttribute, 0)
//        this.attrs2?
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(
        ContextThemeWrapper(context.applicationContext, getThemeBasedOnNightMode(context.applicationContext)), attrs, defStyleAttr)

    override fun onDetachedFromWindow() {
        try {
            if (mParent != null) mParent?.set(this, null)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        super.onDetachedFromWindow()
    }

    fun readIsLightMode(): Boolean{
        val result = AppCompatDelegate.getDefaultNightMode()
        return result == MODE_NIGHT_NO
    }

    fun read(context: Context, attrs: AttributeSet){
//        val typeArray = context.theme.obtainStyledAttributes(
//            attrs,
//            R.styleable.myCustomComponents,
//            0, 0)
//        try {
//
//            val typefaceType = typeArray
//                .getInt(R.styleable.myCustomComponents, 0);
//        } finally {
//            typeArray.recycle()
//        }
    }

//    private fun showClearButton() {
//        // Sets the Drawables (if any) to appear to the left of,
//        // above, to the right of, and below the text.
//        setCompoundDrawablesRelativeWithIntrinsicBounds(
//            null,  // Start of text.
//            null,  // Top of text.
//            mClearButtonImage,  // End of text.
//            null
//        ) // Below text.
//    }

    //lateinit var mClearButtonImage: Drawable

//    init {
//        mClearButtonImage = ResourcesCompat.getDrawable(
//            resources,
//            R.drawable.ic_clear_opaque_24dp, null
//        )!!
//
//        // If the X (clear) button is tapped, clear the text.
//
//        // If the X (clear) button is tapped, clear the text.
//        setOnTouchListener(OnTouchListener { v, event -> // Use the getCompoundDrawables()[2] expression to check
//            // if the drawable is on the "end" of text [2].
//            if (compoundDrawablesRelative[2] != null) {
//                val clearButtonStart: Float // Used for LTR languages
//                val clearButtonEnd: Float // Used for RTL languages
//                var isClearButtonClicked = false
//                // Detect the touch in RTL or LTR layout direction.
//                if (layoutDirection == LAYOUT_DIRECTION_RTL) {
//                    // If RTL, get the end of the button on the left side.
//                    clearButtonEnd = (mClearButtonImage
//                        .intrinsicWidth + paddingStart).toFloat()
//                    // If the touch occurred before the end of the button,
//                    // set isClearButtonClicked to true.
//                    if (event.x < clearButtonEnd) {
//                        isClearButtonClicked = true
//                    }
//                } else {
//                    // Layout is LTR.
//                    // Get the start of the button on the right side.
//                    clearButtonStart = (width - paddingEnd
//                            - mClearButtonImage.intrinsicWidth).toFloat()
//                    // If the touch occurred after the start of the button,
//                    // set isClearButtonClicked to true.
//                    if (event.x > clearButtonStart) {
//                        isClearButtonClicked = true
//                    }
//                }
//                // Check for actions if the button is tapped.
//                if (isClearButtonClicked) {
//                    // Check for ACTION_DOWN (always occurs before ACTION_UP).
//                    if (event.action == MotionEvent.ACTION_DOWN) {
//                        // Switch to the black version of clear button.
//                        mClearButtonImage = ResourcesCompat.getDrawable(
//                            resources,
//                            R.drawable.ic_clear_black_24dp, null
//                        )!!
//                        showClearButton()
//                    }
//                    // Check for ACTION_UP.
//                    if (event.action == MotionEvent.ACTION_UP) {
//                        // Switch to the opaque version of clear button.
//                        mClearButtonImage = ResourcesCompat.getDrawable(
//                            resources,
//                            R.drawable.ic_clear_opaque_24dp, null
//                        )!!
//                        // Clear the text and hide the clear button.
//                        text!!.clear()
//                        hideClearButton()
//                        return@OnTouchListener true
//                    }
//                } else {
//                    return@OnTouchListener false
//                }
//            }
//            false
//        })
//
//    }

    /**
     * Hides the clear button.
     */
    private fun hideClearButton() {
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,  // Start of text.
            null,  // Top of text.
            null,  // End of text.
            null
        ) // Below text.
    }
}