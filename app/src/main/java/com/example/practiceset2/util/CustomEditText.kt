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
            val result = AppCompatDelegate.getDefaultNightMode()
            return if (result == MODE_NIGHT_NO){
                R.style.MyTextInputLayoutStyleLight
            } else {
                R.style.MyTextInputLayoutStyleDark
            }
        }
    }

    constructor(context: Context):
            super(ContextThemeWrapper(context.applicationContext,
                getThemeBasedOnNightMode(context.applicationContext)))

    constructor(context: Context, attrs: AttributeSet):
            super(ContextThemeWrapper(context.applicationContext,
                getThemeBasedOnNightMode(context.applicationContext)), attrs){
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(
        ContextThemeWrapper(context.applicationContext,
            getThemeBasedOnNightMode(context.applicationContext)), attrs, defStyleAttr)

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
}