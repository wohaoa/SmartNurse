package com.magicare.smartnurse.view;

import cn.jpush.android.api.c;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class DrawableTextView extends TextView {

	public DrawableTextView(Context context) {
		super(context);
	}

	public DrawableTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DrawableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable drawableLeft = drawables[0];
			if (drawableLeft != null) {

				Bitmap bitmap = ((BitmapDrawable) drawableLeft).getBitmap();
				canvas.drawBitmap(bitmap, 0, 0, getPaint());
				canvas.drawText(getText().toString(),
						drawableLeft.getIntrinsicWidth(), 0, getPaint());
				setCompoundDrawables(null, null, null, null);
			}
		}
		super.onDraw(canvas);
	}

}
