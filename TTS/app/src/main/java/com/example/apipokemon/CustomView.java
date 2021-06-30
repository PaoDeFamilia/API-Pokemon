package com.example.apipokemon;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public class CustomView extends View {

    private Paint mPaintCircle1;
    private Paint mPaintCircle2;

    public CustomView(Context context) {
        super(context);

        init(null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        mPaintCircle1 = new Paint();
        mPaintCircle1.setAntiAlias(true);
        mPaintCircle1.setColor(Color.BLACK);

        mPaintCircle2 = new Paint();
        mPaintCircle2.setAntiAlias(true);
        mPaintCircle2.setColor(Color.RED);

        if (set == null)
            return;

    }

    public void swapColor() {
        mPaintCircle2.setColor(mPaintCircle2.getColor() == Color.RED ? Color.GREEN : Color.RED);

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx1, cy1;
        float radius1 = 15f;

        cx1 = getWidth() - (getWidth()/2);
        cy1 = getHeight() - (getHeight()/2);

        canvas.drawCircle(cx1, cy1, radius1, mPaintCircle1);

        float cx2, cy2;
        float radius2 = 12f;

        cx2 = getWidth() - (getWidth()/2);
        cy2 = getHeight() - (getHeight()/2);

        canvas.drawCircle(cx2, cy2, radius2, mPaintCircle2);
    }



}
