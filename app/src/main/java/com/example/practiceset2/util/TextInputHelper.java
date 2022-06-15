package com.example.practiceset2.util;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Enable or disable button click events by managing whether multiple TextView or EditText input is empty
 */
public final class TextInputHelper implements TextWatcher {

    private View mMainView; // Operating Button View
    private List< TextView > mViewSet; //TextView collection, subclasses can also be (EditText, TextView, Button)
    private Boolean isAlpha; // Set Transparency

    public TextInputHelper(View view) {
        this(view, true);
    }

    /**
     * Constructor
     *
     *@ Param view follows EditText or TextView input empty to determine whether to start or disable the View
     *@ Does param alpha need to set transparency
     */
    public TextInputHelper(View view, boolean alpha) {
        if (view == null) throw new IllegalArgumentException("The view is empty");
        mMainView = view;
        isAlpha = alpha;
    }

    /**
     * Add EditText or TextView listeners
     *
     *@ Param views pass in a single or multiple EditText or TextView objects
     */
    public void addViews(TextView... views) {
        if (views == null) return;

        if (mViewSet == null) {
            mViewSet = new ArrayList<>(views.length - 1);
        }

        for (TextView view : views) {
            view.addTextChangedListener(this);
            mViewSet.add(view);
        }
        afterTextChanged(null);
    }

    /**
     * Remove EditText listeners to avoid memory leaks
     */
    public void removeViews() {
        if (mViewSet == null) return;

        for (TextView view : mViewSet) {
            view.removeTextChangedListener(this);
        }
        mViewSet.clear();
        mViewSet = null;
    }

    // TextWatcher

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public synchronized void afterTextChanged(Editable s) {
        if (mViewSet == null) return;

        for (TextView view : mViewSet) {
            if ("".equals(view.getText().toString())) {
                setEnabled(false);
                return;
            }
        }

        setEnabled(true);
    }

    /**
     * Events Setting View
     *
     *@ Param enabled enabled or disabled View events
     */
    public void setEnabled(boolean enabled) {
        if (enabled == mMainView.isEnabled()) return;

        if (enabled) {
            // View-enabled events
            mMainView.setEnabled(true);
            if (isAlpha) {
                // Setting opacity
                mMainView.setAlpha(1f);
            }
        }else {
            // Disabling View Events
            mMainView.setEnabled(false);
            if (isAlpha) {
                // Setting up translucency
                mMainView.setAlpha(0.5f);
            }
        }
    }
}

