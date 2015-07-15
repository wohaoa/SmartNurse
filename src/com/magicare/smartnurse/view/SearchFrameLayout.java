package com.magicare.smartnurse.view;

import com.magicare.smartnurse.R;
import com.magicare.smartnurse.utils.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.view.ViewHelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SearchFrameLayout extends FrameLayout {
	CircleImageView iv_temp;
	CircleImageView imageView;
	boolean hasMeasured;
	LinearLayout line_old, line;
	FrameLayout frameLayout;
	RelativeLayout relayout;
	int height;
	private long time = 500;

	public SearchFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SearchFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SearchFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		line_old = (LinearLayout) findViewById(R.id.line_old);
		line = (LinearLayout) findViewById(R.id.line);
		iv_temp = (CircleImageView) findViewById(R.id.iv_temp);
		imageView = (CircleImageView) findViewById(R.id.imageview);
		relayout = (RelativeLayout) findViewById(R.id.relayout);
		frameLayout = (FrameLayout) findViewById(R.id.vPager);
		// ViewHelper.setAlpha(line_old, 0);
		line_old.setVisibility(View.INVISIBLE);
		ViewHelper.setAlpha(frameLayout, 0);

		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		line.measure(w, h);
		height = line.getMeasuredHeight();

		ViewTreeObserver vto = imageView.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {

					int[] locations = new int[2];
					imageView.getLocationOnScreen(locations);
					int x = locations[0];// 获取组件当前位置的横坐标
					int y = locations[1];// 获取组件当前位置的纵坐标

					FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) iv_temp
							.getLayoutParams();
					int statusHeight = Utils.getStatusHeight((Activity) getContext());

					WindowManager.LayoutParams lp = ((Activity) getContext()).getWindow().getAttributes();

					layoutParams.topMargin = y - statusHeight;

					layoutParams.leftMargin = x;
					iv_temp.setLayoutParams(layoutParams);
					hasMeasured = true;
				}
				return true;
			}
		});
	}

	boolean isAnimFinsh;

	public void startAnimLineLayout(boolean isFlag) {

		ViewHelper.setAlpha(frameLayout, 1);
		Animation anim = null;

		if (!isAnimFinsh) {
			isAnimFinsh = true;
			if (isFlag) {
				final int initialHeight = line.getMeasuredHeight();
				anim = new Animation() {
					@Override
					protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t) {

						if (interpolatedTime == 1) {
							line.setVisibility(View.INVISIBLE);
							// line_old.setVisibility(View.VISIBLE);
							line.getLayoutParams().height = 1;
						} else {
							line.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
							line.requestLayout();
						}
					}

					@Override
					public boolean willChangeBounds() {
						return true;
					}
				};

			} else {
				line.setVisibility(View.VISIBLE);
				anim = new Animation() {
					@Override
					protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t) {

						if (interpolatedTime == 1) {
							// line.setVisibility(View.GONE);
							// line_old.setVisibility(View.VISIBLE);
							line.getLayoutParams().height = (int) (height - 0.5);
						} else {
							line.getLayoutParams().height = (int) (height * interpolatedTime);
							line.requestLayout();
						}
					}

					@Override
					public boolean willChangeBounds() {
						return true;
					}
				};

			}

		}

		if (anim != null) {
			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					isAnimFinsh = false;
				}
			});

			anim.setDuration(time + 100);
			line.startAnimation(anim);
		}

	}

	public void setImageBitmap(Bitmap bitmap) {
		iv_temp.setImageBitmap(bitmap);
	}

	AnimatorSet animSetAlpha;

	public void startAnimAlpha(boolean isFlag) {
		if (animSetAlpha != null && animSetAlpha.isRunning()) {
			return;
		}
		if (isFlag) {
			// ViewHelper.setAlpha(line_old, 1);
			line_old.setVisibility(View.VISIBLE);
			ObjectAnimator anim1 = ObjectAnimator.ofFloat(line_old, "alpha", 0, 1);
			animSetAlpha = new AnimatorSet();
			animSetAlpha.play(anim1);
			animSetAlpha.setDuration(time);
			animSetAlpha.start();
		} else {

			ObjectAnimator anim1 = ObjectAnimator.ofFloat(line_old, "alpha", 1, 0);
			animSetAlpha = new AnimatorSet();
			animSetAlpha.play(anim1);
			animSetAlpha.setDuration(time);
			animSetAlpha.start();
			animSetAlpha.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animator arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animator arg0) {
					// TODO Auto-generated method stub
					line_old.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onAnimationCancel(Animator arg0) {
					// TODO Auto-generated method stub

				}
			});

		}
	}

	AnimatorSet animSetSport;

	public void startAnimMove(boolean isFlag) {
		FrameLayout.LayoutParams initialHeight = (android.widget.FrameLayout.LayoutParams) iv_temp.getLayoutParams();
		final int top = initialHeight.topMargin;
		final int left = initialHeight.leftMargin;

		if (animSetSport != null && animSetSport.isRunning()) {
			return;
		}
		if (isFlag) {

			animSetSport = new AnimatorSet();
			iv_temp.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.INVISIBLE);
			ObjectAnimator anim1 = ObjectAnimator.ofFloat(iv_temp, "scaleX", 1.0f, 0.5f);
			ObjectAnimator anim4 = ObjectAnimator.ofFloat(iv_temp, "scaleY", 1.0f, 0.5f);
			ObjectAnimator anim2 = ObjectAnimator.ofFloat(iv_temp, "x", left,
					getContext().getResources().getDimension(R.dimen.userdetail_startAxnimMovex));
			ObjectAnimator anim3 = ObjectAnimator.ofFloat(iv_temp, "y", top,
					getContext().getResources().getDimension(R.dimen.userdetail_startAxnimMovey));

			animSetSport.play(anim1).with(anim2).with(anim3).with(anim4);
			animSetSport.setDuration(time);
			animSetSport.start();
		} else {

			ObjectAnimator anim1 = ObjectAnimator.ofFloat(iv_temp, "scaleX", 0.5f, 1f);
			ObjectAnimator anim4 = ObjectAnimator.ofFloat(iv_temp, "scaleY", 0.5f, 1);
			ObjectAnimator anim2 = ObjectAnimator.ofFloat(iv_temp, "x",
					getContext().getResources().getDimension(R.dimen.userdetail_startAxnimMovex), left);
			ObjectAnimator anim3 = ObjectAnimator.ofFloat(iv_temp, "y",
					getContext().getResources().getDimension(R.dimen.userdetail_startAxnimMovey), top);
			animSetSport = new AnimatorSet();
			animSetSport.play(anim1).with(anim2).with(anim3).with(anim4);

			animSetSport.setDuration(time);
			animSetSport.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animator arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animator arg0) {
					// TODO Auto-generated method stub
					iv_temp.setVisibility(View.GONE);
					imageView.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationCancel(Animator arg0) {
					// TODO Auto-generated method stub

				}
			});
			animSetSport.start();

		}
	}

	int x;

	public void startRelayout(boolean isFlag) {
		if (isFlag) {
			int[] locations = new int[2];
			relayout.getLocationOnScreen(locations);
			x = locations[0];// 获取组件当前位置的横坐标
			ObjectAnimator anim1 = ObjectAnimator.ofFloat(relayout, "x", x, getContext().getResources().getDimension(R.dimen.userdetail_startRelayout));
			AnimatorSet animSet = new AnimatorSet();
			animSet.play(anim1);
			animSet.setDuration(time);
			animSet.start();
		} else {
			ObjectAnimator anim1 = ObjectAnimator.ofFloat(relayout, "x", getContext().getResources().getDimension(R.dimen.userdetail_startRelayout), x);
			AnimatorSet animSet = new AnimatorSet();
			animSet.play(anim1);
			animSet.setDuration(time);
			animSet.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animator arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animator arg0) {
					// TODO Auto-generated method stub
					ViewHelper.setAlpha(frameLayout, 0);
				}

				@Override
				public void onAnimationCancel(Animator arg0) {
					// TODO Auto-generated method stub

				}
			});
			animSet.start();
		}

	}

	public void startAnim(boolean isFlag) {
		startAnimAlpha(isFlag);
		startAnimMove(isFlag);
		startAnimLineLayout(isFlag);
		startRelayout(isFlag);
	}

}
