package com.crazybee.quicksetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crazybee.adapter.MyPagerAdapter;
import com.crazybee.adapter.QSAllAppAdapter;
import com.crazybee.adapter.QSSettingAdapter;
import com.crazybee.adapter.QSShortcutAdapter;
import com.crazybee.data.AppInfo;
import com.crazybee.data.CommonMarks;
import com.crazybee.data.QuickSettingItem;
import com.crazybee.data.ViewHolder;
import com.crazybee.database.MyDBHelper;
import com.crazybee.quicksetting.R;

public class QuickSettingSelectActivity extends Activity implements
		OnItemClickListener {

	private ViewPager mPager;
	private List<View> listViews; 
	private ImageView cursor;
	private TextView t1, t2, t3;
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	private GridView setting;
	private GridView allApp;
	private GridView shortCut;
	private QSSettingAdapter Adapter;
	private List<QuickSettingItem> quicksettingtlist = new ArrayList<QuickSettingItem>();
	private ArrayList<ArrayList<AppInfo>> mSortApps;
													
	private QSAllAppAdapter mAdapter;
	private QSShortcutAdapter shortcutAdapter;
	private List<ResolveInfo> shortcuts; 
	private int[] gtbehaviorName;

	private Handler mHandler;
	private ViewHolder holder;
	private int imageId;
	private int nameId;
	private String name;

	private static final int MSG__APPS_FAIL = 1;
	private static final int MSG__APPS_SUCC = 0;

	private static final String TABLE_NAME = "quicksetting_list";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quick_setting_select_list);

		getSettingData();
		InitImageView();
		InitTextView();
		InitViewPager();

		loadAppData();
		getShortCutData();
		initShortCutList();

		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG__APPS_SUCC:
					initAllAppList();
					break;
				case MSG__APPS_FAIL:
					break;
				}
				super.handleMessage(msg);
			}
		};

		initSettingList();
	}

	private void initAllAppList() {
		// TODO Auto-generated method stub
		if (mSortApps != null) {
			allApp = (GridView) listViews.get(1).findViewById(
					R.id.all_app_gridview);
			mAdapter = new QSAllAppAdapter(this, mSortApps);
			allApp.setAdapter(mAdapter);
			allApp.setOnItemClickListener(this);
		}
	}

	private void initSettingList() {
		// TODO Auto-generated method stub
		setting = (GridView) listViews.get(0).findViewById(
				R.id.setting_gridview);
		Adapter = new QSSettingAdapter(this, quicksettingtlist);
		setting.setAdapter(Adapter);
		setting.setOnItemClickListener(this);
	}

	private void initShortCutList() {
		// TODO Auto-generated method stub
		shortCut = (GridView) listViews.get(2).findViewById(
				R.id.shortcut_gridview);
		shortcutAdapter = new QSShortcutAdapter(this, shortcuts);
		shortCut.setAdapter(shortcutAdapter);
		shortCut.setOnItemClickListener(this);
	}

	private void getShortCutData() {
		// TODO Auto-generated method stub
		PackageManager mPackageManager = getPackageManager();
		Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
		shortcuts = mPackageManager.queryIntentActivities(shortcutsIntent, 0);
	}

	private void loadAppData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message message = new Message();
				ArrayList<AppInfo> mApps = getAllApps();
				if (mApps == null)
					message.what = MSG__APPS_FAIL;
				else {
					AppComparator comparator = new AppComparator();
					Collections.sort(mApps, comparator);
			
					mSortApps = new ArrayList<ArrayList<AppInfo>>();
					ArrayList<AppInfo> arrayList = new ArrayList<AppInfo>();
					String previewStr = " ";
					for (int i = 0; i < mApps.size(); i++) {
						if (arrayList == null)
							arrayList = new ArrayList<AppInfo>();
						// PackageInfo info = mApps.get(i);
						String appName = mApps.get(i).getName();
						if (appName == null)
							break;
						String currentStr = CommonMarks.getAlpha(appName);
						if (currentStr == null)
							break;
						if (i != 0 && !previewStr.equalsIgnoreCase(currentStr)) {
							mSortApps.add(mSortApps.size(), arrayList);
							arrayList = new ArrayList<AppInfo>();
							arrayList.add(mApps.get(i));
						} else {
							arrayList.add(mApps.get(i));
						}
						previewStr = currentStr;
					}
					message.what = MSG__APPS_SUCC;
				}
				mHandler.sendMessage(message);
			}
		}).start();
	}

	private ArrayList<AppInfo> getAllApps() {
		// TODO Auto-generated method stub
		List<AppInfo> apps = new ArrayList<AppInfo>();
	
		PackageManager packageM = getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveInfos = packageM.queryIntentActivities(
				mainIntent, 0);
		Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(
				packageM));
		for (int i = 0; i < resolveInfos.size(); i++) {
			// customs applications
			// apps.add(pak);
			AppInfo app = new AppInfo();
			app.setName(resolveInfos.get(i).loadLabel(packageM).toString());
			app.setPackageName(resolveInfos.get(i).activityInfo.packageName);
			app.setSelected(false);
			try {
				app.setPackageInfo(packageM.getPackageInfo(
						resolveInfos.get(i).activityInfo.packageName,
						PackageManager.GET_ACTIVITIES));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			apps.add(app);

		}
		return (ArrayList<AppInfo>) apps;
	}


	class AppComparator implements Comparator<AppInfo> {

		@Override
		public int compare(AppInfo app1, AppInfo app2) {
			String app1Name = app1.getName();
			String app2Name = app2.getName();
			return app1Name.compareTo(app2Name);
		}
	}

	private void InitViewPager() {
		// TODO Auto-generated method stub
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.qs_setting_gridview, null));

		listViews.add(mInflater.inflate(R.layout.qs_all_app_gridview, null));
		listViews.add(mInflater.inflate(R.layout.qs_shortcut_gridview, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));

		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}


	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;
		int two = one * 2;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private void InitTextView() {
		// TODO Auto-generated method stub
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
	}


	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	private void InitImageView() {
		// TODO Auto-generated method stub
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	private void getSettingData() {
		// TODO Auto-generated method stub

		int[] settingName = {
				R.string.gt_qs_airport,
				// R.string.gt_qs_silence,
				//R.string.gt_qs_vibrate,
				R.string.gt_qs_app_manage,
				R.string.gt_qs_task_manage, R.string.gt_qs_local,
				R.string.gt_qs_putin, R.string.qs_torch, };

		for (int i = 0; i < settingName.length; i++) {
			QuickSettingItem list = new QuickSettingItem();
			list.setNameId(settingName[i]);
			list.setImageType(1);
			quicksettingtlist.add(list);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		// TODO Auto-generated method stub

		RelativeLayout mView = (RelativeLayout) view;
		ImageView childImageView = null;
		TextView childTextView = null;
		View childview = null;
		for (int i = 0; i < mView.getChildCount(); i++) {
			childview = mView.getChildAt(i);
			if (childview instanceof ImageView) {
				childImageView = (ImageView) childview;
			}
			if (childview instanceof TextView) {
				childTextView = (TextView) childview;
			}
		}

		Drawable childDrawable = childImageView.getDrawable();
		String childText = childTextView.getText().toString();

		MyDBHelper databaseOpenHelper = new MyDBHelper(
				QuickSettingSelectActivity.this, "quicksetting.db", 9);
		SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();

		holder = (ViewHolder) view.getTag();
		Object tag = holder.groupItem.getTag();
		String appName = "";
		String packagename = "";
		String classname = "";

		if (tag instanceof ApplicationInfo) {
			appName = holder.groupItem.getText().toString();
			ApplicationInfo applicationInfo = (ApplicationInfo) tag;
			packagename = applicationInfo.packageName;
			classname = applicationInfo.className;

		} else if (tag instanceof AppInfo) {
			AppInfo appInfo = (AppInfo) tag;
			appName = appInfo.getName();
			packagename = appInfo.getPackageName();
			classname = appInfo.getPackageInfo().applicationInfo.className;
		} else if (tag instanceof QuickSettingItem) {
			// appName = (String) tag;
			QuickSettingItem info = (QuickSettingItem) tag;
			nameId = info.getNameId();
		}

		if (arg0.getId() == R.id.setting_gridview) {
			cv.put("nameid", nameId);
			cv.put("imagetype", 1);
			db.insert(TABLE_NAME, null, cv);
		} else {
			cv.put("packagename", packagename);
			cv.put("classname", classname);
			cv.put("imagetype", 2);
			db.insert(TABLE_NAME, null, cv);
		}
		db.close();

		Intent intent = new Intent(QuickSettingSelectActivity.this,
				QuickSettingActivity.class);
		startActivity(intent);

		finish();
	}
}
