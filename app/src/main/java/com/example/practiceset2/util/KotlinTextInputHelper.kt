package com.example.practiceset2.util

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView

data class KotlinTextInputHelper(val view: View, val bool: Boolean=true): TextWatcher {


//    lateinit var mMainView: View
    var mViewSet: ArrayList<TextView>? = null
//    var isAlpha: Boolean = false

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        mViewSet?.let {
            for (textView in it){
                if(textView.text.hashCode() == p0.hashCode()){

                }
            }
        }
    }

    fun addViews(vararg views: TextView) {
        if (views == null) return
        if (mViewSet == null) {
            mViewSet = ArrayList(views.size - 1)
        }
        for (view in views) {
            view.addTextChangedListener(this)
            mViewSet?.add(view)
        }
        afterTextChanged(null)
    }

    fun removeViews() {
        if (mViewSet == null) return
        mViewSet?.let {
            for (view in it) {
                view.removeTextChangedListener(this)
            }
            it.clear()
        }
        mViewSet = null
    }

    fun setEnabled(enabled: Boolean) {
        if (enabled == view.isEnabled()) return
        if (enabled) {
            // View-enabled events
            view.setEnabled(true)
            if (bool) {
                // Setting opacity
                view.setAlpha(1f)
            }
        } else {
            // Disabling View Events
            view.setEnabled(false)
            if (bool) {
                // Setting up translucency
                view.setAlpha(0.5f)
            }
        }
    }
}