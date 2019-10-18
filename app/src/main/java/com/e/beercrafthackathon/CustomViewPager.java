package com.e.beercrafthackathon;

import android.content.Context;
import android.util.AttributeSet;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {private float xDistance, yDistance, lastX, lastY;

public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
/*
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY) / 3; // favor X events
                lastX = curX;
                lastY = curY;
                if (xDistance > yDistance) {
                    return true;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }
*/}