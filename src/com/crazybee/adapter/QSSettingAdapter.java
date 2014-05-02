package com.crazybee.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazybee.data.QuickSettingItem;
import com.crazybee.data.ViewHolder;
import com.crazybee.quicksetting.QuickSettingTools;
import com.crazybee.quicksetting.R;

public class QSSettingAdapter extends BaseAdapter{
	private Context context;
	private List<QuickSettingItem> list;

	public QSSettingAdapter(Context context, List<QuickSettingItem> quickSettingList) {

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
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.qs_setting_item, null);
			mHolder = new ViewHolder();
			convertView.setTag(mHolder);
			mHolder.groupItem = (TextView) convertView.findViewById(R.id.text_item);
			mHolder.groupItem2 = (ImageView)convertView.findViewById(R.id.image_item);

		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
	
		
		int nameid = list.get(position).getNameId();
		String name = context.getString(nameid);
		QuickSettingTools tools = new QuickSettingTools(context);
		int status = tools.getStatus(nameid);
		//int imageid = QuickSettingTools.getIconId(nameid, status);
		int imageid = tools.getIconId(nameid, status);
		mHolder.groupItem.setText(name);
		mHolder.groupItem2.setImageDrawable(context.getResources().getDrawable(imageid));
		
		mHolder.groupItem.setTag(list.get(position));
		mHolder.groupItem.getTag();
		return convertView;
	}
}
