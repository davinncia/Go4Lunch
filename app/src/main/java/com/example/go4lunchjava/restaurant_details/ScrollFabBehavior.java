package com.example.go4lunchjava.restaurant_details;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ScrollFabBehavior extends FloatingActionButton.Behavior {

    public ScrollFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        //setAutoHideEnabled(true);
    }


    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {

        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        if (dyUnconsumed > 0 && child.getVisibility() == View.VISIBLE) {
            // Workmate scrolled down
            child.setVisibility(View.INVISIBLE);
        } else if (dyUnconsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // Workmate scrolled all the way up
            child.show();
        }
    }


}
