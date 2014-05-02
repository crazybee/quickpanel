package com.crazybee.database;

import java.io.ByteArrayOutputStream;

import android.provider.BaseColumns;
import android.util.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.crazybee.quicksetting.R;

public class MyDBHelper extends SQLiteOpenHelper {

	private static final int VERSION = 9;
	private static final String TABLE_NAME = "quicksetting_list";
	private Context mContext;

	public MyDBHelper(Context context, String name,
			SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public MyDBHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	public MyDBHelper(Context context, String name, int version2) {
		// TODO Auto-generated constructor stub
		this(context, name, null, VERSION);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table quicksetting_list(nameid integer, packagename varchar(200), classname varchar(200), imagetype integer)");
		initDataBase(db, mContext);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		// TODO Auto-generated method stub

	}

	// 将转换后的图片存入到数据库中
	private void initDataBase(SQLiteDatabase db, Context context) {

		int[] text = { R.string.gt_qs_bright,R.string.gt_qs_cmcc,
				 R.string.gt_qs_electricity, R.string.gt_qs_wifi,
				R.string.gt_qs_autowhirl, R.string.gt_qs_bluetooth,
				R.string.gt_qs_gps };

		String name = null;
		ContentValues cv = new ContentValues();
		for (int i = 0; i < text.length; i++) {
			cv.clear();
			name = context.getString(text[i]);
			cv.put("imagetype", 1);
			cv.put("nameid", text[i]);
			db.insert(TABLE_NAME, null, cv);
		}

	}

	public static class PictureColumns implements BaseColumns {
		public static final String PICTURE = "icon";
	}

}
