package com.crazybee.adapter;

import java.util.ArrayList;

import com.crazybee.data.AppInfo;
import com.crazybee.data.ViewHolder;
import com.crazybee.quicksetting.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class QSAllAppAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<ArrayList<AppInfo>> list;
	private PackageManager mPM;
	
	public QSAllAppAdapter(Context context, ArrayList<ArrayList<AppInfo>> allApps){
		this.context = context;
		this.list = allApps;
		mPM = context.getPackageManager();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewgroup) {
		// TODO Auto-generated method stub
		ViewHolder allAppHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.quick_setting_select_itme, null);
			allAppHolder = new ViewHolder();
			convertView.setTag(allAppHolder);
			allAppHolder.groupItem = (TextView) convertView.findViewById(R.id.text_item);
			allAppHolder.groupItem2 = (ImageView) convertView.findViewById(R.id.image_item);

		} else {
			allAppHolder = (ViewHolder) convertView.getTag();
		}

		AppInfo app = list.get(position).get(0);
		String appName = app.getName();
		Drawable appIcon = getAppIcon(app.getPackageInfo());
		String packagename = app.getPackageName();
		
		allAppHolder.groupItem.setText(appName);
		allAppHolder.groupItem2.setImageDrawable(appIcon);
		
		//allAppHolder.groupItem.setTag(appName);
		allAppHolder.groupItem.setTag(app);
		return convertView;
	}
	private Drawable getAppIcon(PackageInfo info) {
		return mPM.getApplicationIcon(info.applicationInfo);
	}
}
