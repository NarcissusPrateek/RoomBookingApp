package com.nineleaps.conferenceroombooking.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;
import com.nineleaps.conferenceroombooking.R;

public class ScrollViewWithMaxHeight extends ScrollView {

    private static final int WITHOUTMAXVALUE = -1;

    private int maxHeight = WITHOUTMAXVALUE;

    public ScrollViewWithMaxHeight(Context context) {
        super(context);
    }

    public ScrollViewWithMaxHeight(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ScrollViewWithMaxHeight,
                0, 0);

        try {
            setMaxHeight(a.getDimensionPixelSize(R.styleable.ScrollViewWithMaxHeight_maximumHeight, 0));

        } finally {
            a.recycle();
        }
    }

    public ScrollViewWithMaxHeight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            if (maxHeight != WITHOUTMAXVALUE
                    && heightSize > maxHeight) {
                heightSize = maxHeight;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
            getLayoutParams().height = heightSize;
        } catch (Exception e) {
            Log.e("onMesure", "Error forcing height", e);
        } finally {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
}