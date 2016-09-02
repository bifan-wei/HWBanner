package com.example.bannerlib;

import com.example.bannerlib.BannerViewGroup.BannerClickListenr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BannerViewGroup group = (BannerViewGroup) findViewById(R.id.group);
		BannerViewdata data0 = getdata(0, true, false, R.drawable.pic1);
		BannerViewdata data1 = getdatafromXml(1, false, false);
		BannerViewdata data3 = getdata(2, false, true, R.drawable.pic2);

		data0.setNextviewdata(data1);
		data0.setPreviewdata(data3);
		data1.setNextviewdata(data3);
		data1.setPreviewdata(data0);
		data3.setPreviewdata(data1);
		data3.setNextviewdata(data0);

		group.setBannerViewData(data0); // 设置数据，设置的是最开始的数据

		group.setOnBannerClickListenr(new BannerClickListenr() {// 设置点击监听

			@Override
			public void onckick(int index, BannerViewdata bannerViewdata) {
				Toast.makeText(MainActivity.this, "test" + index, 0).show();
			}
		});
	}

	private BannerViewdata getdata(int index, Boolean isfrst, Boolean islast, int resource) {
		return new BannerViewdata(MainActivity.this, index, null, null, getImage(resource), isfrst, islast);
	}

	private ImageView getImage(int rsoucerce) {
		ImageView imageView = new ImageView(this);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageView.setBackgroundResource(rsoucerce);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "test", 0).show();
			}
		});
		
		return imageView;
	}

	private BannerViewdata getdatafromXml(int index, Boolean isfrst, Boolean islast) {

		LinearLayout layout = (LinearLayout) LinearLayout.inflate(MainActivity.this, R.layout.billboard_detail_layout,
				null);
		LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(p);

		return new BannerViewdata(MainActivity.this, index, null, null, layout, isfrst, islast);
	}

}
