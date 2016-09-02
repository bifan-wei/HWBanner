package com.example.bannerlib;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author huangwei 2016年8月31日下午5:24:02
 * 
 */
public class BannerViewGroup extends ViewGroup {

	private static final int PAGE_UP = 0X04, PAGE_DOWN = 0X05;
	private int movestate = PAGE_DOWN;
	private int automsiwtchtime = 3000;// 自动切换时间
	private int leftdividerxposition, rightdividerxposition;
	private int x0, actiondownx0;

	private BannerViewdata mBannerViewdata;
	private BannerViewdata prepagedata;
	private BannerViewdata nextpagedata;
	private Boolean onAimation = false;
	private Boolean startupautomswitch = true;// 启动自动切换
	private Handler handler;
	private BannerClickListenr bannerClickListenr;
	private long downtime;// action down时的时间，用于判断是不是点击事件

	public BannerViewGroup(Context context) {
		super(context);

	}

	public BannerViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		init();

	}

	public BannerViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@SuppressLint("NewApi")
	public BannerViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private TimerTask timetask = new TimerTask() {

		@Override
		public void run() {
			if (movestate == PAGE_DOWN) {
				handler.sendEmptyMessage(PAGE_DOWN);
			} else {
				handler.sendEmptyMessage(PAGE_UP);
			}

		}
	};

	private void init() {

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case PAGE_DOWN:
					if (!onAimation) {

						doPagedownAnimation();
					}
					break;
				case PAGE_UP:
					if (!onAimation) {

						doPageUpAnimation();
					}
					break;
				default:
					break;
				}

			}
		};
	}

	public void startupautomswitch() {
		startupautomswitch = true;
		Timer timer = new Timer();
		if (mBannerViewdata.getLength() > 1) {
			timer.schedule(timetask, 1000, automsiwtchtime);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int cWidth = 0;
		int cHeight = 0;

		cWidth = getMeasuredWidth();
		cHeight = getMeasuredHeight();

		int childcount = getChildCount();
		// 隐藏所有可见view,这是为了清除可能出现重叠的情况
		for (int i = 0; i < childcount; i++) {
			View childview = getChildAt(i);
			childview.layout(-cWidth, 0, 0, 0);
		}

		if (prepagedata != null) {

			View childView1 = prepagedata.getView();

			if (childView1 != null) {
				int cl1 = 0, ct1 = 0, cr1 = 0, cb1 = 0;
				cl1 = leftdividerxposition - cWidth;
				cr1 = leftdividerxposition;
				cb1 = cHeight + ct1;
				childView1.layout(cl1, ct1, cr1, cb1);
			}
		}

		if (mBannerViewdata != null) {

			View childView2 = mBannerViewdata.getView();

			if (childView2 != null) {
				int cl1 = 0, ct1 = 0, cr1 = 0, cb1 = 0;
				cl1 = leftdividerxposition;
				cr1 = rightdividerxposition;
				cb1 = cHeight + ct1;
				childView2.layout(cl1, ct1, cr1, cb1);
			}
		}

		if (nextpagedata != null) {
			View childView3 = nextpagedata.getView();

			if (childView3 != null) {
				int cl2 = rightdividerxposition, ct2 = 0, cr2 = rightdividerxposition, cb2 = 0;
				cr2 = cl2 + cWidth;
				cb2 = cHeight + ct2;
				childView3.layout(cl2, ct2, cr2, cb2);
			}
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		/**
		 * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
		 */

		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

		// 计算出所有的childView的宽和高
		measureChildren(MeasureSpec.makeMeasureSpec(sizeWidth, MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(sizeHeight, MeasureSpec.AT_MOST));

		leftdividerxposition = 0;
		rightdividerxposition = sizeWidth + leftdividerxposition;

		/**
		 * 直接设置为父容器计算的值
		 */
		setMeasuredDimension(sizeWidth, sizeHeight);

	}

	/**
	 * 添加数据
	 * 
	 * @param bannerViewdata
	 */
	public void setBannerViewData(BannerViewdata bannerViewdata) {
		if (!checkBannersecurity(bannerViewdata)) {
			return;
		}

		mBannerViewdata = bannerViewdata;
		mBannerViewdata = mBannerViewdata.findtheFirstViewdata();

		if (!checkBannersecurity(bannerViewdata)) {
			return;
		}

		prepagedata = mBannerViewdata.getPreviewdata();
		nextpagedata = mBannerViewdata.getNextviewdata();

		addview();

		if (startupautomswitch) {
			startupautomswitch();
		}
	}

	private void addview() {
		BannerViewdata viewdata = mBannerViewdata;

		while (checkBannersecurity(viewdata)) {
			addView(viewdata.getView());
			viewdata = viewdata.getNextviewdata();
			if (checkBannersecurity(viewdata) && viewdata.istheLast()) {
				addView(viewdata.getView());
				return;
			}

		}

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 将事件拦截掉
		return true;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int x = (int) event.getX();

		if (onAimation) {
			return true;
		}

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			downtime = System.currentTimeMillis();
			x0 = x;
			actiondownx0 = x0;

			break;
		case MotionEvent.ACTION_MOVE:
			int offsetx = x - x0;

			x0 = x;
			leftdividerxposition = leftdividerxposition + offsetx;
			rightdividerxposition = leftdividerxposition + getWidth();
			if (x < actiondownx0) {
				movestate = PAGE_DOWN;

			} else {
				movestate = PAGE_UP;

			}

			onLayout(true, getLeft() + offsetx, getTop(), getRight() + offsetx, getBottom());

			break;

		case MotionEvent.ACTION_UP:
			doclickListener(x);
			Boolean hasmoved = Math.abs(x - actiondownx0) > 5;
			if (hasmoved) {
				if (x < actiondownx0) {
					movestate = PAGE_DOWN;
					doPagedownAnimation();
				} else {
					movestate = PAGE_UP;
					doPageUpAnimation();

				}
			} else {

				if (x > getWidth() / 2) {
					movestate = PAGE_DOWN;
					doPagedownAnimation();
				} else {
					movestate = PAGE_UP;
					doPageUpAnimation();
				}
			}

			break;

		default:
			break;
		}

		return true;
	}

	/**
	 * 点击事件监听
	 * 
	 * @param x
	 */
	private void doclickListener(int x) {
		if (isckickeven()) {
			if (bannerClickListenr == null)
				return;
			if (x < leftdividerxposition) {
				if (prepagedata != null) {
					bannerClickListenr.onckick(prepagedata.getIndex(), prepagedata);
				}
			} else {

				if (nextpagedata != null) {
					bannerClickListenr.onckick(nextpagedata.getIndex(), nextpagedata);
				}

			}
		}
	}

	/**
	 * 用于判断是否是执行了点击事件
	 * 
	 * @return
	 */
	private Boolean isckickeven() {
		long timeoffset = System.currentTimeMillis() - downtime;
		downtime = 0;
		return timeoffset > 30 && timeoffset < 100;
	}

	/**
	 * 执行上翻动画
	 */
	private void doPageUpAnimation() {
		final ValueAnimator valueAnimator = getValueAnimator();
		valueAnimator.setDuration(1000);
		final int startposition = leftdividerxposition;
		final int distance = getWidth() - startposition;

		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (Float) animation.getAnimatedValue();
				leftdividerxposition = (int) (startposition + distance * value);
				rightdividerxposition = leftdividerxposition + getWidth();

				if (leftdividerxposition >= getWidth()) {
					valueAnimator.cancel();
					mBannerViewdata = mBannerViewdata.getPreviewdata();
					committchange();
					onAimation = false;
					return;
				}
				onLayout(true, 0, 0, 0, 0);
			}

		});
		valueAnimator.start();

	}

	private void committchange() {
		if (mBannerViewdata != null) {
			prepagedata = mBannerViewdata.getPreviewdata();
			nextpagedata = mBannerViewdata.getNextviewdata();
			leftdividerxposition = 0;
			rightdividerxposition = leftdividerxposition + getWidth();
		}
	}

	/**
	 * 执行下翻动画
	 */
	private void doPagedownAnimation() {
		final ValueAnimator valueAnimator = getValueAnimator();

		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (Float) animation.getAnimatedValue();
				rightdividerxposition = (int) (rightdividerxposition * (1 - value));
				leftdividerxposition = rightdividerxposition - getWidth();
				if (rightdividerxposition <= 0) {
					valueAnimator.cancel();
					mBannerViewdata = mBannerViewdata.getNextviewdata();
					committchange();
					onAimation = false;
					return;
				}
				onLayout(true, 0, 0, 0, 0);
			}

		});
		valueAnimator.start();

	}

	private ValueAnimator getValueAnimator() {
		final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
		valueAnimator.setDuration(3000);

		valueAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {

			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});

		onAimation = true;
		return valueAnimator;
	}

	private Boolean checkBannersecurity(BannerViewdata bannerViewdata) {
		return bannerViewdata != null;
	}

	@Override
	public void addView(View child) {
		super.addView(child);
	}

	@Override
	protected void onDetachedFromWindow() {

		super.onDetachedFromWindow();
		timetask.cancel();
		handler = null;

	}

	/**
	 * 设置点击事件监听
	 * 
	 * @param bannerClickListenr
	 */
	public void setOnBannerClickListenr(BannerClickListenr bannerClickListenr) {
		this.bannerClickListenr = bannerClickListenr;
	}

	public interface BannerClickListenr {
		/**
		 * @param index
		 *            这个其实是点击的bannerViewdata的index
		 * @param bannerViewdata
		 *            这个其实是点击的bannerViewdata
		 */
		public void onckick(int index, BannerViewdata bannerViewdata);
	}
}
