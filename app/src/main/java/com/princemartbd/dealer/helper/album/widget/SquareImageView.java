
package com.princemartbd.dealer.helper.album.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;

import androidx.annotation.Nullable;


public class SquareImageView extends androidx.appcompat.widget.AppCompatImageView {

    private final Configuration mConfig;

    public SquareImageView(Context context) {
        this(context, null, 0);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mConfig = getResources().getConfiguration();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int orientation = mConfig.orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT: {
                super.onMeasure(widthMeasureSpec, widthMeasureSpec);
                break;
            }
            case Configuration.ORIENTATION_LANDSCAPE: {
                super.onMeasure(heightMeasureSpec, heightMeasureSpec);
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }
}