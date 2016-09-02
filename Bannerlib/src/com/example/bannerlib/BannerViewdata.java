package com.example.bannerlib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

/**
 * @author huangwei 2016年9月1日下午5:09:16
 * 
 */
public class BannerViewdata {

	private int index;
	private BannerViewdata previewdata;
	private BannerViewdata nextviewdata;
	private View view;
	private Boolean isfirst = false;
	private Boolean islast = false;

	/**
	 * @param context
	 * @param index
	 *            下标
	 * @param previewdata
	 *            前一个数据
	 * @param nextviewdata
	 *            后一个数据
	 * @param view
	 *            view，不运行为空
	 * @param isfirst
	 *            用于标识是否是第一个view
	 * @param islast
	 *            用于标识是否是最后一个view
	 */
	public BannerViewdata(Context context, int index, BannerViewdata previewdata, BannerViewdata nextviewdata,
			View view, Boolean isfirst, Boolean islast) {
		this.index = index;
		this.previewdata = previewdata;
		this.nextviewdata = nextviewdata;
		this.isfirst = isfirst;
		this.islast = islast;

		setView(view, context);

		if (view == null) {
			throw new NullPointerException();
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public BannerViewdata getPreviewdata() {
		return previewdata;
	}

	public void setPreviewdata(BannerViewdata previewdata) {
		this.previewdata = previewdata;
	}

	public BannerViewdata getNextviewdata() {
		return nextviewdata;
	}

	public void setNextviewdata(BannerViewdata nextviewdata) {
		this.nextviewdata = nextviewdata;
	}

	public View getView() {
		return view;
	}

	public void setView(View view, Context context) {
		RelativeLayout relativeLayout = new RelativeLayout(context);
		relativeLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		relativeLayout.addView(view);
		this.view = relativeLayout;
	}

	public Boolean istheFirst() {
		return isfirst;
	}

	public Boolean istheLast() {
		return islast;
	}

	public int getLength() {

		BannerViewdata first = findtheFirstViewdata();

		if (first == null)
			return 0;

		BannerViewdata next = first.getNextviewdata();

		if (next == null) {
			return 1;
		}

		if (next.islast) {
			return 2;
		}

		int length = 1;

		while (next != null) {
			length++;
			next = next.getNextviewdata();
			if (next.islast) {
				return ++length;
			}

		}

		return length;
	}

	/**
	 * @return 返回最开始的 BannerViewdata，也就是下标为0的，如果没有那么就返回null
	 */
	public BannerViewdata findtheFirstViewdata() {

		if (istheFirst()) {// 当前为0，那么就是最开始的值了
			return this;
		}

		BannerViewdata bannerViewdata = previewdata;

		while (bannerViewdata != null && !bannerViewdata.istheFirst()) {
			bannerViewdata = bannerViewdata.getPreviewdata();
		}

		return bannerViewdata;
	}

}
