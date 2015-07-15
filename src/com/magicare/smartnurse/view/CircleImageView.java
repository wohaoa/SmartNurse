package com.magicare.smartnurse.view;

import com.magicare.smartnurse.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * @author Administrator
 * 
 *         Function:圆形头像
 *         
 *          <com.magicare.smartnurse.view.CircleImageView
		        xmlns:myxmlns="http://schemas.android.com/apk/res/com.magicare.smartnurse"
		        android:id="@+id/user"
		        android:layout_width="100dp"
		        android:layout_height="100dp"
		        android:layout_margin="50dp"
		        android:scaleType="centerCrop"
		        android:src="@drawable/ic_alarm_big"
		        myxmlns:border_color="#ffffff"	//边框颜色
		        myxmlns:border_width="10dp"		//边框宽度
		        myxmlns:inner_color="#ffffff"  //内圆颜色
		        myxmlns:out_color="#65BA21" />  //外圆颜色
 */
public class CircleImageView extends ImageView {
	private static final Xfermode MASK_XFERMODE;
	private Bitmap mask;
	private Paint paint;

	private int mBorderWidth = 10;
	private int mBorderColor = Color.parseColor("#ffffff");
	private int mInnerColor = Color.parseColor("#ffffff");
	private int mOutColor = Color.parseColor("#ffffff");

	static {
		PorterDuff.Mode localMode = PorterDuff.Mode.DST_IN;
		MASK_XFERMODE = new PorterDuffXfermode(localMode);
	}

	public CircleImageView(Context paramContext) {
		super(paramContext);
	}

	public CircleImageView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		TypedArray a = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CircularImage);
		mBorderColor = a.getColor(R.styleable.CircularImage_border_color, mBorderColor);
		final int defalut = (int) (2 * paramContext.getResources().getDisplayMetrics().density + 0.5f);
		mBorderWidth = a.getDimensionPixelOffset(R.styleable.CircularImage_border_width, defalut);
		mInnerColor = a.getColor(R.styleable.CircularImage_inner_color, mInnerColor);
		mOutColor = a.getColor(R.styleable.CircularImage_out_color, mOutColor);
		a.recycle();
	}

	public CircleImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		TypedArray a = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CircularImage);
		mBorderColor = a.getColor(R.styleable.CircularImage_border_color, mBorderColor);
		final int defalut = (int) (2 * paramContext.getResources().getDisplayMetrics().density + 0.5f);
		mBorderWidth = a.getDimensionPixelOffset(R.styleable.CircularImage_border_width, defalut);
		mInnerColor = a.getColor(R.styleable.CircularImage_inner_color, mInnerColor);
		mOutColor = a.getColor(R.styleable.CircularImage_out_color, mOutColor);
		a.recycle();

	}

	protected void onDraw(Canvas paramCanvas) {
		Drawable localDrawable = getDrawable();
		if (localDrawable == null)
			return;
		try {
			if (this.paint == null) {
				Paint localPaint1 = new Paint();
				this.paint = localPaint1;
				this.paint.setFilterBitmap(false);
				Paint localPaint2 = this.paint;
				Xfermode localXfermode1 = MASK_XFERMODE;
				@SuppressWarnings("unused")
				Xfermode localXfermode2 = localPaint2.setXfermode(localXfermode1);
			}
			int width = getWidth();
			int height = getHeight();
			int layer = paramCanvas.saveLayer(0.0F, 0.0F, width, height, null, 31);
			localDrawable.setBounds(0, 0, width, height);
			localDrawable.draw(paramCanvas);
			if ((this.mask == null) || (this.mask.isRecycled())) {
				this.mask = createMask(width, height);
			}
			paramCanvas.drawBitmap(mask, 0.0F, 0.0F, paint);
			paramCanvas.restoreToCount(layer);
			drawBorder(paramCanvas, width, height, paint);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 绘制最外面的边框
	 * 
	 * @param canvas
	 * @param width
	 * @param height
	 */
	private void drawBorder(Canvas canvas, final int width, final int height, Paint paint) {
		if (mBorderWidth == 0) {
			return;
		}

		final Paint mBorderPaint = new Paint();
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mInnerColor);
		mBorderPaint.setStrokeWidth(5);
		canvas.drawCircle(width >> 1, height >> 1, (width - mBorderWidth - 7) >> 1, mBorderPaint);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderWidth);
		/**
		 * 坐标x：view宽度的一般 坐标y：view高度的一般 半径r：因为是view的宽度-border的一半
		 */
		canvas.drawCircle(width >> 1, height >> 1, (width - mBorderWidth) >> 1, mBorderPaint);
		mBorderPaint.setColor(mOutColor);
		mBorderPaint.setStrokeWidth(2);
		canvas.drawCircle(width >> 1, height >> 1, (width - 2) >> 1, mBorderPaint);
		canvas = null;
	}

	private Bitmap createMask(int width, int height) {
		Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
		Bitmap localBitmap = Bitmap.createBitmap(width, height, localConfig);
		Canvas localCanvas = new Canvas(localBitmap);
		Paint localPaint = new Paint(1);
		localPaint.setColor(-16777216);
		RectF localRectF = new RectF(0.0F, 0.0F, width, height);
		localCanvas.drawOval(localRectF, localPaint);
		return localBitmap;
	}

	public void setBorderColor(int color) {
		this.mBorderColor = color;
	}

	public void setBorderWidth(int width) {
		this.mBorderWidth = width;
	}

	public void setOutColor(int color) {
		this.mOutColor = color;
	}

	public void setInnerColor(int color) {
		this.mInnerColor = color;
	}

}
