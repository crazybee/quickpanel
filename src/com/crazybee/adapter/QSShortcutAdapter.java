package com.crazybee.adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazybee.data.ViewHolder;
import com.crazybee.quicksetting.R;

public class QSShortcutAdapter extends BaseAdapter{
	private Context context;
	private List<ResolveInfo> list;
	private String appName;
	private Drawable icon;

	public QSShortcutAdapter(Context context, List<ResolveInfo> shortcuts) {
		this.context = context;
		this.list = shortcuts;
		for (int i = 0; i < list.size(); i++) {
			String packageName = list.get(i).activityInfo.packageName;
			Log.d("SD", "packageName:" + packageName);
		}
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
		ViewHolder shortCutHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.quick_setting_select_itme, null);
			shortCutHolder = new ViewHolder();
			convertView.setTag(shortCutHolder);
			shortCutHolder.groupItem = (TextView) convertView.findViewById(R.id.text_item);
			shortCutHolder.groupItem2 = (ImageView) convertView.findViewById(R.id.image_item);

		} else {
			shortCutHolder = (ViewHolder) convertView.getTag();
		}

		String packageName = list.get(position).activityInfo.packageName;
		Log.d("SD", "packageName:" + packageName);
		PackageManager manager = context.getPackageManager();
		ApplicationInfo info = null;
		try {
			info = manager.getApplicationInfo(packageName,
					PackageManager.GET_META_DATA);
			appName = list.get(position).loadLabel(manager).toString();
			icon = info.loadIcon(manager);
			packageName = info.packageName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		shortCutHolder.groupItem.setText(appName);
		shortCutHolder.groupItem2.setImageDrawable(icon);
		shortCutHolder.groupItem.setTag(info);
		return convertView;
	}
}
