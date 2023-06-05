package com.example.kyselyapp;

import android.content.Context;
import android.util.AttributeSet;

public class SquareRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {

    public SquareRadioButton(Context context) {
        super(context);
    }

    public SquareRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
