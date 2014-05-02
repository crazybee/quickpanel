package com.crazybee.adapter;

import java.util.List;

import com.crazybee.data.QuickSettingItem;
import com.crazybee.data.ViewHolder;
import com.crazybee.database.MyDBHelper;
import com.crazybee.quicksetting.QuickSettingActivity;
import com.crazybee.quicksetting.QuickSettingTools;
import com.crazybee.quicksetting.R;

import android.util.Log;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class QuickSettingAdapter extends BaseAdapter{

	private Context context;
	private List<QuickSettingItem> list;

	public QuickSettingAdapter(Context context, List<QuickSettingItem> quickSettingList) {

		this.context = context;
		this.list = quickSettingList;

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
		ViewHolder Holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.quick_setting_item, null);
			Holder = new ViewHolder();
			convertView.setTag(Holder);
			Holder.groupItem = (TextView) convertView.findViewById(R.id.text_item);
			Holder.groupItem2 = (ImageView)convertView.findViewById(R.id.image_item);

		} else {
			Holder = (ViewHolder) convertView.getTag();
		}
	
		int type = list.get(position).getImageType();
		int nameid = 0;
		String name = null;
		if(type == 1){
			nameid = list.get(position).getNameId();
			name = context.getString(nameid);
			QuickSettingTools tools = new QuickSettingTools(context);
			int status = tools.getStatus(nameid);
			//int imageid = QuickSettingTools.getIconId(nameid, status);
			int imageid = tools.getIconId(nameid, status);
			Holder.groupItem.setText(name);
			Holder.groupItem2.setImageDrawable(context.getResources().getDrawable(imageid));
		}else {
			String appName = null;
			Drawable icon = null;
			String packagename = list.get(position).getpackageName();
			PackageManager manager = context.getPackageManager();
			ApplicationInfo info;
			try {
				info = manager.getApplicationInfo(packagename,
									PackageManager.GET_META_DATA);
				appName = info.loadLabel(manager).toString();
				icon = info.loadIcon(manager);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Holder.groupItem.setText(appName);
			Holder.groupItem2.setImageDrawable(icon);
		}
		
		
		Holder.groupItem.setTag(list.get(position));
		Holder.groupItem.getTag();
		return convertView;
	}

}
